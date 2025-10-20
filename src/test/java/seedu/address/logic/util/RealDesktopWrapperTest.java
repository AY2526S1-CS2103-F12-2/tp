package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import java.awt.Desktop;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class RealDesktopWrapperTest {

    @Test
    void constructor_desktopNotSupported_throwsUnsupportedOperationException() {
        try (MockedStatic<Desktop> mockedStatic = mockStatic(Desktop.class)) {
            mockedStatic.when(Desktop::getDesktop).thenThrow(UnsupportedOperationException.class);

            assertThrows(UnsupportedOperationException.class, RealDesktopWrapper::new);
        }
    }
}
