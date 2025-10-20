package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class RealDesktopWrapperTest {
    private Desktop mockDesktop;
    private RealDesktopWrapper desktopWrapper;

    @BeforeEach
    void setUp() {
        mockDesktop = mock(Desktop.class);

        // Use a subclass to inject the mock Desktop
        desktopWrapper = new RealDesktopWrapper() {
            @Override
            public boolean isSupported(Desktop.Action action) {
                return mockDesktop.isSupported(action);
            }

            @Override
            public void mail(URI uri) throws IOException {
                mockDesktop.mail(uri);
            }

            @Override
            public void browse(URI uri) throws IOException {
                mockDesktop.browse(uri);
            }
        };
    }

    @Test
    void isSupported_returnsTrueIfDesktopSupportsAction() {
        when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);

        assertTrue(desktopWrapper.isSupported(Desktop.Action.BROWSE));
        verify(mockDesktop).isSupported(Desktop.Action.BROWSE);
    }

    @Test
    void isSupported_returnsFalseIfDesktopDoesNotSupportAction() {
        when(mockDesktop.isSupported(Desktop.Action.MAIL)).thenReturn(false);

        assertFalse(desktopWrapper.isSupported(Desktop.Action.MAIL));
        verify(mockDesktop).isSupported(Desktop.Action.MAIL);
    }

    @Test
    void mail_delegatesToDesktop() throws IOException {
        URI uri = URI.create("mailto:test@example.com");

        desktopWrapper.mail(uri);

        verify(mockDesktop).mail(uri);
    }

    @Test
    void mail_throwsIOExceptionIfDesktopThrows() throws IOException {
        URI uri = URI.create("mailto:test@example.com");
        doThrow(IOException.class).when(mockDesktop).mail(uri);

        assertThrows(IOException.class, () -> desktopWrapper.mail(uri));
    }

    @Test
    void browse_delegatesToDesktop() throws IOException {
        URI uri = URI.create("https://example.com");

        desktopWrapper.browse(uri);

        verify(mockDesktop).browse(uri);
    }

    @Test
    void browse_throwsIOExceptionIfDesktopThrows() throws IOException {
        URI uri = URI.create("https://example.com");
        doThrow(IOException.class).when(mockDesktop).browse(uri);

        assertThrows(IOException.class, () -> desktopWrapper.browse(uri));
    }

    @Test
    void constructor_DesktopNotSupported_throwsUnsupportedOperationException() {
        try (MockedStatic<Desktop> mockedStatic = mockStatic(Desktop.class)) {
            mockedStatic.when(Desktop::getDesktop).thenThrow(UnsupportedOperationException.class);

            assertThrows(UnsupportedOperationException.class, RealDesktopWrapper::new);
        }
    }
}
