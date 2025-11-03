package seedu.address.logic.util;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Utility class to launch external application links such as User Guide, Telegram and GitHub.
 */
public class ApplicationLinkLauncher {

    public static final String MESSAGE_TELEGRAM_NOTE =
            "Note: You can only launch Telegram links from the browser if you have the"
                    + " Telegram application installed on your device.";
    public static final String MESSAGE_SUCCESS = "Launched %s successfully.\n" + MESSAGE_TELEGRAM_NOTE;
    public static final String MESSAGE_FAILURE = "Failed to launch %s.";

    private static final String MESSAGE_DESKTOP_API_LAUNCH_FAIL = "DesktopAPI failed to open link: %s";

    private static final String LAUNCH_NO_PREFIX = "";
    private static final String LAUNCH_TELEGRAM_PREFIX = "https://t.me/";
    private static final String LAUNCH_GITHUB_PREFIX = "http://github.com/";

    private static final String USERGUIDE_URL = "https://ay2526s1-cs2103-f12-2.github.io/tp/UserGuide.html";

    private static Logger logger = LogsCenter.getLogger(ApplicationLinkLauncher.class);

    private static ApplicationLinkResult launchApp(String prefix, String value, ApplicationType type) {
        requireNonNull(value);
        return launchApplicationLink(prefix + value, type);
    }

    /**
     * Launches the telegram web with the specified handle.
     *
     * @param handle The telegram handle to launch.
     * @return The result of the launch attempt.
     */
    public static ApplicationLinkResult launchTelegram(String handle) {
        return launchApp(LAUNCH_TELEGRAM_PREFIX, handle, ApplicationType.TELEGRAM);
    }

    /**
     * Launches the GitHub page of the specified username.
     *
     * @param username of the GitHub page to launch.
     * @return The result of the launch attempt.
     */
    public static ApplicationLinkResult launchGithub(String username) {
        return launchApp(LAUNCH_GITHUB_PREFIX, username, ApplicationType.GITHUB);
    }

    /**
     * Launches the user guide of Devbooks.
     *
     * @return The result of the launch attempt.
     */
    public static ApplicationLinkResult launchUserGuide() {
        return launchApp(LAUNCH_NO_PREFIX, USERGUIDE_URL, ApplicationType.USERGUIDE);
    }

    /**
     * Attempts to launch the application, and return the results of its attempt
     *
     * @param link to be parsed to create a {@code URI} instance
     * @param type helps display {@code ApplicationType} it is attempting to launch
     * @return {@code ApplicationLinkResult} from its attempt of launching the application.
     */
    protected static ApplicationLinkResult launchApplicationLink(String link, ApplicationType type) {
        try {
            URI uri = parseToUri(link);
            tryOpenWithDesktopApi(uri);
            return new ApplicationLinkResult(true, String.format(MESSAGE_SUCCESS, type));
        } catch (URISyntaxException | IOException e) {
            return new ApplicationLinkResult(false, String.format(MESSAGE_FAILURE, type));
        }
    }

    private static URI parseToUri(String link) throws URISyntaxException {
        return new URI(link);
    }

    /**
     * Relying on custom DesktopAPI class to handle link opening.
     *
     * @return <code>true</code> if the link was successfully opened.
     * @throws IOException if both DesktopApi fails to open the link.
     */
    private static void tryOpenWithDesktopApi(URI uri) throws IOException {
        requireNonNull(uri);
        boolean success = DesktopApi.browse(uri);
        if (!success) {
            String errorMessage = String.format(MESSAGE_DESKTOP_API_LAUNCH_FAIL, uri);
            logger.info(errorMessage);
            throw new IOException(errorMessage);
        }
    }

}
