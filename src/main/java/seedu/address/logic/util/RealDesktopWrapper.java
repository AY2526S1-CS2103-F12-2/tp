package seedu.address.logic.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * A real implementation of DesktopWrapper that uses java.awt.Desktop.
 */
public class RealDesktopWrapper implements DesktopWrapper {
    private final Desktop desktop = Desktop.getDesktop();

    public boolean isSupported(Desktop.Action action) {
        return desktop.isSupported(action);
    }

    public void mail(URI uri) throws IOException {
        desktop.mail(uri);
    }

    public void browse(URI uri) throws IOException {
        desktop.browse(uri);
    }
}
