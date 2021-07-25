package com.github.old_horizon.selenium.hub;

import com.github.old_horizon.selenium.DriverBaseTestCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.grid.internal.*;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BaseUrlDriverTest extends DriverBaseTestCase {
    @InjectMocks
    private BaseUrlDriver target;

    @Mock
    private GridRegistry registry;

    @Nested
    class find {
        @Test
        void whenSessionExists() throws Exception {
            String sessionKey = "sessionKey";
            URL expected = new URL("http://node:5555/");
            TestSession session = mock(TestSession.class);
            TestSlot slot = mock(TestSlot.class);
            RemoteProxy proxy = mock(RemoteProxy.class);

            when(registry.getSession(ExternalSessionKey.fromString("sessionKey"))).thenReturn(session);
            when(session.getSlot()).thenReturn(slot);
            when(slot.getProxy()).thenReturn(proxy);
            when(proxy.getRemoteHost()).thenReturn(expected);

            assertThat(target.find(sessionKey), is(expected));

            verify(registry).getSession(ExternalSessionKey.fromString("sessionKey"));
            verify(session).getSlot();
            verify(slot).getProxy();
            verify(proxy).getRemoteHost();
        }

        @Test
        void whenSessionNotExists() throws Exception {
            SessionNotFoundException exception = assertThrows(SessionNotFoundException.class, () -> {
                String sessionKey = "sessionKey";

                when(registry.getSession(ExternalSessionKey.fromString("sessionKey"))).thenReturn(null);

                target.find(sessionKey);
            });

            verify(registry).getSession(ExternalSessionKey.fromString("sessionKey"));

            assertThat(exception.getMessage(), is("session associated with key sessionKey not found"));
        }
    }
}