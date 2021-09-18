package com.github.old_horizon.selenium.common.node;

import com.github.old_horizon.selenium.common.HttpServletExceptionMapper;
import com.github.old_horizon.selenium.common.HttpServletResponseUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class Downloads extends HttpServlet {
    private FilesUseCase useCase = new FilesUseCase(new FilesDriver(Configuration.getInstance().getDownloadDirectory()));
    private HttpServletExceptionMapper exceptionMapper = new HttpServletExceptionMapper.Builder()
            .addMapping(FileNotFoundException.class, SC_NOT_FOUND)
            .build();
    private HttpServletResponseUtil util = HttpServletResponseUtil.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        exceptionMapper.apply(() -> {
            Optional<String> fileName = getFileName(request);
            if (fileName.isPresent()) {
                util.sendBinary(response, useCase.getFile(fileName.get()));
            } else {
                util.sendJson(response, toFilesMap(useCase.getFiles()));
            }
        }, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        exceptionMapper.apply(() -> {
            Optional<String> fileName = getFileName(request);
            if (fileName.isPresent()) {
                useCase.deleteFile(fileName.get());
            } else {
                useCase.deleteFiles();
            }
        }, response);
    }

    Optional<String> getFileName(HttpServletRequest request) {
        return Optional.ofNullable(request.getPathInfo())
                .map(p -> p.replaceFirst("/", ""))
                .filter(p -> !p.isEmpty());
    }

    Map<String, List<Map<String, String>>> toFilesMap(List<String> items) {
        return util.singleEntryMap("files", items.stream()
                .map(i -> util.singleEntryMap("name", i))
                .collect(Collectors.toList()));
    }
}
