package seedu.address.logic.util;

public class ApplicationLinkResult {
    private final boolean success;
    private final String message;

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
