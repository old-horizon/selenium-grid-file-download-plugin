package com.github.old_horizon.selenium.hub;

import java.io.Closeable;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface RemoteResponse extends Closeable {
    int getStatus();

    InputStream getByteStream();

    Map<String, List<String>> getHeaders();
}
