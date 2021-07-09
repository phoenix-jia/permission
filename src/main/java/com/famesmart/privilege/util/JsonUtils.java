package com.famesmart.privilege.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String objectToJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static <T> T jsonToObject(String jsonData, Class<T> type) {
        try {
            return objectMapper.readValue(jsonData, type);
        } catch (Exception e) {
            return null;
        }

    }

    public static <T> List<T> jsonToList(String jsonData, Class<T> type) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, type);
        try {
            return objectMapper.readValue(jsonData, javaType);
        } catch (Exception e) {
            return null;
        }
    }
}
