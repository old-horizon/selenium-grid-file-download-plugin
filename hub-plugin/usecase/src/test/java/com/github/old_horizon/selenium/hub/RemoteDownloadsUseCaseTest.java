package com.github.old_horizon.selenium.hub;

import com.github.old_horizon.selenium.UseCaseBaseTestCase;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class RemoteDownloadsUseCaseTest extends UseCaseBaseTestCase {
    @InjectMocks
    private RemoteDownloadsUseCase target;

    @Mock
    private BaseUrlPort baseUrlPort;

    @Mock
    private HttpPort httpPort;

    @Test
    void get() throws Exception {
        target = spy(target);

        String sessionKey = "sessionKey";
        String path = "path";
        RemoteResponse expected = mock(RemoteResponse.class);
        URL baseUrl = new URL("http://node:5555/");
        String url = "url";

        when(baseUrlPort.find(sessionKey)).thenReturn(baseUrl);
        doReturn(url).when(target).createUrl(baseUrl, path);
        when(httpPort.get(url)).thenReturn(expected);

        assertThat(target.get(sessionKey, path), is(expected));

        verify(baseUrlPort).find(sessionKey);
        verify(target).createUrl(baseUrl, path);
        verify(httpPort).get(url);
    }

    @Test
    void delete() throws Exception {
        target = spy(target);

        String sessionKey = "sessionKey";
        String path = "path";
        RemoteResponse expected = mock(RemoteResponse.class);
        URL baseUrl = new URL("http://node:5555/");
        String url = "url";

        when(baseUrlPort.find(sessionKey)).thenReturn(baseUrl);
        doReturn(url).when(target).createUrl(baseUrl, path);
        when(httpPort.delete(url)).thenReturn(expected);

        assertThat(target.delete(sessionKey, path), is(expected));

        verify(baseUrlPort).find(sessionKey);
        verify(target).createUrl(baseUrl, path);
        verify(httpPort).delete(url);
    }

    @Test
    void createUrl() throws Exception {
        URL baseUrl = new URL("http://node:5555/");
        String path = "path";

        assertThat(target.createUrl(baseUrl, path), is("http://node:5555/extra/Downloads/path"));
    }
}