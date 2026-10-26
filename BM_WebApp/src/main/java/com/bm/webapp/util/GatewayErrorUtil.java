package com.bm.webapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

/**
 * Every backend service's GlobalExceptionHandler returns a body shaped like:
 * { "timestamp": ..., "status": ..., "error": "...", "message": "..." }
 * This pulls out the "message" field so it can be shown as a friendly alert.
 */
public final class GatewayErrorUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private GatewayErrorUtil() {}

    public static String extractMessage(RestClientResponseException ex) {
        try {
            String body = ex.getResponseBodyAsString();
            if (body == null || body.isBlank()) {
                return ex.getMessage();
            }
            Map<?, ?> map = MAPPER.readValue(body, Map.class);
            Object message = map.get("message");
            return message != null ? message.toString() : ex.getMessage();
        } catch (Exception parseFailure) {
            return "Something went wrong. Please try again.";
        }
    }
}
