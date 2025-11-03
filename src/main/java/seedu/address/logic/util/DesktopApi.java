package seedu.address.logic.util;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * A utility class to open files and browse URIs using the system's default applications.
 * <p>
 * This class provides OS-specific fallbacks for systems where {@link java.awt.Desktop} may not be fully supported.
 * </p>
 *
 * <p>
 * Credits:Adapted from
 * <a
 * href="https://stackoverflow.com/questions/18004150/desktop-api-is-not-supported-on-the-current-platform">
 * StackOverflow: Desktop API is not supported on the current platform
 * </a>
 * </p>
 * <p>
 * Note: These JavaDoc Headers were not provided by the original credited author, but by the developer
 * <a href="https://github.com/MoshiMoshiMochi/">
 * MoshiMoshiMochi
 * </a>
 * implementing this. Hence, these documentations may not be exactly what the original author envisioned
 * </p>
 */
public class DesktopApi {

    // Constants for Commands & Args
    private static final String KDE_OPEN = "kde-open";
    private static final String GNOME_OPEN = "gnome-open";
    private static final String XDG_OPEN = "xdg-open";
    private static final String MAC_OPEN = "open";
    private static final String WINDOWS_EXPLORER = "explorer";
    private static final String ARG_FORMAT = "%s";

    // Constants for Log Messages
    private static final String LOG_PREFIX_TRY_EXEC = "Trying to exec:\n   cmd = ";
    private static final String LOG_ARGS = "\n   args = ";
    private static final String LOG_FILE = "\n   %s = ";
    private static final String LOG_PLATFORM_NOT_SUPPORTED = "Platform is not supported.";
    private static final String LOG_BROWSE_NOT_SUPPORTED = "BROWSE is not supported.";
    private static final String LOG_OPEN_NOT_SUPPORTED = "OPEN is not supported.";
    private static final String LOG_EDIT_NOT_SUPPORTED = "EDIT is not supported.";
    private static final String LOG_ACTION_NOT_SUPPORTED = "ACTION %s is not supported";
    private static final String LOG_BROWSE_ERROR = "Error using desktop browse.";
    private static final String LOG_OPEN_ERROR = "Error using desktop open.";
    private static final String LOG_EDIT_ERROR = "Error using desktop edit.";
    private static final String LOG_PROCESS_ENDED = "Process ended immediately.";
    private static final String LOG_PROCESS_CRASHED = "Process crashed.";
    private static final String LOG_PROCESS_RUNNING = "Process is running.";
    private static final String LOG_PROCESS_ALL_FAILED = "All Process failed to run";
    private static final String LOG_RUN_COMMAND_ERROR = "Error running command.";
    private static final String LOG_TRY_BROWSE = "Trying to use Desktop.getDesktop().browse() with ";
    private static final String LOG_TRY_OPEN = "Trying to use Desktop.getDesktop().open() with ";
    private static final String LOG_TRY_EDIT = "Trying to use Desktop.getDesktop().edit() with ";

    // OS Name Substring Constants
    private static final String OS_NAME_PROPERTY = "os.name";
    private static final String OS_WIN = "win";
    private static final String OS_MAC = "mac";
    private static final String OS_SOLARIS = "solaris";
    private static final String OS_SUNOS = "sunos";
    private static final String OS_LINUX = "linux";
    private static final String OS_UNIX = "unix";

    private static Logger logger = LogsCenter.getLogger(DesktopApi.class);

    /**
     * Opens the default web browser to browse the given URI.
     * <p>
     * Attempts a system-specific command first, then falls back to using the Java {@link Desktop#browse(URI)} API
     * if available.
     * </p>
     *
     * @param uri The URI to be browsed.
     * @return {@code true} if the operation was successful, {@code false} otherwise.
     */
    public static boolean browse(URI uri) {

        boolean isSuccess = openSystemSpecific(uri.toString());
        if (!isSuccess) {
            isSuccess = browseDesktop(uri);
        }

        return isSuccess;
    }

    /**
     * Attempts to open the specified target (e.g. URL or file) using an OS-specific command.
     *
     * @param what the target string (URL or file path) to open
     * @return {@code true} if a command succeeds, {@code false} otherwise
     */
    static boolean openSystemSpecific(String what) {

        EnumOS os = getOs();

        if (os.isLinux()) {
            if (runCommand(KDE_OPEN, ARG_FORMAT, what)) {
                return true;
            } else if (runCommand(GNOME_OPEN, ARG_FORMAT, what)) {
                return true;
            } else if (runCommand(XDG_OPEN, ARG_FORMAT, what)) {
                return true;
            }
        }

        if (os.isMac()) {
            return runCommand(MAC_OPEN, ARG_FORMAT, what);
        }

        if (os.isWindows()) {
            return runCommand(WINDOWS_EXPLORER, ARG_FORMAT, what);
        }

        logger.info(LOG_PROCESS_ALL_FAILED);

        return false;
    }

    /**
     * Attempts to open the given URI using the Java Desktop API.
     *
     * @param uri the URI to browse
     * @return {@code true} if browsing succeeds, {@code false} otherwise
     */
    static boolean browseDesktop(URI uri) {

        logOut(LOG_TRY_BROWSE + uri.toString());
        try {
            if (!checkDesktopSupport(Action.BROWSE)) {
                return false;
            }

            // Some unsupported linux distro actually support the java.awt.Desktop
            // but for some reason crashes on browse
            // So, if a linux command somehow manages to get reach this point, the code will definitely crash
            Desktop.getDesktop().browse(uri);
            return true;

        } catch (Throwable t) {
            logErr(LOG_BROWSE_ERROR);
            return false;
        }
    }

    /**
     * Attempts to open the given file using the default system application.
     *
     * @param file the file to open
     * @return {@code true} if the file is successfully opened, {@code false} otherwise
     */
    static boolean openDesktop(File file) {

        logOut(LOG_TRY_OPEN + file);
        try {
            if (!checkDesktopSupport(Action.OPEN)) {
                return false;
            }

            Desktop.getDesktop().open(file);
            return true;

        } catch (Throwable t) {
            logErr(LOG_OPEN_ERROR);
            return false;
        }
    }

    /**
     * Attempts to edit the given file using the default system editor.
     *
     * @param file the file to edit
     * @return {@code true} if the file is successfully opened for editing, {@code false} otherwise
     */
    static boolean editDesktop(File file) {

        logOut(LOG_TRY_EDIT + file);
        try {
            if (!checkDesktopSupport(Action.EDIT)) {
                return false;
            }

            Desktop.getDesktop().edit(file);
            return true;

        } catch (Throwable t) {
            logErr(LOG_EDIT_ERROR);
            return false;
        }
    }

    private static boolean checkDesktopSupport(Action action) {
        if (!Desktop.isDesktopSupported()) {
            logErr(LOG_PLATFORM_NOT_SUPPORTED);
            return false;
        }

        Desktop desktop = Desktop.getDesktop();

        if (!desktop.isSupported(action)) {
            switch (action) {
            case EDIT: {
                logErr(LOG_EDIT_NOT_SUPPORTED);
                break;
            }
            case BROWSE: {
                logErr(LOG_BROWSE_NOT_SUPPORTED);
                break;
            }
            case OPEN: {
                logErr(LOG_OPEN_NOT_SUPPORTED);
                break;
            }
            default: {
                logErr(String.format(LOG_ACTION_NOT_SUPPORTED, action));
            }
            }
            return false;
        }

        return true;
    }

    /**
     * Executes a system command to open or edit a file/URI.
     *
     * @param command the system command (e.g., {@code "open"}, {@code "explorer"})
     * @param args    the command arguments (format string)
     * @param file    the target file or URI
     * @return {@code true} if the command executes successfully, {@code false} otherwise
     */
    static boolean runCommand(String command, String args, String file) {

        logOut(LOG_PREFIX_TRY_EXEC + command + LOG_ARGS + args + LOG_FILE + file);

        String[] parts = prepareCommand(command, args, file);

        try {
            Process p = Runtime.getRuntime().exec(parts);
            if (p == null) {
                return false;
            }

            try {
                int retval = p.exitValue();
                if (retval == 0) {
                    logErr(LOG_PROCESS_ENDED);
                    return false;
                } else {
                    logErr(LOG_PROCESS_CRASHED);
                    return false;
                }
            } catch (IllegalThreadStateException itse) {
                logErr(LOG_PROCESS_RUNNING);
                return true;
            }
        } catch (IOException e) {
            logErr(LOG_RUN_COMMAND_ERROR);
            return false;
        }
    }

    /**
     * Builds the full command-line instruction from the given parameters.
     *
     * @param command the base command
     * @param args    command arguments (may include {@code "%s"} placeholders)
     * @param file    the file or URI to substitute into the arguments
     * @return an array representing the full command for execution
     */
    static String[] prepareCommand(String command, String args, String file) {

        List<String> parts = new ArrayList<String>();
        parts.add(command);

        if (args != null) {
            for (String s : args.split(" ")) {
                s = String.format(s, file); // put in the filename thing

                parts.add(s.trim());
            }
        }

        return parts.toArray(new String[parts.size()]);
    }

    private static void logErr(String msg) {
        logger.fine(msg);
    }

    private static void logOut(String msg) {
        logger.info(msg);
    }

    /**
     * Enum to represent the operating system types.
     */
    public static enum EnumOS {
        linux, macos, solaris, unknown, windows;

        public boolean isLinux() {
            return this == linux || this == solaris;
        }

        public boolean isMac() {
            return this == macos;
        }

        public boolean isWindows() {
            return this == windows;
        }
    }

    /**
     * Determines the current operating system from the system properties.
     *
     * @return an {@link EnumOS} representing the detected OS type, or {@link EnumOS#unknown} if unrecognized
     */
    public static EnumOS getOs() {

        String osName = System.getProperty(OS_NAME_PROPERTY).toLowerCase();

        if (osName.contains(OS_WIN)) {
            return EnumOS.windows;
        }
        if (osName.contains(OS_MAC)) {
            return EnumOS.macos;
        }
        if (osName.contains(OS_SOLARIS) || osName.contains(OS_SUNOS)) {
            return EnumOS.solaris;
        }
        if (osName.contains(OS_LINUX) || osName.contains(OS_UNIX)) {
            return EnumOS.linux;
        }
        return EnumOS.unknown;
    }
}
