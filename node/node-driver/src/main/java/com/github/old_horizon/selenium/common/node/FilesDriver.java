package com.github.old_horizon.selenium.common.node;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesDriver implements FilesPort {
    private final Path dir;

    public FilesDriver(Path dir) {
        this.dir = dir;
    }

    @Override
    public byte[] getFile(String fileName) throws IOException {
        Path file = dir.resolve(fileName);
        throwIfNotExists(file);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Files.copy(file, out);
            return out.toByteArray();
        }
    }

    @Override
    public List<String> getFiles() throws IOException {
        return findFiles().stream()
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        final Path file = dir.resolve(fileName);
        throwIfNotExists(file);
        Files.delete(file);
    }

    @Override
    public void deleteFiles() throws IOException {
        for (Path file : findFiles()) {
            Files.delete(file);
        }
    }

    List<Path> findFiles() throws IOException {
        try (Stream<Path> files = Files.find(dir, 1, (path, attr) -> attr.isRegularFile())) {
            return files.collect(Collectors.toList());
        }
    }

    private void throwIfNotExists(Path file) throws FileNotFoundException {
        if (!Files.exists(file)) {
            throw new FileNotFoundException("file " + file.getFileName().toString() + " not found");
        }
    }
}
