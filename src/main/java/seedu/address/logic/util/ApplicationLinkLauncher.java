package seedu.address.logic.util;

import static java.util.Objects.requireNonNull;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class to launch external application links such as email, Telegram, and GitHub.
 */
public class ApplicationLinkLauncher {

    public static final String MESSAGE_TELEGRAM_NOTE =
            "Note: You can only launch Telegram links from the browser if you have the"
                    + " Telegram application installed on your device.";
    public static final String MESSAGE_SUCCESS = "Launched %s successfully.\n" + MESSAGE_TELEGRAM_NOTE;
    public static final String MESSAGE_FAILURE = "Failed to launch %s.";


    private static final String LAUNCH_EMAIL_PREFIX = "mailto:";
    private static final String LAUNCH_TELEGRAM_PREFIX = "https://t.me/";
    private static final String LAUNCH_GITHUB_PREFIX = "http://github.com/";

    private static DesktopWrapper desktopWrapper;

    static DesktopWrapper getDesktopWrapper() {
        if (desktopWrapper == null) {
            try {
                desktopWrapper = new RealDesktopWrapper();
            } catch (Exception e) {
                // CI or headless mode — fall back to a dummy wrapper
                desktopWrapper = new DummyDesktopWrapper();
            }
        }
        return desktopWrapper;
    }

    public static void setDesktopWrapper(DesktopWrapper wrapper) {
        desktopWrapper = wrapper; // assign to the static field
    }

    /**
     * Enum representing different application types.
     */
    public enum ApplicationType {
        EMAIL,
        TELEGRAM,
        GITHUB,
        UNKOWNN
    }

    /**
     * Launches the email application with the specified email address.
     *
     * @param email The email address to launch.
     * @return The result of the launch attempt.
     */
    public static ApplicationLinkResult launchEmail(String email) {
        return launchApplicationLink(LAUNCH_EMAIL_PREFIX + email, ApplicationType.EMAIL);
    }

    public static ApplicationLinkResult launchTelegram(String handle) {
        return launchApplicationLink(LAUNCH_TELEGRAM_PREFIX + handle, ApplicationType.TELEGRAM);
    }

    public static ApplicationLinkResult launchGithub(String username) {
        return launchApplicationLink(LAUNCH_GITHUB_PREFIX + username, ApplicationType.GITHUB);
    }

    protected static ApplicationLinkResult launchApplicationLink(String link, ApplicationType type) {
        try {
            URI uri = parseToUri(link);
            openLink(uri);
            return new ApplicationLinkResult(true, String.format(MESSAGE_SUCCESS, type));
        } catch (URISyntaxException | IOException e) {
            return new ApplicationLinkResult(false, String.format(MESSAGE_FAILURE, type));
        }
    }

    private static URI parseToUri(String link) throws URISyntaxException {
        return new URI(link);
    }

    /**
     * Opens the given URI using the Desktop API, with a fallback for unsupported systems.
     *
     * @param uri The URI to be opened.
     * @throws IOException if both Desktop and fallback methods fail to open the link.
     */
    private static void openLink(URI uri) throws IOException {
        requireNonNull(uri);

        boolean success = attemptOpenWithDesktop(uri);
        if (!success) {
            System.err.println("Desktop browse/mail failed, attempting fallback...");
            success = tryOpenWithFallback(uri);
            if (!success) {
                throw new IOException("Failed to open link via both Desktop and DesktopAPI.");
            }
        }
    }

    /**
     * Attempts to open the link using the java.awt.Desktop API.
     *
     * @return <code>true</code> if the link was successfully opened.
     */
    protected static boolean attemptOpenWithDesktop(URI uri) throws IOException, UnsupportedOperationException {
        if (getDesktopWrapper() == null && !Desktop.isDesktopSupported()) {
            return false;
        }
        try {
            // Use wrapper if set
            String scheme = uri.getScheme();
            if ("mailto".equalsIgnoreCase(scheme) && getDesktopWrapper().isSupported(Action.MAIL)) {
                getDesktopWrapper().mail(uri);
                return true;
            } else if (getDesktopWrapper().isSupported(Action.BROWSE)) {
                getDesktopWrapper().browse(uri);
                return true;
            }
            System.err.println("Desktop action not supported for: " + scheme);
            return false;
        } catch (IOException | UnsupportedOperationException e) {
            System.err.println("Desktop API failed: " + e.getMessage());
        }

        return false;
    }

    /**
     * Fallback method for systems that don't support Desktop API (e.g., Linux).
     * Relying on custom DesktopAPI class to handle link opening.
     *
     * @return <code>true</code> if the link was successfully opened.
     */
    private static boolean tryOpenWithFallback(URI uri) {
        boolean success = DesktopApi.browse(uri);
        if (!success) {
            System.err.println("Fallback DesktopAPI failed to open link: " + uri);
        }
        return success;
    }

    protected static boolean isActionSupported(Action action) {
        requireNonNull(getDesktopWrapper());
        requireNonNull(action);

        switch (action) {
        case BROWSE:
            return desktopWrapper.isSupported(Action.BROWSE);
        case MAIL:
            return desktopWrapper.isSupported(Action.MAIL);
        case OPEN:
            return desktopWrapper.isSupported(Action.OPEN);
        case EDIT:
            return desktopWrapper.isSupported(Action.EDIT);
        case PRINT:
            return desktopWrapper.isSupported(Action.PRINT);
        default:
            return false;
        }

    }
}
