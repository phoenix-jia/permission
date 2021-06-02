package com.famesmart.privilege.handler;

import com.famesmart.privilege.service.WsService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class WsHandler extends TextWebSocketHandler {

    @Autowired
    private WsService wsService;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        wsService.afterConnectionEstablished(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        wsService.afterConnectionClosed(session);
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        wsService.handleTextMessage(session, message);
    }

}
