package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
        // Mock that mail action is supported
        when(mockDesktop.isSupported(Desktop.Action.MAIL)).thenReturn(true);

        ApplicationLinkResult result =
                ApplicationLinkLauncher.launchEmail("test@example.com");

        try {
            verify(mockDesktop, times(1)).mail(any(URI.class));
            assertNotNull(result);
            assertEquals(
                    String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS,
                            ApplicationLinkLauncher.ApplicationType.EMAIL),
                    result.getMessage()
            );
        } catch (IOException e) {
            // This block should not be reached in this test
            assert false : "IOException should not occur in this test.";
        }
    }

    @Test
    public void launchTelegram_validHandle_returnsSuccess() {
        // Mock that browse action is supported
        when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);

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
        // Mock that browse action is supported
        when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);

        ApplicationLinkResult result =
                ApplicationLinkLauncher.launchGithub("testuser");
        try {
            verify(mockDesktop, times(1)).browse(any(URI.class));
            assertNotNull(result);
            assertEquals(
                    String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS,
                            ApplicationLinkLauncher.ApplicationType.GITHUB),
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
                String.format(ApplicationLinkLauncher.MESSAGE_FAILURE,
                        ApplicationLinkLauncher.ApplicationType.GITHUB),
                result.getMessage()
        );
    }

    @Test
    public void attemptOpenWithDesktop_desktopNotSupported_returnsFailure() {
        ApplicationLinkLauncher.setDesktop(null);

        try {
            boolean result = ApplicationLinkLauncher.attemptOpenWithDesktop(
                    new URI("https://t.me/testhandle"));
            assertFalse(result, "Expected failure when Desktop is not supported");
        } catch (URISyntaxException | IOException e) {
            // This block should not be reached in this test
            assert false : "URISyntaxException or IOException should not occur in this test.";
        }
    }

    /**
     * Occurs when Desktop is not supported but DesktopAPI fallback is successful
     */
    @Test
    public void openLink_tryOpenWithFallbacks_successfulOpen() {
        // Simulate Desktop not supported
        ApplicationLinkLauncher.setDesktop(null);

        // Mock DesktopAPI fallback to succeed
        try (var mockedApi = mockStatic(DesktopAPI.class)) {
            // Mock DesktopAPI fallback to succeed
            mockedApi.when(() -> DesktopAPI.browse(any(URI.class))).thenReturn(true);

            URI uri = new URI("https://t.me/testhandle");

            assertDoesNotThrow(() -> {
                ApplicationLinkLauncher.launchApplicationLink(uri.toString(),
                        ApplicationLinkLauncher.ApplicationType.TELEGRAM);
            });

            // Verify fallback was invoked once
            mockedApi.verify(() -> DesktopAPI.browse(any(URI.class)), times(1));
        } catch (URISyntaxException e) {
            // This block should not be reached in this test
            assert false : "URISyntaxException should not occur in this test.";
        }
    }

    /**
     * Occurs when both Desktop and DesktopAPI fallback methods fail
     */
    @Test
    public void openLink_allMethodsFail_throwsIOException() {
        // Desktop not supported
        ApplicationLinkLauncher.setDesktop(null);

        try (var mockedApi = mockStatic(DesktopAPI.class)) {
            mockedApi.when(() -> DesktopAPI.browse(any(URI.class))).thenReturn(false);
            URI uri = new URI("https://github.com/testuser");

            var openLink = ApplicationLinkLauncher.class.getDeclaredMethod("openLink", URI.class);
            openLink.setAccessible(true);

            Exception thrown = assertThrows(Exception.class, () -> openLink.invoke(null, uri));

            // unwrap InvocationTargetException
            Throwable cause = thrown.getCause();
            assertNotNull(cause, "Expected underlying IOException cause");
            assertTrue(cause instanceof IOException, "Expected IOException when all methods fail");
            assertTrue(cause.getMessage().contains("Failed to open link"));
        } catch (URISyntaxException e) {
            // This block should not be reached in this test
            assert false : "URISyntaxException should not occur in this test.";
        } catch (Exception e) {
            // Handle reflection exceptions
            assert false : "Reflection exception occurred: " + e.getMessage();
        }
    }

    /**
     * Occurs when Desktop is supported but action is not supported
     */
    @Test
    public void attemptOpenWithDesktop_isActionSupportFalse_returnFailure() {
        when(mockDesktop.isSupported(any(Desktop.Action.class))).thenReturn(false);

        try {
            boolean result = ApplicationLinkLauncher.attemptOpenWithDesktop(
                    new URI("https://t.me/testhandle"));

            assertFalse(result, "Expected failure when Desktop action is not supported");
            verify(mockDesktop, never()).browse(any(URI.class));
            verify(mockDesktop, never()).mail(any(URI.class));
        } catch (URISyntaxException | IOException e) {
            // This block should not be reached in this test
            assert false : "URISyntaxException or IOException should not occur in this test.";
        }
    }

    /**
     * Occurs when Desktop is supported and action is supported but
     * Desktop method throws UnsupportedOperationException
     */
    @Test
    public void attemptOpenWithDesktop_catchUnsupportedOperationException_returnFailure() {
        when(mockDesktop.isSupported(any(Desktop.Action.class))).thenReturn(true);
        try {
            doThrow(new UnsupportedOperationException("Simulated unsupported")).when(mockDesktop)
                    .browse(any(URI.class));

            boolean result = ApplicationLinkLauncher.attemptOpenWithDesktop(
                    new URI("https://t.me/testhandle"));

            assertFalse(result, "Expected failure when UnsupportedOperationException is thrown");
        } catch (URISyntaxException | IOException e) {
            // This block should not be reached in this test
            assert false : "URISyntaxException or IOException should not occur in this test.";
        }
    }

    /**
     * Occurs when Desktop is supported and action is supported but
     * Desktop method throws IOException
     */
    @Test
    public void attemptOpenWithDesktop_catchIOException_returnFailure() {
        when(mockDesktop.isSupported(any(Desktop.Action.class))).thenReturn(true);
        try {
            doThrow(new IOException("Simulated I/O error")).when(mockDesktop)
                    .browse(any(URI.class));

            boolean result = ApplicationLinkLauncher.attemptOpenWithDesktop(
                    new URI("https://t.me/testhandle"));

            assertFalse(result, "Expected failure when IOException is thrown");

        } catch (URISyntaxException | IOException e) {
            // This block should not be reached in this test
            assert false : "URISyntaxException or IOException should not occur in this test.";
        }
    }

    /* The following test cases are to check if isActionSupported works as intended */
    @Test
    public void isActionSupported_browseActionSupported_returnsTrue() {
        when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);
        assertTrue(
                ApplicationLinkLauncher.isActionSupported(Desktop.Action.BROWSE),
                "Expected BROWSE action to be supported");
    }

    @Test
    public void isActionSupported_mailActionSupported_returnsTrue() {
        when(mockDesktop.isSupported(Desktop.Action.MAIL)).thenReturn(true);
        assertTrue(
                ApplicationLinkLauncher.isActionSupported(Desktop.Action.MAIL),
                "Expected MAIL action to be supported");
    }

    @Test
    public void isActionSupported_openActionSupported_returnsTrue() {
        when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(true);
        assertTrue(
                ApplicationLinkLauncher.isActionSupported(Desktop.Action.OPEN),
                "Expected OPEN action to be supported");
    }

    @Test
    public void isActionSupported_editActionSupported_returnsTrue() {
        when(mockDesktop.isSupported(Desktop.Action.EDIT)).thenReturn(true);
        assertTrue(
                ApplicationLinkLauncher.isActionSupported(Desktop.Action.EDIT),
                "Expected EDIT action to be supported");
    }

    @Test
    public void isActionSupported_printActionSupported_returnsTrue() {
        when(mockDesktop.isSupported(Desktop.Action.PRINT)).thenReturn(true);
        assertTrue(
                ApplicationLinkLauncher.isActionSupported(Desktop.Action.PRINT),
                "Expected PRINT action to be supported");
    }
}
