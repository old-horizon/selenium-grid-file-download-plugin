package com.github.old_horizon.selenium.node;

import com.github.old_horizon.selenium.HttpServletResponseUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class DownloadsTest extends RestBaseTestCase {
    @InjectMocks
    private Downloads target;

    @Mock
    private FilesUseCase useCase;

    @Mock
    private HttpServletResponseUtil util;

    @Nested
    class doGet {
        @Test
        @SuppressWarnings("unchecked")
        void whenFileNotSpecified() throws Exception {
            target = Mockito.spy(target);

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            List<String> items = (List<String>) mock(List.class);
            Map<String, List<Map<String, String>>> map = (Map<String, List<Map<String, String>>>) mock(Map.class);

            doReturn(Optional.empty()).when(target).getFileName(request);
            when(useCase.getFiles()).thenReturn(items);
            doReturn(map).when(target).toFilesMap(items);
            doNothing().when(util).sendJson(response, map);

            target.doGet(request, response);

            verify(target).getFileName(request);
            verify(useCase).getFiles();
            verify(target).toFilesMap(items);
            verify(util).sendJson(response, map);
        }

        @Test
        void whenFileExists() throws Exception {
            target = Mockito.spy(target);

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            byte[] bytes = new byte[0];

            doReturn(Optional.of("file.txt")).when(target).getFileName(request);
            when(useCase.getFile("file.txt")).thenReturn(bytes);
            doNothing().when(util).sendBinary(response, bytes);

            target.doGet(request, response);

            verify(target).getFileName(request);
            verify(useCase).getFile("file.txt");
            verify(util).sendBinary(response, bytes);
        }
    }

    @Nested
    class doDelete {
        @Test
        void whenFileExists() throws Exception {
            target = Mockito.spy(target);

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            doReturn(Optional.of("file.txt")).when(target).getFileName(request);
            doNothing().when(useCase).deleteFile("file.txt");

            target.doDelete(request, response);

            verify(target).getFileName(request);
            verify(useCase).deleteFile("file.txt");
        }

        @Test
        void whenFileNotSpecified() throws Exception {
            target = Mockito.spy(target);

            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            doReturn(Optional.empty()).when(target).getFileName(request);
            doNothing().when(useCase).deleteFiles();

            target.doDelete(request, response);

            verify(target).getFileName(request);
            verify(useCase).deleteFiles();
        }
    }

    @Nested
    class getFileName {
        @Test
        void whenFileNameExists() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn("/file.txt");

            assertThat(target.getFileName(request), is(Optional.of("file.txt")));

            verify(request).getPathInfo();
        }

        @Test
        void whenSlashOnly() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn("/");

            assertThat(target.getFileName(request), is(Optional.empty()));

            verify(request).getPathInfo();
        }

        @Test
        void whenNull() throws Exception {
            HttpServletRequest request = mock(HttpServletRequest.class);

            when(request.getPathInfo()).thenReturn(null);

            assertThat(target.getFileName(request), is(Optional.empty()));

            verify(request).getPathInfo();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void toFilesMap() throws Exception {
        List<String> items = Arrays.asList("file1.txt", "file2.txt", "file3.txt");

        Map<String, String> file1Map = (Map<String, String>) mock(Map.class);
        Map<String, String> file2Map = (Map<String, String>) mock(Map.class);
        Map<String, String> file3Map = (Map<String, String>) mock(Map.class);
        Map<String, List<Map<String, String>>> expected = (Map<String, List<Map<String, String>>>) mock(Map.class);

        when(util.singleEntryMap("name", "file1.txt")).thenReturn(file1Map);
        when(util.singleEntryMap("name", "file2.txt")).thenReturn(file2Map);
        when(util.singleEntryMap("name", "file3.txt")).thenReturn(file3Map);
        when(util.singleEntryMap("files", Arrays.asList(file1Map, file2Map, file3Map))).thenReturn(expected);

        assertThat(target.toFilesMap(items), is(expected));

        verify(util).singleEntryMap("name", "file1.txt");
        verify(util).singleEntryMap("name", "file2.txt");
        verify(util).singleEntryMap("name", "file3.txt");
        verify(util).singleEntryMap("files", Arrays.asList(file1Map, file2Map, file3Map));
    }
}