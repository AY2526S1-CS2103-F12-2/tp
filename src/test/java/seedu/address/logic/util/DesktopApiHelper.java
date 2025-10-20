package seedu.address.logic.util;

import java.net.URI;

/**
 * A helper class to expose the protected methods of DesktopApi for testing.
 */
public class DesktopApiHelper extends DesktopApi {
    public static String[] prepareCommand(String cmd, String args, String file) {
        return DesktopApi.prepareCommand(cmd, args, file);
    }

    public static boolean runCommand(String cmd, String args, String file) {
        return DesktopApi.runCommand(cmd, args, file);
    }

    public static boolean browseDesktop(URI uri) {
        return DesktopApi.browseDesktop(uri);
    }

    public static boolean openSystemSpecific(String what) {
        return DesktopApi.openSystemSpecific(what);
    }
}
