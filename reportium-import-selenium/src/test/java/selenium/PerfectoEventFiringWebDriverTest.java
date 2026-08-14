package selenium;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.model.ImportExecutionContext;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link PerfectoEventFiringWebDriver}.
 */
public class PerfectoEventFiringWebDriverTest {

    private IMocksControl mocksControl;

    @BeforeMethod
    public void beforeMethod() {
        mocksControl = EasyMock.createControl();
    }

    @Test
    public void constructor_withPlainWebDriver_wrapsDriver() {
        WebDriver driverMock = mocksControl.createMock(WebDriver.class);
        mocksControl.replay();

        PerfectoEventFiringWebDriver tested = new PerfectoEventFiringWebDriver(driverMock);

        mocksControl.verify();
        assertSame(tested.getWrappedDriver(), driverMock);
    }

    @Test
    public void constructor_withNonRemoteWebDriver_skipsPlatformUpdateButRegistersListener() {
        WebDriver driverMock = mocksControl.createMock(WebDriver.class);
        ReportiumImportClient reportiumImportClientMock = mocksControl.createMock(ReportiumImportClient.class);
        // getExecutionContext() must never be called since driver is not a RemoteWebDriver
        mocksControl.replay();

        PerfectoEventFiringWebDriver tested = new PerfectoEventFiringWebDriver(driverMock, reportiumImportClientMock);

        mocksControl.verify();

        Object listener = ReflectionTestUtils.getField(tested, "perfectoWebDriverListener");
        assertTrue(listener instanceof PerfectoWebDriverEventListenerImpl);

        @SuppressWarnings("unchecked")
        List<Object> eventListeners = (List<Object>) ReflectionTestUtils.getField(tested, "eventListeners");
        assertTrue(eventListeners.contains(listener));
    }

    @Test
    public void constructor_withRemoteWebDriver_updatesPlatformsAndRegistersListener() {
        RemoteWebDriver remoteDriverMock = mocksControl.createMock(RemoteWebDriver.class);
        Capabilities capabilitiesMock = mocksControl.createMock(Capabilities.class);
        ReportiumImportClient reportiumImportClientMock = mocksControl.createMock(ReportiumImportClient.class);
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();

        Map<String, Object> capabilitiesMap = new HashMap<String, Object>();
        capabilitiesMap.put(ImportExecutionContext.SELENIUM_VERSION, "99");
        capabilitiesMap.put(ImportExecutionContext.SELENIUM_BROWSER_NAME, "chrome");
        capabilitiesMap.put(ImportExecutionContext.SELENIUM_PLATFORM, "LINUX");

        EasyMock.expect(remoteDriverMock.getCapabilities()).andReturn(capabilitiesMock);
        EasyMock.expect(capabilitiesMock.asMap()).andReturn(capabilitiesMap);
        EasyMock.expect(reportiumImportClientMock.getExecutionContext()).andReturn(executionContext).anyTimes();
        mocksControl.replay();

        PerfectoEventFiringWebDriver tested = new PerfectoEventFiringWebDriver(remoteDriverMock, reportiumImportClientMock);

        mocksControl.verify();
        // the platforms list was populated as a side effect of the RemoteWebDriver branch
        assertFalse(executionContext.getPlatforms().isEmpty());

        Object listener = ReflectionTestUtils.getField(tested, "perfectoWebDriverListener");
        assertTrue(listener instanceof PerfectoWebDriverEventListenerImpl);
    }

    @Test
    public void getKeyboard_wrapsUnderlyingKeyboardWithPerfectoKeyboard() {
        RemoteWebDriver remoteDriverMock = mocksControl.createMock(RemoteWebDriver.class);
        Capabilities capabilitiesMock = mocksControl.createMock(Capabilities.class);
        ReportiumImportClient reportiumImportClientMock = mocksControl.createMock(ReportiumImportClient.class);
        Keyboard keyboardMock = mocksControl.createMock(Keyboard.class);
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();

        EasyMock.expect(remoteDriverMock.getCapabilities()).andReturn(capabilitiesMock);
        EasyMock.expect(capabilitiesMock.asMap()).andReturn(new HashMap<String, Object>());
        EasyMock.expect(reportiumImportClientMock.getExecutionContext()).andReturn(executionContext).anyTimes();
        EasyMock.expect(remoteDriverMock.getKeyboard()).andReturn(keyboardMock);
        mocksControl.replay();

        PerfectoEventFiringWebDriver tested = new PerfectoEventFiringWebDriver(remoteDriverMock, reportiumImportClientMock);
        Keyboard result = tested.getKeyboard();

        mocksControl.verify();
        assertTrue(result instanceof PerfectoKeyboard);
        // super.getKeyboard() wraps the raw driver keyboard in Selenium's own EventFiringKeyboard;
        // dig one level further to confirm it ultimately delegates to our mock.
        Object eventFiringKeyboard = ReflectionTestUtils.getField(result, "keyboard");
        Object wrappedKeyboard = ReflectionTestUtils.getField(eventFiringKeyboard, "keyboard");
        assertSame(wrappedKeyboard, keyboardMock);
        Object wrappedListener = ReflectionTestUtils.getField(result, "listener");
        assertSame(wrappedListener, ReflectionTestUtils.getField(tested, "perfectoWebDriverListener"));
    }

    @Test
    public void getMouse_wrapsUnderlyingMouseWithPerfectoMouse() {
        RemoteWebDriver remoteDriverMock = mocksControl.createMock(RemoteWebDriver.class);
        Capabilities capabilitiesMock = mocksControl.createMock(Capabilities.class);
        ReportiumImportClient reportiumImportClientMock = mocksControl.createMock(ReportiumImportClient.class);
        Mouse mouseMock = mocksControl.createMock(Mouse.class);
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();

        EasyMock.expect(remoteDriverMock.getCapabilities()).andReturn(capabilitiesMock);
        EasyMock.expect(capabilitiesMock.asMap()).andReturn(new HashMap<String, Object>());
        EasyMock.expect(reportiumImportClientMock.getExecutionContext()).andReturn(executionContext).anyTimes();
        EasyMock.expect(remoteDriverMock.getMouse()).andReturn(mouseMock);
        mocksControl.replay();

        PerfectoEventFiringWebDriver tested = new PerfectoEventFiringWebDriver(remoteDriverMock, reportiumImportClientMock);
        Mouse result = tested.getMouse();

        mocksControl.verify();
        assertTrue(result instanceof PerfectoMouse);
        // super.getMouse() wraps the raw driver mouse in Selenium's own EventFiringMouse;
        // dig one level further to confirm it ultimately delegates to our mock.
        Object eventFiringMouse = ReflectionTestUtils.getField(result, "mouse");
        Object wrappedMouse = ReflectionTestUtils.getField(eventFiringMouse, "mouse");
        assertSame(wrappedMouse, mouseMock);
        Object wrappedListener = ReflectionTestUtils.getField(result, "listener");
        assertSame(wrappedListener, ReflectionTestUtils.getField(tested, "perfectoWebDriverListener"));
    }

    @Test
    public void quit_notifiesListenerThenDelegatesToWrappedDriver() {
        RemoteWebDriver remoteDriverMock = mocksControl.createMock(RemoteWebDriver.class);
        Capabilities capabilitiesMock = mocksControl.createMock(Capabilities.class);
        ReportiumImportClient reportiumImportClientMock = mocksControl.createMock(ReportiumImportClient.class);
        ImportExecutionContext executionContext = new ImportExecutionContext.Builder().build();

        EasyMock.expect(remoteDriverMock.getCapabilities()).andReturn(capabilitiesMock);
        EasyMock.expect(capabilitiesMock.asMap()).andReturn(new HashMap<String, Object>());
        EasyMock.expect(reportiumImportClientMock.getExecutionContext()).andReturn(executionContext).anyTimes();
        reportiumImportClientMock.quit();
        remoteDriverMock.quit();
        mocksControl.replay();

        PerfectoEventFiringWebDriver tested = new PerfectoEventFiringWebDriver(remoteDriverMock, reportiumImportClientMock);
        tested.quit();

        mocksControl.verify();
    }
}
