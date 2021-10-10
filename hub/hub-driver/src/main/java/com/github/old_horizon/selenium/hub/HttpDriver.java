package com.github.old_horizon.selenium.hub;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpDriver implements HttpPort {
    private final OkHttpClient client;

    public HttpDriver() {
        client = new OkHttpClient.Builder().build();
    }

    @Override
    public RemoteResponse get(String url) throws IOException {
        return method(url, "GET");
    }

    @Override
    public RemoteResponse delete(String url) throws IOException {
        return method(url, "DELETE");
    }

    private RemoteResponseImpl method(String url, String method) throws IOException {
        Request request = new Request.Builder().url(url).method(method, null).build();
        Response response = client.newCall(request).execute();
        return new RemoteResponseImpl(response);
    }
}
