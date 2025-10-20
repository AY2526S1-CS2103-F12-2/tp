package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DummyDesktopWrapper}.
 */
public class DummyDesktopWrapperTest {

    private DummyDesktopWrapper dummyWrapper;
    private URI sampleUri;

    @BeforeEach
    public void setup() throws URISyntaxException {
        dummyWrapper = new DummyDesktopWrapper();
        sampleUri = new URI("https://example.com");
    }

    @Test
    public void isSupported_allActions_returnFalse() {
        // Verify that no Desktop.Action is supported
        for (Desktop.Action action : Desktop.Action.values()) {
            assertFalse(dummyWrapper.isSupported(action),
                    "Expected isSupported(" + action + ") to return false");
        }
    }

    @Test
    public void mail_anyUri_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> dummyWrapper.mail(sampleUri),
                "Expected mail() to throw UnsupportedOperationException");
    }

    @Test
    public void browse_anyUri_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> dummyWrapper.browse(sampleUri),
                "Expected browse() to throw UnsupportedOperationException");
    }

    @Test
    public void mail_nullUri_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> dummyWrapper.mail(null),
                "Expected mail(null) to throw UnsupportedOperationException");
    }

    @Test
    public void browse_nullUri_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> dummyWrapper.browse(null),
                "Expected browse(null) to throw UnsupportedOperationException");
    }
}
