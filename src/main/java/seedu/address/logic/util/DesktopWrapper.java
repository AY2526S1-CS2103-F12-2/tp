package seedu.address.logic.util;

import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;

/**
 * A wrapper interface for java.awt.Desktop to facilitate testing.
 */
public interface DesktopWrapper {
    boolean isSupported(Action action);

    void mail(URI uri) throws IOException;

    void browse(URI uri) throws IOException;
}
