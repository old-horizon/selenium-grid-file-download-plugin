package com.github.old_horizon.selenium.node;

import java.io.IOException;
import java.util.List;

public interface FilesPort {
    byte[] getFile(String fileName) throws IOException;

    List<String> getFiles() throws IOException;

    void deleteFile(String fileName) throws IOException;

    void deleteFiles() throws IOException;
}
