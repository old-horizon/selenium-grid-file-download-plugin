package com.github.old_horizon.selenium;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class HttpServletExceptionMapperTest extends RestBaseTestCase {
    @InjectMocks
    private HttpServletExceptionMapper target;

    @Mock
    private HttpServletResponseUtil util;

    @Mock
    private Logger logger;

    @Nested
    class apply {
        @Test
        void success() throws Exception {
            target = spy(target);

            ThrowingRunnable throwingRunnable = mock(ThrowingRunnable.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            doNothing().when(throwingRunnable).run();

            target.apply(throwingRunnable, response);

            verify(throwingRunnable).run();
        }

        @Test
        @SuppressWarnings("unchecked")
        void definedExceptionThrown() throws Exception {
            target = spy(target);
            target.mapping = new HashMap<Class<? extends Exception>, Integer>() {
                {
                    put(FileNotFoundException.class, SC_NOT_FOUND);
                }
            };

            ThrowingRunnable throwingRunnable = mock(ThrowingRunnable.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            FileNotFoundException exception = new FileNotFoundException("exceptionMessage");
            Map<String, String> messageMap = (Map<String, String>) mock(Map.class);

            doThrow(exception).when(throwingRunnable).run();
            doNothing().when(logger).log(Level.WARNING, "exceptionMessage", exception);
            doNothing().when(response).setStatus(SC_NOT_FOUND);
            doReturn(messageMap).when(target).toMessageMap("exceptionMessage");
            doNothing().when(util).sendJson(response, messageMap);

            target.apply(throwingRunnable, response);

            verify(throwingRunnable).run();
            verify(logger).log(Level.WARNING, "exceptionMessage", exception);
            verify(response).setStatus(SC_NOT_FOUND);
            verify(target).toMessageMap("exceptionMessage");
            verify(util).sendJson(response, messageMap);
        }

        @Test
        @SuppressWarnings("unchecked")
        void undefinedExceptionThrown() throws Exception {
            target = spy(target);
            target.mapping = Collections.emptyMap();

            ThrowingRunnable throwingRunnable = mock(ThrowingRunnable.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            IllegalArgumentException exception = new IllegalArgumentException("exceptionMessage");
            Map<String, String> messageMap = (Map<String, String>) mock(Map.class);

            doThrow(exception).when(throwingRunnable).run();
            doNothing().when(logger).log(Level.WARNING, "exceptionMessage", exception);
            doNothing().when(response).setStatus(SC_INTERNAL_SERVER_ERROR);
            doReturn(messageMap).when(target).toMessageMap("exceptionMessage");
            doNothing().when(util).sendJson(response, messageMap);

            target.apply(throwingRunnable, response);

            verify(throwingRunnable).run();
            verify(logger).log(Level.WARNING, "exceptionMessage", exception);
            verify(response).setStatus(SC_INTERNAL_SERVER_ERROR);
            verify(target).toMessageMap("exceptionMessage");
            verify(util).sendJson(response, messageMap);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void toMessageMap() throws Exception {
        Map<String, String> expected = (Map<String, String>) mock(Map.class);

        when(util.singleEntryMap("message", "value")).thenReturn(expected);

        assertThat(target.toMessageMap("value"), is(expected));

        verify(util).singleEntryMap("message", "value");
    }

    @Nested
    class Builder {
        @Test
        void addMapping() throws Exception {
            HttpServletExceptionMapper.Builder builder = new HttpServletExceptionMapper.Builder();

            Map<Class<? extends Exception>, Integer> expected = new HashMap<Class<? extends Exception>, Integer>() {
                {
                    put(FileNotFoundException.class, SC_NOT_FOUND);
                }
            };

            assertThat(builder.addMapping(FileNotFoundException.class, SC_NOT_FOUND), is(builder));

            assertThat(builder.mapping, is(expected));
        }

        @Test
        void build() throws Exception {
            Map<Class<? extends Exception>, Integer> mapping = new HashMap<Class<? extends Exception>, Integer>() {
                {
                    put(FileNotFoundException.class, SC_NOT_FOUND);
                }
            };

            HttpServletExceptionMapper.Builder builder = new HttpServletExceptionMapper.Builder();
            builder.mapping = mapping;

            HttpServletExceptionMapper expected = new HttpServletExceptionMapper(mapping);

            assertThat(builder.build(), is(expected));
        }
    }
}