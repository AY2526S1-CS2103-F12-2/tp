package seedu.address.logic.util;

import static java.util.Objects.requireNonNull;

import java.awt.Desktop;
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

    private static Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

    /**
     * Sets a mock Desktop instance for testing purposes.
     *
     * @param mockDesktop The mock Desktop instance to be used.
     */
    public static void setDesktop(Desktop mockDesktop) {
        desktop = mockDesktop;
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

    private static void openLink(URI uri) throws IOException {
        requireNonNull(uri);
        if (desktop != null) {
            desktop.browse(uri);
        } else {
            throw new IOException();
        }
    }
}
