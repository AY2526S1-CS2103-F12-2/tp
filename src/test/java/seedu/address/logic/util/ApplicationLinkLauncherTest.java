package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApplicationLinkLauncherTest {

    private DesktopWrapper mockDesktopWrapper;

    @BeforeEach
    public void setup() {
        mockDesktopWrapper = mock(DesktopWrapper.class);
        ApplicationLinkLauncher.setDesktopWrapper(mockDesktopWrapper);
    }

    @Test
    public void launchEmail_validEmail_returnsSuccess() {
        try {
            when(mockDesktopWrapper.isSupported(Action.MAIL)).thenReturn(true);
            doNothing().when(mockDesktopWrapper).mail(any(URI.class));

            ApplicationLinkResult result = ApplicationLinkLauncher.launchEmail("test@example.com");

            verify(mockDesktopWrapper, times(1)).mail(any(URI.class));
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
        try {
            when(mockDesktopWrapper.isSupported(Action.BROWSE)).thenReturn(true);
            doNothing().when(mockDesktopWrapper).browse(any(URI.class));
            ApplicationLinkResult result =
                    ApplicationLinkLauncher.launchTelegram("testhandle");
            verify(mockDesktopWrapper, times(1)).browse(any(URI.class));
            assertNotNull(result);
            assertEquals(
                    String.format(ApplicationLinkLauncher.MESSAGE_SUCCESS,
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
        try {
            when(mockDesktopWrapper.isSupported(Action.BROWSE)).thenReturn(true);
            doNothing().when(mockDesktopWrapper).browse(any(URI.class));
            ApplicationLinkResult result =
                    ApplicationLinkLauncher.launchGithub("testuser");
            verify(mockDesktopWrapper, times(1)).browse(any(URI.class));
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
        DesktopWrapper mockWrapper = mock(DesktopWrapper.class);
        when(mockWrapper.isSupported(any())).thenReturn(false); // Simulate not supported
        ApplicationLinkLauncher.setDesktopWrapper(mockWrapper);

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
        DesktopWrapper mockWrapper = mock(DesktopWrapper.class);
        when(mockWrapper.isSupported(any())).thenReturn(false); // Simulate not supported
        ApplicationLinkLauncher.setDesktopWrapper(mockWrapper);

        // Mock DesktopAPI fallback to succeed
        try (var mockedApi = mockStatic(DesktopApi.class)) {
            // Mock DesktopAPI fallback to succeed
            mockedApi.when(() -> DesktopApi.browse(any(URI.class))).thenReturn(true);

            URI uri = new URI("https://t.me/testhandle");

            assertDoesNotThrow(() -> {
                ApplicationLinkLauncher.launchApplicationLink(uri.toString(),
                        ApplicationLinkLauncher.ApplicationType.TELEGRAM);
            });

            // Verify fallback was invoked once
            mockedApi.verify(() -> DesktopApi.browse(any(URI.class)), times(1));
        } catch (URISyntaxException e) {
            // This block should not be reached in this test
            assert false : "URISyntaxException should not occur in this test.";
        }
    }

    /**
     * Occurs when both Desktop and DesktopAPI fallback methods fail
     */
    @Test
    public void openLink_allMethodsFail_throwsException() {
        // Desktop not supported
        DesktopWrapper mockWrapper = mock(DesktopWrapper.class);
        when(mockWrapper.isSupported(any())).thenReturn(false);
        ApplicationLinkLauncher.setDesktopWrapper(mockWrapper);

        try (var mockedApi = mockStatic(DesktopApi.class)) {
            mockedApi.when(() -> DesktopApi.browse(any(URI.class))).thenReturn(false);

            ApplicationLinkResult result = ApplicationLinkLauncher.launchApplicationLink(
                    "https://github.com/testuser", ApplicationLinkLauncher.ApplicationType.GITHUB);

            assertNotNull(result);
            assertEquals(
                    String.format(ApplicationLinkLauncher.MESSAGE_FAILURE,
                            ApplicationLinkLauncher.ApplicationType.GITHUB),
                    result.getMessage()
            );

            mockedApi.verify(() -> DesktopApi.browse(any(URI.class)), times(1));
        }
    }

    /**
     * Occurs when Desktop is supported but action is not supported
     */
    @Test
    public void attemptOpenWithDesktop_isActionSupportFalse_returnFailure() {
        when(mockDesktopWrapper.isSupported(any(Desktop.Action.class))).thenReturn(false);

        try {
            boolean result = ApplicationLinkLauncher.attemptOpenWithDesktop(
                    new URI("https://t.me/testhandle"));

            assertFalse(result, "Expected failure when Desktop action is not supported");
            verify(mockDesktopWrapper, never()).browse(any(URI.class));
            verify(mockDesktopWrapper, never()).mail(any(URI.class));
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
        when(mockDesktopWrapper.isSupported(any(Desktop.Action.class))).thenReturn(true);
        try {
            doThrow(new UnsupportedOperationException("Simulated unsupported")).when(mockDesktopWrapper)
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
    public void attemptOpenWithDesktop_catchException_returnFailure() {
        when(mockDesktopWrapper.isSupported(any(Desktop.Action.class))).thenReturn(true);
        try {
            doThrow(new IOException("Simulated I/O error")).when(mockDesktopWrapper)
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
    public void isActionSupported_openActionSupported_returnsTrue() {
        when(mockDesktopWrapper.isSupported(Desktop.Action.OPEN)).thenReturn(true);
        assertTrue(
                ApplicationLinkLauncher.isActionSupported(Desktop.Action.OPEN),
                "Expected OPEN action to be supported");
    }

    @Test
    public void isActionSupported_editActionSupported_returnsTrue() {
        when(mockDesktopWrapper.isSupported(Desktop.Action.EDIT)).thenReturn(true);
        assertTrue(
                ApplicationLinkLauncher.isActionSupported(Desktop.Action.EDIT),
                "Expected EDIT action to be supported");
    }

    @Test
    public void isActionSupported_printActionSupported_returnsTrue() {
        when(mockDesktopWrapper.isSupported(Desktop.Action.PRINT)).thenReturn(true);
        assertTrue(
                ApplicationLinkLauncher.isActionSupported(Desktop.Action.PRINT),
                "Expected PRINT action to be supported");
    }

    @Test
    public void getDesktopWrapper_nullDesktop_returnsDummyWrapper() {
        ApplicationLinkLauncher.setDesktopWrapper(null);
        DesktopWrapper wrapper = ApplicationLinkLauncher.getDesktopWrapper();
        assertNotNull(wrapper, "Expected fallback DummyDesktopWrapper when wrapper is null");
    }
}
