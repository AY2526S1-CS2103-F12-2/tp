package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_TELEGRAM = new Prefix("l/");
    public static final Prefix PREFIX_GITHUB = new Prefix("g/");
    public static final Prefix PREFIX_PREFERRED_MODE = new Prefix("pm/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_REMOVE_TAG = new Prefix("r/");

    /* Flag definitions (for List Command) */
    public static final Prefix FLAG_ALPHABETICAL_ORDER = new Prefix("-a");
    public static final Prefix FLAG_RECENT_ORDER = new Prefix("-r");

    /* Flag definitions (for Launch Command) */
    public static final Prefix FLAG_EMAIL_LAUNCH = new Prefix("-e");
    public static final Prefix FLAG_TELEGRAM_LAUNCH = new Prefix("-l");
    public static final Prefix FLAG_GITHUB_LAUNCH = new Prefix("-g");

    /* Flag & Prefix definitions (for TagCommand) */
    public static final Prefix FLAG_RENAME_TAG = new Prefix("-r");
    public static final Prefix FLAG_DELETE_TAG = new Prefix("-d");
    public static final Prefix PREFIX_TARGET_TAG = new Prefix("t/");
    public static final Prefix PREFIX_RENAMED_TAG = new Prefix("r/");
}
