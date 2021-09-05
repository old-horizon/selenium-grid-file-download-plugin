package com.github.old_horizon.selenium.hub;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RemoteResponseImplTest {
    @Test
    void removeHeaders() {
        Map<String, List<String>> headers = new TreeMap<String, List<String>>() {
            {
                put("Host", Collections.singletonList("localhost"));
                put("Date", Collections.singletonList("1 Jan 2021 12:34:56 GMT"));
                put("Server", Collections.singletonList("Jetty"));
                put("Content-Type", Arrays.asList("application/json", "charset=utf-8"));
                put("Content-Length", Collections.singletonList("1024"));
            }
        };

        Map<String, List<String>> expected = new TreeMap<String, List<String>>() {
            {
                put("Content-Type", Arrays.asList("application/json", "charset=utf-8"));
                put("Content-Length", Collections.singletonList("1024"));
            }
        };

        assertThat(RemoteResponseImpl.removeHeaders(headers, Arrays.asList("Host", "Date", "Server")), is(expected));
    }
}