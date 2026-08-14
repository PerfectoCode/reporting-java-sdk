package selenium;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.model.command.Command;
import com.perfecto.reportium.imports.model.command.CommandParameter;
import com.perfecto.reportium.imports.model.command.CommandStatus;
import org.apache.commons.codec.binary.Base64;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.easymock.EasyMock.capture;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link PerfectoWebDriverEventListenerImpl}
 */
public class PerfectoWebDriverEventListenerImplTest {

    private IMocksControl mocksControl;
    private ReportiumImportClient reportiumImportClientMock;
    private PerfectoWebDriverEventListenerImpl tested;

    /**
     * Non-static inner class: instances of it carry a synthetic "this$0" field pointing at this
     * test instance. Used to exercise the "enclosing object exists but is not a RemoteWebElement"
     * branch of the production code's coordinates-parameter extraction.
     */
    private class NonRemoteElementCoordinates implements Coordinates {
        @Override
        public Point onScreen() {
            return null;
        }

        @Override
        public Point inViewPort() {
            return null;
        }

        @Override
        public Point onPage() {
            return null;
        }

        @Override
        public Object getAuxiliary() {
            return null;
        }
    }

    @BeforeMethod
    public void beforeMethod() {
        mocksControl = EasyMock.createControl();
        reportiumImportClientMock = mocksControl.createMock(ReportiumImportClient.class);
        tested = new PerfectoWebDriverEventListenerImpl(reportiumImportClientMock);
    }

    private Capture<Command> expectCommand() {
        Capture<Command> capture = EasyMock.newCapture();
        reportiumImportClientMock.command(capture(capture));
        return capture;
    }

    private RemoteWebElement newRemoteWebElement(String id) {
        RemoteWebElement element = new RemoteWebElement();
        element.setId(id);
        return element;
    }

    private void assertNoParameters(Command command) {
        assertTrue(command.getParameters().isEmpty());
    }

    private void assertSingleParameter(Command command, String name, String value) {
        assertEquals(command.getParameters(), Arrays.asList(new CommandParameter(name, value)));
    }

    // ---------------------------------------------------------------- alerts

    @Test
    public void alertAccept() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeAlertAccept(null);
        tested.afterAlertAccept(null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "AlertAccept");
        assertEquals(command.getStatus(), CommandStatus.SUCCESS);
        assertNoParameters(command);
    }

    @Test
    public void alertDismiss() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeAlertDismiss(null);
        tested.afterAlertDismiss(null);

        mocksControl.verify();
        assertEquals(capture.getValue().getName(), "AlertDismiss");
    }

    // ------------------------------------------------------------ navigation

    @Test
    public void navigateTo() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeNavigateTo("http://example.com", null);
        tested.afterNavigateTo("http://example.com", null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "NavigateTo");
        assertSingleParameter(command, "url", "http://example.com");
    }

    @Test
    public void navigateBack() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeNavigateBack(null);
        tested.afterNavigateBack(null);

        mocksControl.verify();
        assertEquals(capture.getValue().getName(), "NavigateBack");
    }

    @Test
    public void navigateForward() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeNavigateForward(null);
        tested.afterNavigateForward(null);

        mocksControl.verify();
        assertEquals(capture.getValue().getName(), "NavigateForward");
    }

    @Test
    public void navigateRefresh() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeNavigateRefresh(null);
        tested.afterNavigateRefresh(null);

        mocksControl.verify();
        assertEquals(capture.getValue().getName(), "NavigateRefresh");
    }

    // ----------------------------------------------------------------- findBy

    @Test
    public void findBy() {
        By by = By.id("someId");
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeFindBy(by, null, null);
        tested.afterFindBy(by, null, null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "FindBy");
        assertSingleParameter(command, "by", by.toString());
    }

    // ---------------------------------------------------------------- clickOn

    @Test
    public void clickOn_withElement() {
        RemoteWebElement element = newRemoteWebElement("elementId");
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeClickOn(element, null);
        tested.afterClickOn(element, null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "ClickOn");
        assertSingleParameter(command, "element", element.toString());
    }

    @Test
    public void clickOn_nullElement() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeClickOn(null, null);
        tested.afterClickOn(null, null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "ClickOn");
        assertNoParameters(command);
    }

    // ---------------------------------------------------------- changeValueOf

    @Test
    public void changeValueOf_nullElement_nullKeys() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeChangeValueOf(null, null, null);
        tested.afterChangeValueOf(null, null, null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "ChangeValueOf");
        assertNoParameters(command);
    }

    @Test
    public void changeValueOf_withElement_withKeys() {
        RemoteWebElement element = newRemoteWebElement("elementId");
        CharSequence[] keys = new CharSequence[]{"x", "y"};
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeChangeValueOf(element, null, keys);
        tested.afterChangeValueOf(element, null, keys);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "ChangeValueOf");
        assertEquals(command.getParameters(), Arrays.asList(
                new CommandParameter("element", element.toString()),
                new CommandParameter("keys", "x, y")));
    }

    @Test
    public void changeValueOf_withElement_emptyKeys() {
        RemoteWebElement element = newRemoteWebElement("elementId");
        CharSequence[] keys = new CharSequence[0];
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeChangeValueOf(element, null, keys);
        tested.afterChangeValueOf(element, null, keys);

        mocksControl.verify();
        Command command = capture.getValue();
        assertSingleParameter(command, "element", element.toString());
    }

    // --------------------------------------------------------------- script

    @Test
    public void script() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeScript("alert('hi')", null);
        tested.afterScript("alert('hi')", null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "Script");
        assertSingleParameter(command, "script", "alert('hi')");
    }

    // ----------------------------------------------------------- switchWindow

    @Test
    public void switchToWindow() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeSwitchToWindow("myWindow", null);
        tested.afterSwitchToWindow("myWindow", null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "SwitchToWindow");
        assertSingleParameter(command, "windowName", "myWindow");
    }

    // -------------------------------------------------------------- sendKeys

    @Test
    public void sendKeys_withKeys() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeSendKeys("a", "b");
        tested.afterSendKeys("a", "b");

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "SendKeys");
        assertSingleParameter(command, "keys", "a, b");
    }

    @Test
    public void sendKeys_nullArray() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeSendKeys((CharSequence[]) null);
        tested.afterSendKeys((CharSequence[]) null);

        mocksControl.verify();
        assertNoParameters(capture.getValue());
    }

    @Test
    public void sendKeys_emptyArray() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeSendKeys();
        tested.afterSendKeys();

        mocksControl.verify();
        assertNoParameters(capture.getValue());
    }

    // -------------------------------------------------------------- pressKey

    @Test
    public void pressKey_withKey() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforePressKey("a");
        tested.afterPressKey("a");

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "PressKey");
        assertSingleParameter(command, "key", "a");
    }

    @Test
    public void pressKey_nullKey() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforePressKey(null);
        tested.afterPressKey(null);

        mocksControl.verify();
        assertNoParameters(capture.getValue());
    }

    // ------------------------------------------------------------ releaseKey

    @Test
    public void releaseKey_withKey() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeReleaseKey("a");
        tested.afterReleaseKey("a");

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "ReleaseKey");
        assertSingleParameter(command, "key", "a");
    }

    @Test
    public void releaseKey_nullKey() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeReleaseKey(null);
        tested.afterReleaseKey(null);

        mocksControl.verify();
        assertNoParameters(capture.getValue());
    }

    // ---------------------------------------------------------------- getText

    @Test
    public void getText_before() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeGetText(null, null);
        tested.afterGetText(null, null, null);

        mocksControl.verify();
        assertEquals(capture.getValue().getName(), "GetText");
    }

    @Test
    public void getText_after_withText() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeGetText(null, null);
        tested.afterGetText(null, null, "hello");

        mocksControl.verify();
        assertSingleParameter(capture.getValue(), "text", "hello");
    }

    @Test
    public void getText_after_nullText() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeGetText(null, null);
        tested.afterGetText(null, null, null);

        mocksControl.verify();
        assertNoParameters(capture.getValue());
    }

    // ------------------------------------------------------------------ click

    @Test
    public void click_nullCoordinates() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeClick(null);
        tested.afterClick(null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "Click");
        assertNoParameters(command);
    }

    @Test
    public void click_withRemoteWebElementCoordinates() {
        RemoteWebElement element = newRemoteWebElement("elementId");
        Coordinates coordinates = element.getCoordinates();
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeClick(coordinates);
        tested.afterClick(coordinates);

        mocksControl.verify();
        assertSingleParameter(capture.getValue(), "coordinates", element.toString());
    }

    @Test
    public void click_withNonRemoteWebElementCoordinates() {
        Coordinates coordinates = new NonRemoteElementCoordinates();
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeClick(coordinates);
        tested.afterClick(coordinates);

        mocksControl.verify();
        assertNoParameters(capture.getValue());
    }

    @Test
    public void click_withCoordinatesReflectionFailure() {
        Coordinates coordinates = mocksControl.createMock(Coordinates.class);
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeClick(coordinates);
        tested.afterClick(coordinates);

        mocksControl.verify();
        // Reflection failed to find "this$0" on the mock; parameter extraction is swallowed.
        assertNoParameters(capture.getValue());
    }

    // ------------------------------------------------------------ doubleClick

    @Test
    public void doubleClick() {
        RemoteWebElement element = newRemoteWebElement("elementId");
        Coordinates coordinates = element.getCoordinates();
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeDoubleClick(coordinates);
        tested.afterDoubleClick(coordinates);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "DoubleClick");
        assertSingleParameter(command, "coordinates", element.toString());
    }

    // -------------------------------------------------------------- mouseDown

    @Test
    public void mouseDown() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeMouseDown();
        tested.afterMouseDown();

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "MouseDown");
        assertNoParameters(command);
    }

    // ---------------------------------------------------------------- mouseUp

    @Test
    public void mouseUp() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeMouseUp(null);
        tested.afterMouseUp(null);

        mocksControl.verify();
        assertEquals(capture.getValue().getName(), "MouseUp");
    }

    // -------------------------------------------------------------- mouseMove

    @Test
    public void mouseMove_withoutOffsets() {
        RemoteWebElement element = newRemoteWebElement("elementId");
        Coordinates coordinates = element.getCoordinates();
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeMouseMove(coordinates);
        tested.afterMouseMove(coordinates);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "MouseMove");
        assertSingleParameter(command, "coordinates", element.toString());
    }

    @Test
    public void mouseMove_withOffsets() {
        RemoteWebElement element = newRemoteWebElement("elementId");
        Coordinates coordinates = element.getCoordinates();
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeMouseMove(coordinates, 5L, 10L);
        tested.afterMouseMove(coordinates, 5L, 10L);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "MouseMove");
        assertEquals(command.getParameters(), Arrays.asList(
                new CommandParameter("coordinates", element.toString()),
                new CommandParameter("xOffset", "5"),
                new CommandParameter("yOffset", "10")));
    }

    // ----------------------------------------------------------- contextClick

    @Test
    public void contextClick() {
        RemoteWebElement element = newRemoteWebElement("elementId");
        Coordinates coordinates = element.getCoordinates();
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeContextClick(coordinates);
        tested.afterContextClick(coordinates);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getName(), "ContextClick");
        assertSingleParameter(command, "coordinates", element.toString());
    }

    // -------------------------------------------------------------- screenshot

    @Test
    public void getScreenshotAs_before() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeGetScreenshotAs(OutputType.BYTES);
        tested.afterGetScreenshotAs(OutputType.BYTES, new byte[]{1, 2, 3});

        mocksControl.verify();
        assertEquals(capture.getValue().getName(), "Screenshot");
    }

    @Test
    public void getScreenshotAs_withFile() throws Exception {
        Path tempFile = Files.createTempFile("screenshot_", ".png");
        tempFile.toFile().deleteOnExit();
        try {
            Capture<Command> capture = expectCommand();
            mocksControl.replay();

            tested.beforeGetScreenshotAs(OutputType.FILE);
            tested.afterGetScreenshotAs(OutputType.FILE, tempFile.toFile());

            mocksControl.verify();
            Command command = capture.getValue();
            assertEquals(command.getScreenshots().size(), 1);
            assertEquals(command.getScreenshots().get(0).getAbsolutePath(), tempFile.toFile().getAbsolutePath());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void getScreenshotAs_withByteArray() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeGetScreenshotAs(OutputType.BYTES);
        tested.afterGetScreenshotAs(OutputType.BYTES, "not-a-real-png".getBytes(StandardCharsets.UTF_8));

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getScreenshots().size(), 1);
        assertEquals(command.getScreenshots().get(0).getExtension(), "png");
    }

    @Test
    public void getScreenshotAs_withBase64String() {
        String base64 = Base64.encodeBase64String("not-a-real-png".getBytes(StandardCharsets.UTF_8));
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeGetScreenshotAs(OutputType.BASE64);
        tested.afterGetScreenshotAs(OutputType.BASE64, base64);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getScreenshots().size(), 1);
        assertEquals(command.getScreenshots().get(0).getExtension(), "png");
    }

    @Test
    public void getScreenshotAs_withUnsupportedType() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeGetScreenshotAs(OutputType.BYTES);
        invokeAfterGetScreenshotAsRaw(OutputType.BYTES, null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertTrue(command.getScreenshots().isEmpty());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void invokeAfterGetScreenshotAsRaw(OutputType target, Object screenshot) {
        tested.afterGetScreenshotAs(target, screenshot);
    }

    @Test
    public void getScreenshotAs_afterWithoutBefore_doesNotSendCommand() {
        mocksControl.replay();

        tested.afterGetScreenshotAs(OutputType.BYTES, new byte[]{1});

        mocksControl.verify();
    }

    // ------------------------------------------------------------- onException

    @Test
    public void onException_withPendingCommand() {
        Capture<Command> capture = expectCommand();
        mocksControl.replay();

        tested.beforeClickOn(null, null);
        tested.onException(new RuntimeException("boom"), null);

        mocksControl.verify();
        Command command = capture.getValue();
        assertEquals(command.getStatus(), CommandStatus.FAILURE);
        assertEquals(command.getMessage(), "boom");
    }

    @Test
    public void onException_withoutPendingCommand() {
        mocksControl.replay();

        tested.onException(new RuntimeException("boom"), null);

        mocksControl.verify();
    }

    // ------------------------------------------------------------------ onQuit

    @Test
    public void onQuit() {
        reportiumImportClientMock.quit();
        mocksControl.replay();

        tested.onQuit();

        mocksControl.verify();
    }

    // --------------------------------------------------------- generic after()

    @Test
    public void afterWithoutBefore_doesNotSendCommand() {
        mocksControl.replay();

        tested.afterAlertAccept(null);

        mocksControl.verify();
    }
}
