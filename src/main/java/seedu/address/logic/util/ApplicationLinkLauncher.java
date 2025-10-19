package seedu.address.logic.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ApplicationLinkLauncher {

    private static final String LAUNCH_EMAIL_PREFIX = "mailto:";
    private static final String LAUNCH_TELEGRAM_PREFIX = "https://t.me/";
    private static final String LAUNCH_GITHUB_PREFIX = "http://github.com/";

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

    private static ApplicationLinkResult launchApplicationLink(String link, ApplicationType type) {
        try {
            URI uri = parseToUri(link);
            openLink(uri);
            // Result Display SUCCESS
            return new ApplicationLinkResult(true, type + " link opened successfully.");
        } catch (URISyntaxException | IOException e) {
            System.out.println("FAHH");
            // Result Display ERROR
            return new ApplicationLinkResult(false, "Failed to open " + type + " link.");
        }
    }

    private static URI parseToUri(String link) throws URISyntaxException {
        return new URI(link);
    }

    private static void openLink(URI uri) throws IOException {
        Desktop.getDesktop().browse(uri);
    }
}
