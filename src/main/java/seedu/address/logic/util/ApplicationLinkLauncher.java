package seedu.address.logic.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ApplicationLinkLauncher {

    private static Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

    static void setDesktop(Desktop mockDesktop) {
        desktop = mockDesktop;
    }

    private static final String LAUNCH_EMAIL_PREFIX = "mailto:";
    private static final String LAUNCH_TELEGRAM_PREFIX = "https://t.me/";
    private static final String LAUNCH_GITHUB_PREFIX = "http://github.com/";

    protected static final String MESSAGE_SUCCESS = "Launched %s successfully.";
    protected static final String MESSAGE_FAILURE = "Failed to launch %s.";

    public enum ApplicationType {
        EMAIL,
        TELEGRAM,
        GITHUB
    }

    public static ApplicationLinkResult  launchEmail(String email) {
        return launchApplicationLink(LAUNCH_EMAIL_PREFIX + email, ApplicationType.EMAIL);
    }

    public static ApplicationLinkResult  launchTelegram(String handle) {
        return launchApplicationLink(LAUNCH_TELEGRAM_PREFIX + handle, ApplicationType.TELEGRAM);
    }

    public static ApplicationLinkResult  launchGithub(String username) {
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
        assert uri != null : "URI should not be null when opening link.";
        if (desktop != null) {
            desktop.browse(uri);
        } else {
            throw new IOException();
        }
    }
}
