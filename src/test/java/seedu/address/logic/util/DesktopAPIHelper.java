package seedu.address.logic.util;

import java.net.URI;

public class DesktopAPIHelper extends DesktopAPI {
    public static String[] prepareCommand(String cmd, String args, String file) {
        return DesktopAPI.prepareCommand(cmd, args, file);
    }
    public static boolean runCommand(String cmd, String args, String file) {
        return DesktopAPI.runCommand(cmd, args, file);
    }
    public static boolean browseDESKTOP(URI uri) {
        return DesktopAPI.browseDESKTOP(uri);
    }
    public static boolean openSystemSpecific(String what) {
        return DesktopAPI.openSystemSpecific(what);
    }
}