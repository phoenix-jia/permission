package com.famesmart.privilege.service;

import com.famesmart.privilege.config.RabbitMQConfig;
import com.famesmart.privilege.entity.Users;
import com.famesmart.privilege.entity.bo.FromMessageBO;
import com.famesmart.privilege.entity.bo.ToMessageBO;
import com.famesmart.privilege.security.UserDetailsCustom;
import com.famesmart.privilege.util.JsonUtils;
import com.famesmart.privilege.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class WsService {

    @Autowired
    private UserCommsService userCommsService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final Map<String, List<WebSocketSession>> map = new ConcurrentHashMap<>();

    public void afterConnectionEstablished(WebSocketSession session) {

        String username = getUsername(session);
        log.info("new connection from user: {}", username);

        if (!map.containsKey(username)) {
            map.put(username, new CopyOnWriteArrayList<>());
        }
        List<WebSocketSession> sessions = map.get(username);
        sessions.add(session);
    }

    private String getUsername(WebSocketSession session) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) session.getPrincipal();
        assert token != null;
        UserDetailsCustom userDetailsCustom = (UserDetailsCustom) token.getPrincipal();
        return userDetailsCustom.getUsername();
    }

    public void afterConnectionClosed(WebSocketSession session) {
        String username = getUsername(session);
        List<WebSocketSession> sessions = map.get(username);
        sessions.remove(session);
        log.info("disconnection from user: {}", username);
    }

    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String fromUser = getUsername(session);
        log.info("user {} send message: {}", fromUser, message.getPayload());

        ToMessageBO toMessageBO = JsonUtils.jsonToObject(message.getPayload(), ToMessageBO.class);
        if (toMessageBO == null
                || (StringUtils.isBlank(toMessageBO.getToUser())
                && StringUtils.isBlank(toMessageBO.getToComm()))) {

            String payload = JsonUtils.objectToJson(Result.error("invalid message"));
            assert payload != null;
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        toMessageBO.setFromUser(fromUser);
        sendMessage(toMessageBO);
    }

    private void sendMessage(ToMessageBO toMessageBO) {
        String toUser = toMessageBO.getToUser();
        String toComm = toMessageBO.getToComm();
        String fromUser = toMessageBO.getFromUser();

        FromMessageBO fromMessageBO = new FromMessageBO(
                fromUser, toMessageBO.getType(), toMessageBO.getData());
        String text = JsonUtils.objectToJson(fromMessageBO);
        assert text != null;
        TextMessage textMessage = new TextMessage(text);

        if (toUser.equals("*")) {
            if (toComm.equals("*")) {
                for (Map.Entry<String, List<WebSocketSession>> entry : map.entrySet()) {
                    if (!entry.getKey().equals(fromUser)) {
                        entry.getValue().forEach(s -> sendTextMessage(s, textMessage));
                    }
                }
                return;
            }

            if (StringUtils.isBlank(toComm)) {
                return;
            }

            List<Users> users = userCommsService.getUserByCommCode(toComm);
            for (Users user: users) {
                String username = user.getUsername();
                if (username.equals(fromUser)) {
                    continue;
                }
                List<WebSocketSession> sessions = map.get(username);
                if (sessions == null || sessions.isEmpty()) {
                    log.info("session not found for user {}", username);
                    continue;
                }
                sessions.forEach(s -> sendTextMessage(s, textMessage));
            }
            return;
        }

        if (StringUtils.isNotBlank(toUser) || !fromUser.equals(toUser)) {
            List<WebSocketSession> sessions = map.get(toUser);
            if (sessions == null || sessions.isEmpty()) {
                log.info("session not found for user {}", toUser);
            } else {
                map.get(toUser).forEach(s -> sendTextMessage(s, textMessage));
            }
        }
    }

    public void push(ToMessageBO toMessageBO) {
        String message = JsonUtils.objectToJson(toMessageBO);

        if (StringUtils.isBlank(message)) {
            throw new RuntimeException("invalid ws message to push");
        }

        rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName, toMessageBO.getType(), message);
    }

    @RabbitListener(queues = RabbitMQConfig.queueName)
    public void receivedMessage(String message) {
        ToMessageBO toMessageBO = JsonUtils.jsonToObject(message, ToMessageBO.class);

        if (toMessageBO == null) {
            throw new RuntimeException("invalid ws message received");
        }

        sendMessage(toMessageBO);
    }

    public void sendTextMessage(WebSocketSession session, TextMessage textMessage) {
        try {
            session.sendMessage(textMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
