package com.github.old_horizon.selenium.hub;

import com.github.old_horizon.selenium.hub.Downloads.UseCaseMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DownloadsTest extends RestBaseTestCase {
    @InjectMocks
    private Downloads target;

    @Test
    void invoke() throws Exception {
        target = spy(target);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        UseCaseMethod method = mock(UseCaseMethod.class);
        String sessionKey = "sessionKey";
        String path = "path";
        RemoteResponse remoteResponse = mock(RemoteResponse.class);

        doReturn(Optional.of(sessionKey)).when(target).getSessionKey(request);
        doReturn(path).when(target).getPath(request);
        when(method.invoke(sessionKey, path)).thenReturn(remoteResponse);
        doNothing().when(target).sendResponse(response, remoteResponse);

        target.invoke(request, response, method);

        verify(target).getSessionKey(request);
        verify(target).getPath(request);
        verify(method).invoke(sessionKey, path);
        verify(target).sendResponse(response, remoteResponse);
    }

    @Nested
    class getSessionKey {
        @Test
        void whenSpecified() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn("/sessionKey/path");

            assertThat(target.getSessionKey(request), is(Optional.of("sessionKey")));

            verify(request).getPathInfo();
        }

        @Test
        void whenSlashOnly() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn("/");

            assertThat(target.getSessionKey(request), is(Optional.empty()));

            verify(request).getPathInfo();
        }

        @Test
        void whenNull() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn(null);

            assertThat(target.getSessionKey(request), is(Optional.empty()));

            verify(request).getPathInfo();
        }
    }

    @Nested
    class getPath {
        @Test
        void whenSpecified() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn("/sessionKey/path");

            assertThat(target.getPath(request), is("path"));

            verify(request).getPathInfo();
        }

        @Test
        void whenNotSpecified() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn("/sessionKey");

            assertThat(target.getPath(request), is(""));

            verify(request).getPathInfo();
        }

        @Test
        void whenSlashOnly() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn("/");

            assertThat(target.getPath(request), is(""));

            verify(request).getPathInfo();
        }

        @Test
        void whenNull() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn(null);

            assertThat(target.getPath(request), is(""));

            verify(request).getPathInfo();
        }
    }

    @Test
    void sendResponse() throws Exception {
        target = spy(target);

        HttpServletResponse response = mock(HttpServletResponse.class);
        RemoteResponse remoteResponse = mock(RemoteResponse.class);
        int status = 200;
        InputStream byteStream = mock(InputStream.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        when(remoteResponse.getStatus()).thenReturn(status);
        doNothing().when(response).setStatus(status);
        doNothing().when(target).addHeaders(response, remoteResponse);
        when(remoteResponse.getByteStream()).thenReturn(byteStream);
        when(response.getOutputStream()).thenReturn(outputStream);
        doNothing().when(target).copy(byteStream, outputStream);

        target.sendResponse(response, remoteResponse);

        verify(remoteResponse).getStatus();
        verify(response).setStatus(status);
        verify(target).addHeaders(response, remoteResponse);
        verify(remoteResponse).getByteStream();
        verify(response).getOutputStream();
        verify(target).copy(byteStream, outputStream);
    }

    @Test
    void addHeaders() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        RemoteResponse remoteResponse = mock(RemoteResponse.class);

        Map<String, List<String>> headers = new TreeMap<String, List<String>>() {
            {
                put("Content-Type", Arrays.asList("application/json", "charset=utf-8"));
                put("Content-Length", Collections.singletonList("1024"));
            }
        };

        when(remoteResponse.getHeaders()).thenReturn(headers);
        doNothing().when(response).addHeader("Content-Type", "application/json");
        doNothing().when(response).addHeader("Content-Type", "charset=utf-8");
        doNothing().when(response).addHeader("Content-Length", "1024");

        target.addHeaders(response, remoteResponse);

        verify(remoteResponse).getHeaders();
        verify(response).addHeader("Content-Type", "application/json");
        verify(response).addHeader("Content-Type", "charset=utf-8");
        verify(response).addHeader("Content-Length", "1024");
    }
}