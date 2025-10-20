package seedu.address.logic.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * A dummy implementation of DesktopWrapper that does nothing.
 * Used for testing purposes.
 */
public class DummyDesktopWrapper implements DesktopWrapper {
    @Override
    public boolean isSupported(Desktop.Action action) {
        return false;
    }

    @Override
    public void mail(URI uri) throws IOException {
        throw new UnsupportedOperationException("Dummy wrapper — mail not supported");
    }

    @Override
    public void browse(URI uri) throws IOException {
        throw new UnsupportedOperationException("Dummy wrapper — browse not supported");
    }
}
