package com.github.old_horizon.selenium.common;

import com.google.common.io.ByteStreams;
import org.openqa.selenium.json.Json;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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

    public void sendBinary(HttpServletResponse response, byte[] bytes) throws IOException {
        try (ServletOutputStream out = response.getOutputStream()) {
            copy(toInputStream(bytes), out);
        }
    }

    public <T> Map<String, T> singleEntryMap(String key, T value) {
        Map<String, T> map = new TreeMap<>();
        map.put(key, value);
        return map;
    }

    @SuppressWarnings("UnstableApiUsage")
    void copy(InputStream in, OutputStream out) throws IOException {
        ByteStreams.copy(in, out);
    }

    ByteArrayInputStream toInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }
}
