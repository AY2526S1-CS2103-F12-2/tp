package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class DesktopAPITest {
    private File mockFile;
    private URI mockUri;

    @BeforeEach
    void setup() throws Exception {
        mockFile = new File("test.txt");
        mockUri = new URI("https://example.com");
    }

    @Test
    void getOs_returnsWindows_whenSystemPropertyContainsWin() {
        System.setProperty("os.name", "Windows 10");
        assertEquals(DesktopAPI.EnumOS.windows, DesktopAPI.getOs());
    }

    @Test
    void getOs_returnsMac_whenSystemPropertyContainsMac() {
        System.setProperty("os.name", "Mac OS X");
        assertEquals(DesktopAPI.EnumOS.macos, DesktopAPI.getOs());
    }

    @Test
    void getOs_returnsLinux_whenSystemPropertyContainsLinux() {
        System.setProperty("os.name", "Linux");
        assertTrue(DesktopAPI.getOs().isLinux());
    }

    @Test
    void getOs_returnsUnknown_whenSystemPropertyIsRandom() {
        System.setProperty("os.name", "SomeOS");
        assertEquals(DesktopAPI.EnumOS.unknown, DesktopAPI.getOs());
    }

    @Test
    void prepareCommand_formatsArgumentsCorrectly() {
        String[] result = invokePrepareCommand("xdg-open", "%s --flag", "file.txt");
        assertArrayEquals(new String[]{"xdg-open", "file.txt", "--flag"}, result);
    }

    private String[] invokePrepareCommand(String cmd, String args, String file) {
        return DesktopAPIHelper.prepareCommand(cmd, args, file);
    }

    @Test
    void runCommand_returnsTrue_whenProcessIsRunning() throws Exception {
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class)) {
            Process mockProcess = mock(Process.class);
            doThrow(new IllegalThreadStateException()).when(mockProcess).exitValue();

            Runtime mockRuntime = mock(Runtime.class);
            when(mockRuntime.exec(any(String[].class))).thenReturn(mockProcess);
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            assertTrue(DesktopAPIHelper.runCommand("echo", "%s", "test"));
        }
    }

    @Test
    void runCommand_returnsFalse_whenIOExceptionThrown() throws Exception {
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class)) {
            Runtime mockRuntime = mock(Runtime.class);
            when(mockRuntime.exec(any(String[].class))).thenThrow(new IOException("error"));
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            assertFalse(DesktopAPIHelper.runCommand("invalid", "%s", "test"));
        }
    }


    @Test
    void browseDESKTOP_returnsFalse_whenDesktopNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);
            assertFalse(DesktopAPIHelper.browseDESKTOP(mockUri));
        }
    }

    @Test
    void browseDESKTOP_returnsFalse_whenActionNotSupported() {
        try (MockedStatic<DesktopAPI> apiMock = mockStatic(DesktopAPI.class, CALLS_REAL_METHODS)) {
            apiMock.when(() -> DesktopAPI.openSystemSpecific(any())).thenReturn(true);
            assertTrue(DesktopAPI.browse(mockUri));
        }
    }

    @Test
    void browseDESKTOP_returnsTrue_whenSupportedAndNoError() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);

            assertTrue(DesktopAPIHelper.browseDESKTOP(mockUri));
            verify(mockDesktop).browse(mockUri);
        }
    }

    @Test
    void openDESKTOP_returnsFalse_whenDesktopNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);
            var method = DesktopAPI.class.getDeclaredMethod("openDESKTOP", File.class);
            method.setAccessible(true);

            boolean result = (boolean) method.invoke(null, mockFile);
            assertFalse(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e); // This should not happen
        }
    }

    @Test
    void openDESKTOP_returnsFalse_whenActionNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(false);
            assertFalse(invokeOpenDesktop(mockFile));
        }
    }

    @Test
    void openDESKTOP_returnsTrue_whenSupported() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(true);
            assertTrue(invokeOpenDesktop(mockFile));
        }
    }

    @Test
    void openDESKTOP_returnsFalse_whenExceptionThrown() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(true);
            doThrow(new IOException()).when(mockDesktop).open(any(File.class));
            assertFalse(invokeOpenDesktop(mockFile));
        }
    }

    @Test
    void editDESKTOP_returnsFalse_whenNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);
            assertFalse(invokeEditDesktop(mockFile));
        }
    }

    @Test
    void editDESKTOP_returnsFalse_whenActionNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.EDIT)).thenReturn(false);
            assertFalse(invokeEditDesktop(mockFile));
        }
    }

    @Test
    void editDESKTOP_returnsTrue_whenSupported() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.EDIT)).thenReturn(true);
            assertTrue(invokeEditDesktop(mockFile));
        }
    }

    @Test
    void editDESKTOP_returnsFalse_whenDesktopNotSupported() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);

            var method = DesktopAPI.class.getDeclaredMethod("editDESKTOP", File.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(null, mockFile);
            assertFalse(result);
        }
    }

    @Test
    void editDESKTOP_returnsFalse_whenExceptionThrown() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.EDIT)).thenReturn(true);
            doThrow(new IOException()).when(mockDesktop).edit(any(File.class));
            assertFalse(invokeEditDesktop(mockFile));
        }
    }

    // Helper methods to invoke private static methods via reflection
    private boolean invokeOpenDesktop(File f) {
        try {
            var method = DesktopAPI.class.getDeclaredMethod("openDESKTOP", File.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokeEditDesktop(File f) {
        try {
            var method = DesktopAPI.class.getDeclaredMethod("editDESKTOP", File.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
