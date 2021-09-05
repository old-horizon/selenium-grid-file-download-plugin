package com.github.old_horizon.selenium.node;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class FilesUseCaseTest extends UseCaseBaseTestCase {
    @InjectMocks
    private FilesUseCase target;

    @Mock
    private FilesPort port;

    @Test
    void getFile() throws Exception {
        byte[] bytes = new byte[0];

        when(port.getFile("item.txt")).thenReturn(bytes);

        assertThat(target.getFile("item.txt"), is(bytes));

        verify(port).getFile("item.txt");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getFiles() throws Exception {
        List<String> items = (List<String>) mock(List.class);

        when(port.getFiles()).thenReturn(items);

        assertThat(target.getFiles(), is(items));

        verify(port).getFiles();
    }

    @Test
    void deleteFile() throws Exception {
        doNothing().when(port).deleteFile("item.txt");

        target.deleteFile("item.txt");

        verify(port).deleteFile("item.txt");
    }

    @Test
    void deleteFiles() throws Exception {
        doNothing().when(port).deleteFiles();

        target.deleteFiles();

        verify(port).deleteFiles();
    }
}