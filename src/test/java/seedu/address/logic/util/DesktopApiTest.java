package seedu.address.logic.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
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

public class DesktopApiTest {
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
        assertEquals(DesktopApi.EnumOS.windows, DesktopApi.getOs());
    }

    @Test
    void getOs_returnsMac_whenSystemPropertyContainsMac() {
        System.setProperty("os.name", "Mac OS X");
        assertEquals(DesktopApi.EnumOS.macos, DesktopApi.getOs());
    }

    @Test
    void getOs_returnsLinux_whenSystemPropertyContainsLinux() {
        System.setProperty("os.name", "Linux");
        assertTrue(DesktopApi.getOs().isLinux());

        System.setProperty("os.name", "Solaris");
        assertTrue(DesktopApi.getOs().isLinux());

        System.setProperty("os.name", "sunos");
        assertTrue(DesktopApi.getOs().isLinux());

        System.setProperty("os.name", "unix");
        assertTrue(DesktopApi.getOs().isLinux());
    }

    @Test
    void getOs_returnsUnknown_whenSystemPropertyIsRandom() {
        System.setProperty("os.name", "SomeOS");
        assertEquals(DesktopApi.EnumOS.unknown, DesktopApi.getOs());
    }

    @Test
    public void enumOS_isMac_returnsTrue() {
        assertTrue(DesktopApi.EnumOS.macos.isMac());
    }

    @Test
    public void enumOS_isMac_returnsFalse() {
        assertFalse(DesktopApi.EnumOS.unknown.isMac());
    }

    @Test
    public void enumOS_isWindows_returnsFalse() {
        assertTrue(DesktopApi.EnumOS.windows.isWindows());
    }

    @Test
    void prepareCommand_formatsArgumentsCorrectly() {
        String[] result = invokePrepareCommand("xdg-open", "%s --flag", "file.txt");
        assertArrayEquals(new String[]{"xdg-open", "file.txt", "--flag"}, result);
    }

    private String[] invokePrepareCommand(String cmd, String args, String file) {
        return DesktopApiHelper.prepareCommand(cmd, args, file);
    }

    @Test
    void runCommand_returnsTrue_whenProcessIsRunning() throws Exception {
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class)) {
            Process mockProcess = mock(Process.class);
            doThrow(new IllegalThreadStateException()).when(mockProcess).exitValue();

            Runtime mockRuntime = mock(Runtime.class);
            when(mockRuntime.exec(any(String[].class))).thenReturn(mockProcess);
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            assertTrue(DesktopApiHelper.runCommand("echo", "%s", "test"));
        }
    }

    @Test
    void runCommand_returnsFalse_whenExceptionThrown() throws Exception {
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class)) {
            Runtime mockRuntime = mock(Runtime.class);
            when(mockRuntime.exec(any(String[].class))).thenThrow(new IOException("error"));
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            assertFalse(DesktopApiHelper.runCommand("invalid", "%s", "test"));
        }
    }

    @Test
    void runCommand_returnsFalseWhenProcessEndsImmediately_exitValueZero() throws Exception {
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class)) {
            Process mockProcess = mock(Process.class);
            when(mockProcess.exitValue()).thenReturn(0); // simulate process ends immediately

            Runtime mockRuntime = mock(Runtime.class);
            when(mockRuntime.exec(any(String[].class))).thenReturn(mockProcess);
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            assertFalse(DesktopApiHelper.runCommand("echo", "%s", "test"));
        }
    }

    @Test
    void runCommand_returnsFalseWhenProcessCrashes_exitValueNonZero() throws Exception {
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class)) {
            Process mockProcess = mock(Process.class);
            when(mockProcess.exitValue()).thenReturn(1); // simulate process crashed

            Runtime mockRuntime = mock(Runtime.class);
            when(mockRuntime.exec(any(String[].class))).thenReturn(mockProcess);
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            assertFalse(DesktopApiHelper.runCommand("echo", "%s", "test"));
        }
    }

    @Test
    void browseDesktop_returnsFalseWhenDesktopNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);
            assertFalse(DesktopApiHelper.browseDesktop(mockUri));
        }
    }

    @Test
    void browseDesktop_returnsFalse_whenActionNotSupported() {
        try (MockedStatic<DesktopApi> apiMock = mockStatic(DesktopApi.class, CALLS_REAL_METHODS)) {
            apiMock.when(() -> DesktopApi.openSystemSpecific(any())).thenReturn(true);
            assertTrue(DesktopApi.browse(mockUri));
        }
    }

    @Test
    void browseDesktop_returnsTrue_whenSupportedAndNoError() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.BROWSE)).thenReturn(true);

            assertTrue(DesktopApiHelper.browseDesktop(mockUri));
            verify(mockDesktop).browse(mockUri);
        }
    }

    @Test
    void openDesktop_returnsFalse_whenDesktopNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);
            var method = DesktopApi.class.getDeclaredMethod("openDesktop", File.class);
            method.setAccessible(true);

            boolean result = (boolean) method.invoke(null, mockFile);
            assertFalse(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e); // This should not happen
        }
    }

    @Test
    void openDesktop_returnsFalse_whenActionNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(false);
            assertFalse(invokeOpenDesktop(mockFile));
        }
    }

    @Test
    void openDesktop_returnsTrue_whenSupported() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(true);
            assertTrue(invokeOpenDesktop(mockFile));
        }
    }

    @Test
    void openDesktop_returnsFalse_whenExceptionThrown() throws Exception {
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
    void editDesktop_returnsFalse_whenNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);
            assertFalse(invokeEditDesktop(mockFile));
        }
    }

    @Test
    void editDesktop_returnsFalse_whenActionNotSupported() {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.EDIT)).thenReturn(false);
            assertFalse(invokeEditDesktop(mockFile));
        }
    }

    @Test
    void editDesktop_returnsTrue_whenSupported() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class, CALLS_REAL_METHODS)) {
            Desktop mockDesktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.EDIT)).thenReturn(true);
            assertTrue(invokeEditDesktop(mockFile));
        }
    }

    @Test
    void editDesktop_returnsFalse_whenDesktopNotSupported() throws Exception {
        try (MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);

            var method = DesktopApi.class.getDeclaredMethod("editDesktop", File.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(null, mockFile);
            assertFalse(result);
        }
    }

    @Test
    void editDesktop_returnsFalse_whenExceptionThrown() throws Exception {
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
            var method = DesktopApi.class.getDeclaredMethod("openDesktop", File.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokeEditDesktop(File f) {
        try {
            var method = DesktopApi.class.getDeclaredMethod("editDesktop", File.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
