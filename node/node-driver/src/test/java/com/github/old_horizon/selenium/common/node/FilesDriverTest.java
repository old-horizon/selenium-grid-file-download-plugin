package com.github.old_horizon.selenium.common.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilesDriverTest {
    @TempDir
    Path dir;

    private FilesDriver target;

    @BeforeEach
    void setup() {
        target = new FilesDriver(dir);
    }

    @Nested
    class getFile {
        @Test
        void whenExists() throws Exception {
            Files.write(dir.resolve("file.txt"), Collections.singletonList("content"));

            assertThat(target.getFile("file.txt"), is("content\n".getBytes()));
        }

        @Test
        void whenNotExists() throws Exception {
            final FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
                target.getFile("file.txt");
            });

            assertThat(exception.getMessage(), is("file file.txt not found"));
        }
    }

    @Test
    void getFiles() throws Exception {
        Files.createFile(dir.resolve("file1.txt"));
        Files.createFile(dir.resolve("file2.txt"));
        Files.createDirectory(dir.resolve("subDirectory"));

        assertThat(target.getFiles(), containsInAnyOrder("file1.txt", "file2.txt"));
    }

    @Nested
    class deleteFile {
        @Test
        void whenExists() throws Exception {
            Files.createFile(dir.resolve("file.txt"));

            target.deleteFile("file.txt");

            assertThat(Files.exists(dir.resolve("file.txt")), is(false));
        }

        @Test
        void whenNotExists() throws Exception {
            final FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
                target.deleteFile("file.txt");
            });

            assertThat(exception.getMessage(), is("file file.txt not found"));
        }
    }

    @Test
    void deleteFiles() throws Exception {
        Files.createFile(dir.resolve("file1.txt"));
        Files.createFile(dir.resolve("file2.txt"));

        target.deleteFiles();

        try (Stream<Path> list = Files.list(dir)) {
            assertThat(list.count(), is(0L));
        }
    }
}