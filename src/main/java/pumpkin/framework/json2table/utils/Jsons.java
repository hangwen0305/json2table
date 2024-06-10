package pumpkin.framework.json2table.utils;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jsons {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER;

    static {
        DEFAULT_OBJECT_MAPPER = new ObjectMapper();
    }

    public static <T> T toJavaObject(final String jsonString, final Class<T> type) {
        return toJavaObject(jsonString, type, DEFAULT_OBJECT_MAPPER);
    }

    public static <T> T toJavaObject(final String jsonString, final Class<T> type, final ObjectMapper objectMapper) {
        Asserts.hasArg(jsonString, "parameter 'jsonString' is required");
        Asserts.hasArg(type, "parameter 'type' is required");
        Asserts.hasArg(objectMapper, "parameter 'objectMapper' is required");

        try {
            return objectMapper.readValue(jsonString, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonString(final Object obj, final ObjectMapper objectMapper) {
        return toJsonString(obj, objectMapper, false);
    }

    public static String toJsonString(final Object obj,
                                      final ObjectMapper objectMapper,
                                      final boolean throwException) {
        if (obj == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            if (throwException) {
                throw new RuntimeException(e);
            } else {
                return obj.toString();
            }
        }
    }

    public static String toJsonString(final Object obj) {
        return toJsonString(obj, DEFAULT_OBJECT_MAPPER);
    }

    public static String toJsonString(final Object obj, final boolean throwException) throws JsonProcessingException {
        return toJsonString(obj, DEFAULT_OBJECT_MAPPER, throwException);
    }

    public static Map<String, Object> toMap(final String jsonString) {
        //noinspection unchecked
        return toJavaObject(jsonString, Map.class);
    }

}
