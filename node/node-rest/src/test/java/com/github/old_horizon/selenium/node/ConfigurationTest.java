package com.github.old_horizon.selenium.node;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ConfigurationTest extends RestBaseTestCase {
    @InjectMocks
    private Configuration target;

    @Nested
    class getDownloadDirectory {
        @Test
        void defaultValue() throws Exception {
            target = spy(target);

            doReturn("/home/selenium").when(target).getSystemProperty("user.home");

            assertThat(target.getDownloadDirectory(), is(Paths.get("/home/selenium/Downloads")));

            verify(target).getSystemProperty("user.home");
        }

        @Test
        void whenConfigured() throws Exception {
            target = spy(target);

            doReturn("/download/directory").when(target).getSystemProperty("download.directory");

            assertThat(target.getDownloadDirectory(), is(Paths.get("/download/directory")));

            verify(target).getSystemProperty("download.directory");
        }
    }
}