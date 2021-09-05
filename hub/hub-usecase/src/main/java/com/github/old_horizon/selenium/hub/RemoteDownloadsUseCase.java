package com.github.old_horizon.selenium.hub;

import java.io.IOException;
import java.net.URL;

public class RemoteDownloadsUseCase {
    private final BaseUrlPort baseUrlPort;
    private final HttpPort httpPort;

    public RemoteDownloadsUseCase(BaseUrlPort baseUrlPort, HttpPort httpPort) {
        this.baseUrlPort = baseUrlPort;
        this.httpPort = httpPort;
    }

    public RemoteResponse get(String sessionKey, String path) throws IOException {
        URL baseUrl = baseUrlPort.find(sessionKey);
        return httpPort.get(createUrl(baseUrl, path));
    }

    public RemoteResponse delete(String sessionKey, String path) throws IOException {
        URL baseUrl = baseUrlPort.find(sessionKey);
        return httpPort.delete(createUrl(baseUrl, path));
    }

    String createUrl(URL baseUrl, String path) {
        return String.format("%s://%s:%d/extra/Downloads/%s", baseUrl.getProtocol(), baseUrl.getHost(),
                baseUrl.getPort(), path);
    }
}
