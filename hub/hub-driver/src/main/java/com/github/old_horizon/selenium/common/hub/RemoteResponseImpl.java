package com.github.old_horizon.selenium.common.hub;

import okhttp3.Response;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class RemoteResponseImpl implements RemoteResponse {
    private final Response response;

    public RemoteResponseImpl(Response response) {
        this.response = response;
    }

    @Override
    public int getStatus() {
        return response.code();
    }

    @Override
    public InputStream getByteStream() {
        return response.body().byteStream();
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return removeHeaders(response.headers().toMultimap(), Arrays.asList("Host", "Date", "Server"));
    }

    @Override
    public void close() {
        response.close();
    }

    static Map<String, List<String>> removeHeaders(Map<String, List<String>> headers, List<String> names) {
        return headers.entrySet()
                .stream()
                .filter(e -> names.stream().noneMatch(e.getKey()::equalsIgnoreCase))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
