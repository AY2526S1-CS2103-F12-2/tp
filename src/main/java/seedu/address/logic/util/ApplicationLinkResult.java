package seedu.address.logic.util;

/**
 * Represents the result of an attempt to link to an external application.
 */
public class ApplicationLinkResult {
    private final boolean success;
    private final String message;

    /**
     * Constructs an ApplicationLinkResult.
     *
     * @param success whether the link attempt was successful
     * @param message the message associated with the link attempt
     */
    public ApplicationLinkResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
