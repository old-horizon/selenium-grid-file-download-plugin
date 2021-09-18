package com.github.old_horizon.selenium.common;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class HttpServletExceptionMapper {
    Map<Class<? extends Exception>, Integer> mapping;
    private HttpServletResponseUtil util = HttpServletResponseUtil.getInstance();
    private Logger logger = Logger.getLogger(HttpServletExceptionMapper.class.getName());

    HttpServletExceptionMapper(Map<Class<? extends Exception>, Integer> mapping) {
        this.mapping = mapping;
    }

    public void apply(ThrowingRunnable runnable, HttpServletResponse response) throws IOException {
        try {
            runnable.run();
        } catch (Exception e) {
            String message = e.getMessage();
            logger.log(Level.WARNING, message, e);
            response.setStatus(mapping.getOrDefault(e.getClass(), SC_INTERNAL_SERVER_ERROR));
            util.sendJson(response, toMessageMap(message));
        }
    }

    <T extends Exception> Map<String, String> toMessageMap(String message) {
        return util.singleEntryMap("message", message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpServletExceptionMapper that = (HttpServletExceptionMapper) o;
        return mapping.equals(that.mapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapping);
    }

    public static class Builder {
        Map<Class<? extends Exception>, Integer> mapping = new HashMap<>();

        public Builder addMapping(Class<? extends Exception> exceptionClass, int status) {
            mapping.put(exceptionClass, status);
            return this;
        }

        public HttpServletExceptionMapper build() {
            return new HttpServletExceptionMapper(mapping);
        }
    }
}
