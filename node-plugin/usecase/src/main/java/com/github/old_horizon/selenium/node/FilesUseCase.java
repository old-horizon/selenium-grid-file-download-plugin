package com.github.old_horizon.selenium.node;

import java.io.IOException;
import java.util.List;

public class FilesUseCase {
    private final FilesPort port;

    public FilesUseCase(FilesPort port) {
        this.port = port;
    }

    public byte[] getFile(String fileName) throws IOException {
        return port.getFile(fileName);
    }

    public List<String> getFiles() throws IOException {
        return port.getFiles();
    }

    public void deleteFile(String fileName) throws IOException {
        port.deleteFile(fileName);
    }

    public void deleteFiles() throws IOException {
        port.deleteFiles();
    }
}
