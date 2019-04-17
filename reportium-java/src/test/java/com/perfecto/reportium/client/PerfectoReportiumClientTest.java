package com.perfecto.reportium.client;

import com.perfecto.reportium.exception.ReportiumException;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;

/**
 * Test case for {@link PerfectoReportiumClient}
 */
public class PerfectoReportiumClientTest {

    /**
     * Marker interface for EasyMock to simulate a RemoteWebDriver.
     */
    private interface DriverWithCapabilities extends WebDriver, HasCapabilities, JavascriptExecutor {
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testWebDriverInteraction() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);
        Capabilities capabilitiesMock = createMock(Capabilities.class);

        expect(webDriverMock.executeScript(eq("mobile:test:start"), isA(Map.class))).andReturn(1000);
        expect(webDriverMock.executeScript(eq("mobile:test:step"), isA(Map.class))).andReturn(2000).times(2);
        expect(webDriverMock.executeScript(eq("mobile:test:end"), isA(Map.class))).andReturn(3000);
        expect(webDriverMock.getCapabilities()).andReturn(capabilitiesMock);
        expect(capabilitiesMock.getCapability(eq(Constants.Capabilities.executionReportUrl))).andReturn("link");

        replay(webDriverMock, capabilitiesMock);

        client.testStart("abc", new TestContext());
        client.testStep("step1");
        client.testStep("step2");
        client.testStop(TestResultFactory.createFailure("Just because", new Throwable("Yikes")));
        assertEquals(client.getReportUrl(), "link");

        verify(webDriverMock, capabilitiesMock);
    }

    @Test
    public void testStop_nullTestContext() {
        DriverWithCapabilities webdriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webdriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        expect(webdriverMock.executeScript(eq("mobile:test:end"), isA(Map.class))).andReturn(3000);

        replay(webdriverMock);
        client.testStop(TestResultFactory.createFailure("Just because", new Throwable("Yikes")), null);
        verify(webdriverMock);
    }

    @Test(expectedExceptions = ReportiumException.class,
            expectedExceptionsMessageRegExp = "Missing required web driver\\(s\\) argument\\. Call your builder's withWebDriver\\(\\) method")
    public void testRequiredWebDriver() {
        new PerfectoExecutionContext.PerfectoExecutionContextBuilder().build();
    }
}
