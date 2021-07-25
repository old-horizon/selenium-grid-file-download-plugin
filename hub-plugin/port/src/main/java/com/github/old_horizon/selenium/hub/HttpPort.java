package com.github.old_horizon.selenium.hub;

import java.io.IOException;

public interface HttpPort {
    RemoteResponse get(String url) throws IOException;

    RemoteResponse delete(String url) throws IOException;
}
