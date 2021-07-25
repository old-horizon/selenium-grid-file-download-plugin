package com.github.old_horizon.selenium;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.json.Json;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

class HttpServletResponseUtilTest extends RestBaseTestCase {
    @InjectMocks
    private HttpServletResponseUtil target;

    @Mock
    private Json json;

    @Test
    void sendJson() throws Exception {
        Object object = mock(Object.class);
        String jsonString = "jsonString";
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);

        when(json.toJson(object)).thenReturn(jsonString);
        doNothing().when(response).setHeader("Content-Type", "application/json");
        when(response.getWriter()).thenReturn(writer);
        doNothing().when(writer).println(jsonString);

        target.sendJson(response, object);

        verify(json).toJson(object);
        verify(response).setHeader("Content-Type", "application/json");
        verify(response).getWriter();
        verify(writer).println(jsonString);
    }
}