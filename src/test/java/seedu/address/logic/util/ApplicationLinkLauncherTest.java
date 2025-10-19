package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import org.junit.jupiter.api.Test;

public class ApplicationLinkLauncherTest {

    @Test
    public void parseToUri_validLink_success() throws Exception {
        Method parseMethod = ApplicationLinkLauncher.class
                .getDeclaredMethod("parseToUri", String.class);
        parseMethod.setAccessible(true);

        URI uri = (URI) parseMethod.invoke(null, "https://example.com");
        assertEquals("https://example.com", uri.toString());
    }

    @Test
    public void launchEmail_validEmail_returnsSuccess() {
        ApplicationLinkResult result =
                ApplicationLinkLauncher.launchEmail("test@example.com");
        // we cannot verify actual browser open, so only assert message consistency
        assertNotNull(result);
        assertTrue(result.getMessage().contains("EMAIL"));
    }


}
