package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ApplicationLinkResultTest {

    @Test
    public void constructor_andGetters_workCorrectly() {
        ApplicationLinkResult successResult =
                new ApplicationLinkResult(true, "Email opened successfully.");

        assertTrue(successResult.isSuccess());
        assertEquals("Email opened successfully.", successResult.getMessage());

        ApplicationLinkResult failResult =
                new ApplicationLinkResult(false, "Failed to open link.");

        assertFalse(failResult.isSuccess());
        assertEquals("Failed to open link.", failResult.getMessage());
    }

}
