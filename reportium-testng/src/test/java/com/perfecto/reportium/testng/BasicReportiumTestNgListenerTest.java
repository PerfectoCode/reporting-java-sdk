package com.perfecto.reportium.testng;

import com.perfecto.reportium.WebDriverProvider;
import com.perfecto.reportium.client.ReportiumClient;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.IClass;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * Unit tests for {@link BasicReportiumTestNgListener}.
 */
public class BasicReportiumTestNgListenerTest {

    /**
     * Marker interface for EasyMock to simulate a real Selenium remote driver, which is both
     * a {@link JavascriptExecutor} (needed by the underlying ReportiumClient) and provides
     * {@link org.openqa.selenium.Capabilities} (needed for the multiple-execution/report-url checks).
     */
    private interface DriverWithCapabilities extends WebDriver, HasCapabilities, JavascriptExecutor {
    }

    private static class DummyTestClass {
    }

    private static WebDriverProvider webDriverProviderReturning(final WebDriver driver) {
        return new WebDriverProvider() {
            @Override
            public WebDriver getWebDriver() {
                return driver;
            }
        };
    }

    @Test
    public void testGetWebDriver_instanceIsWebDriverProvider_returnsDriver() {
        WebDriver driverMock = createMock(WebDriver.class);
        replay(driverMock);

        WebDriverProvider provider = webDriverProviderReturning(driverMock);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getInstance()).andReturn(provider);
        replay(testResult);

        BasicReportiumTestNgListener listener = new BasicReportiumTestNgListener();
        assertSame(listener.getWebDriver(testResult), driverMock);

        verify(testResult);
    }

    @Test
    public void testGetWebDriver_instanceIsNotWebDriverProvider_throws() {
        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getInstance()).andReturn(new Object());
        replay(testResult);

        BasicReportiumTestNgListener listener = new BasicReportiumTestNgListener();
        try {
            listener.getWebDriver(testResult);
            fail("Expected a RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Unable to get WebDriver instance");
        }

        verify(testResult);
    }

    @Test
    public void testCreateReportiumClient_buildsClientFromWebDriver() {
        DriverWithCapabilities driverMock = createMock(DriverWithCapabilities.class);
        replay(driverMock);

        WebDriverProvider provider = webDriverProviderReturning(driverMock);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getInstance()).andReturn(provider);
        replay(testResult);

        BasicReportiumTestNgListener listener = new BasicReportiumTestNgListener();
        ReportiumClient client = listener.createReportiumClient(testResult);

        assertNotNull(client);

        verify(testResult, driverMock);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testReportTestStart_endToEnd_startsRealClient() {
        DriverWithCapabilities driverMock = createMock(DriverWithCapabilities.class);
        expect(driverMock.executeScript(eq("mobile:test:start"), isA(Map.class))).andReturn(1000);
        replay(driverMock);

        WebDriverProvider provider = webDriverProviderReturning(driverMock);

        IClass testClass = createMock(IClass.class);
        expect((Class) testClass.getRealClass()).andReturn(DummyTestClass.class).anyTimes();
        replay(testClass);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getInstance()).andReturn(provider);
        expect(testResult.getTestClass()).andReturn(testClass).anyTimes();
        expect(testResult.getName()).andReturn("myTest").anyTimes();
        replay(testResult);

        BasicReportiumTestNgListener listener = new BasicReportiumTestNgListener();
        // Exercises BasicReportiumTestNgListener.reportTestStart(), which in turn calls
        // createReportiumClient(), sets it on the provider, and delegates to the base class,
        // which calls the real (mocked-webdriver-backed) ReportiumClient.testStart().
        listener.reportTestStart(testResult);

        verify(testResult, testClass, driverMock);
    }
}
