package com.github.old_horizon.selenium.hub;

import com.github.old_horizon.selenium.common.HttpServletExceptionMapper;
import com.google.common.io.ByteStreams;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class Downloads extends RegistryBasedServlet {
    private RemoteDownloadsUseCase useCase;
    private HttpServletExceptionMapper exceptionMapper = new HttpServletExceptionMapper.Builder()
            .addMapping(SessionNotFoundException.class, SC_NOT_FOUND)
            .addMapping(IllegalArgumentException.class, SC_BAD_REQUEST)
            .build();

    public Downloads() {
        super(null);
    }

    public Downloads(GridRegistry registry) {
        super(registry);
    }

    @Override
    public void init() {
        useCase = new RemoteDownloadsUseCase(new BaseUrlDriver(getRegistry()), new HttpDriver());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        invoke(request, response, useCase::get);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        invoke(request, response, useCase::delete);
    }

    void invoke(HttpServletRequest request, HttpServletResponse response, UseCaseMethod method) throws IOException {
        exceptionMapper.apply(() -> {
            Optional<String> sessionKey = getSessionKey(request);
            if (sessionKey.isPresent()) {
                RemoteResponse remoteResponse = method.invoke(sessionKey.get(), getPath(request));
                sendResponse(response, remoteResponse);
            } else {
                throw new IllegalArgumentException("session key is not specified");
            }
        }, response);
    }

    Optional<String> getSessionKey(HttpServletRequest request) {
        List<String> pathInfo = splitPathInfo(request);
        if (pathInfo.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pathInfo.get(0));
    }

    String getPath(HttpServletRequest request) {
        List<String> pathInfo = splitPathInfo(request);
        int size = pathInfo.size();
        if (size < 2) {
            return "";
        }
        return String.join("/", pathInfo.subList(1, size));
    }

    void sendResponse(HttpServletResponse response, RemoteResponse remoteResponse) throws IOException {
        try (RemoteResponse remote = remoteResponse;
             InputStream byteStream = remote.getByteStream()) {
            response.setStatus(remote.getStatus());
            addHeaders(response, remoteResponse);
            copy(byteStream, response.getOutputStream());
        }
    }

    void addHeaders(HttpServletResponse response, RemoteResponse remoteResponse) {
        remoteResponse.getHeaders()
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue()
                        .stream()
                        .map(v -> new SimpleEntry<>(e.getKey(), v)))
                .forEach(e -> response.addHeader(e.getKey(), e.getValue()));
    }

    @SuppressWarnings("UnstableApiUsage")
    void copy(InputStream in, OutputStream out) throws IOException {
        ByteStreams.copy(in, out);
    }

    private List<String> splitPathInfo(HttpServletRequest request) {
        return Optional.ofNullable(request.getPathInfo()).map(pathInfo ->
                Arrays.stream(pathInfo.split("/"))
                        .filter(p -> !p.isEmpty())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @FunctionalInterface
    interface UseCaseMethod {
        RemoteResponse invoke(String sessionKey, String path) throws IOException;
    }
}
