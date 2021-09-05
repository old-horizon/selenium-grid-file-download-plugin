package com.github.old_horizon.selenium;

import org.openqa.selenium.json.Json;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class HttpServletResponseUtil {
    private static final HttpServletResponseUtil INSTANCE = new HttpServletResponseUtil();

    private Json json = new Json();

    private HttpServletResponseUtil() {
    }

    public static HttpServletResponseUtil getInstance() {
        return INSTANCE;
    }

    public void sendJson(HttpServletResponse response, Object object) throws IOException {
        String json = this.json.toJson(object);
        response.setHeader("Content-Type", "application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(json);
        }
    }

    public <T> Map<String, T> singleEntryMap(String key, T value) {
        Map<String, T> map = new TreeMap<>();
        map.put(key, value);
        return map;
    }
}
