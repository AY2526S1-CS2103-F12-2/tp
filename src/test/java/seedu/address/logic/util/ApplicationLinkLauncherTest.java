package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApplicationLinkLauncherTest {

    private Desktop mockDesktop;

    @BeforeEach
    public void setup() {
        mockDesktop = mock(Desktop.class);
        ApplicationLinkLauncher.setDesktop(mockDesktop);
    }

    @Test
    public void launchEmail_validEmail_returnsSuccess() {
        ApplicationLinkResult result =
                ApplicationLinkLauncher.launchEmail("test@example.com");

        try {
            verify(mockDesktop, times(1)).browse(any(URI.class));
            assertNotNull(result);
            assertEquals(
                    String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS, ApplicationLinkLauncher.ApplicationType.EMAIL),
                    result.getMessage()
            );
        } catch (IOException e) {
            // This block should not be reached in this test
            assert false : "IOException should not occur in this test.";
        }
    }

    @Test
    public void launchTelegram_validHandle_returnsSuccess() {
        ApplicationLinkResult result =
                ApplicationLinkLauncher.launchTelegram("testhandle");
        try {
            verify(mockDesktop, times(1)).browse(any(URI.class));
            assertNotNull(result);
            assertEquals(
                    String.format(
                            ApplicationLinkLauncher.MESSAGE_SUCCESS,
                            ApplicationLinkLauncher.ApplicationType.TELEGRAM),
                    result.getMessage()
            );
        } catch (IOException e) {
            // This block should not be reached in this test
            assert false : "IOException should not occur in this test.";
        }
    }

    @Test
    public void launchGithub_validUsername_returnsSuccess() {
        ApplicationLinkResult result =
                ApplicationLinkLauncher.launchGithub("testuser");
        try {
            verify(mockDesktop, times(1)).browse(any(URI.class));
            assertNotNull(result);
            assertEquals(
                    String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS, ApplicationLinkLauncher.ApplicationType.GITHUB),
                    result.getMessage()
            );
        } catch (IOException e) {
            // This block should not be reached in this test
            assert false : "IOException should not occur in this test.";
        }
    }

    @Test
    public void launchApplicationLink_invalidUri_returnsFailure() {

        ApplicationLinkResult result = ApplicationLinkLauncher.launchApplicationLink(
                "ht!tp://invalid-uri",
                ApplicationLinkLauncher.ApplicationType.GITHUB
        );

        assertNotNull(result);
        assertEquals(
                String.format(ApplicationLinkLauncher.MESSAGE_FAILURE, ApplicationLinkLauncher.ApplicationType.GITHUB),
                result.getMessage()
        );
    }

    @Test
    public void launchApplicationLink_desktopNotSupported_returnsFailure() {
        ApplicationLinkLauncher.setDesktop(null);

        ApplicationLinkResult result = ApplicationLinkLauncher.launchApplicationLink(
                "https://t.me/testhandle",
                ApplicationLinkLauncher.ApplicationType.TELEGRAM
        );

        // Assert: ensure the result indicates failure
        assertFalse(result.isSuccess(), "Expected failure when Desktop is not supported");
        assertEquals(
                String.format(ApplicationLinkLauncher.MESSAGE_FAILURE, ApplicationLinkLauncher.ApplicationType.TELEGRAM),
                result.getMessage()
        );
    }


}
