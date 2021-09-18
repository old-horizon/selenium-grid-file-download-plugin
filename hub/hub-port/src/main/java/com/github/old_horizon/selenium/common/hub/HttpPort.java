package com.github.old_horizon.selenium.common.hub;

import java.io.IOException;

public interface HttpPort {
    RemoteResponse get(String url) throws IOException;

    RemoteResponse delete(String url) throws IOException;
}
