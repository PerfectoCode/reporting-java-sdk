package com.perfecto.reportium.client;

import com.perfecto.reportium.exception.ReportiumException;
import com.perfecto.reportium.model.CustomField;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.easymock.Capture;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link PerfectoReportiumClient}
 */
public class PerfectoReportiumClientTest {
    public static final String EXPECTED_REPORT_URL = "https://tenant.reporting.perfectomobile.com/library?externalId%5B0%5D=32f76d03";
    public static final String REPORT_URL = "https://tenant.reporting.perfectomobile.com/library?externalId[0]=32f76d03";

    /**
     * Marker interface for EasyMock to simulate a RemoteWebDriver.
     */
    private interface DriverWithCapabilities extends WebDriver, HasCapabilities, JavascriptExecutor {
    }

    /**
     * Marker interface for a WebDriver instance that does NOT expose Selenium Capabilities.
     */
    private interface PlainWebDriver extends WebDriver {
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
        expect(capabilitiesMock.getCapability(eq(Constants.Capabilities.executionReportUrl))).andReturn(REPORT_URL);

        replay(webDriverMock, capabilitiesMock);

        client.testStart("abc", new TestContext());
        client.testStep("step1");
        client.testStep("step2");
        client.testStop(TestResultFactory.createFailure("Just because", new Throwable("Yikes")));
        assertEquals(client.getReportUrl(), EXPECTED_REPORT_URL);

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

    @Test
    public void testGetReportUrl_WithQueryParameters() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);
        Capabilities capabilitiesMock = createMock(Capabilities.class);

        expect(webDriverMock.getCapabilities()).andReturn(capabilitiesMock);
        expect(capabilitiesMock.getCapability(eq(Constants.Capabilities.executionReportUrl))).andReturn(REPORT_URL);

        replay(webDriverMock, capabilitiesMock);

        String actualUrl = client.getReportUrl();
        assertEquals(EXPECTED_REPORT_URL, actualUrl);

        verify(webDriverMock, capabilitiesMock);
    }

    @Test
    public void testGetReportUrl_NotHasCapabilities() {
        PlainWebDriver webDriverMock = createMock(PlainWebDriver.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        replay(webDriverMock);

        try {
            client.getReportUrl();
            org.testng.Assert.fail("Expected a ReportiumException");
        } catch (ReportiumException e) {
            assertEquals(e.getMessage(), "WebDriver instance is assumed to have Selenium Capabilities");
        }

        verify(webDriverMock);
    }

    @Test
    public void testGetReportUrl_NullCapabilityValue() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);
        Capabilities capabilitiesMock = createMock(Capabilities.class);

        expect(webDriverMock.getCapabilities()).andReturn(capabilitiesMock);
        expect(capabilitiesMock.getCapability(eq(Constants.Capabilities.executionReportUrl))).andReturn(null);

        replay(webDriverMock, capabilitiesMock);

        assertEquals(client.getReportUrl(), null);

        verify(webDriverMock, capabilitiesMock);
    }

    @Test
    public void testGetReportUrl_InvalidUriFallsBackToRawValue() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);
        Capabilities capabilitiesMock = createMock(Capabilities.class);

        String invalidUri = "https://tenant.reporting.perfectomobile.com/library?param=a b|c";
        expect(webDriverMock.getCapabilities()).andReturn(capabilitiesMock);
        expect(capabilitiesMock.getCapability(eq(Constants.Capabilities.executionReportUrl))).andReturn(invalidUri);

        replay(webDriverMock, capabilitiesMock);

        assertEquals(client.getReportUrl(), invalidUri);

        verify(webDriverMock, capabilitiesMock);
    }

    @Test
    public void testTestStart_withJobAndProject() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        Capture<Map<String, Object>> paramsCapture = newCapture();
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .withJob(new Job("myJob", 42).withBranch("myBranch"))
                .withProject(new Project("myProject", "1.0"))
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        expect(webDriverMock.executeScript(eq("mobile:test:start"), capture(paramsCapture))).andReturn(1000);
        replay(webDriverMock);

        client.testStart("abc", new TestContext());

        Map<String, Object> params = paramsCapture.getValue();
        assertEquals(params.get("jobName"), "myJob");
        assertEquals(params.get("jobNumber"), 42);
        assertEquals(params.get("jobBranch"), "myBranch");
        assertEquals(params.get("projectName"), "myProject");
        assertEquals(params.get("projectVersion"), "1.0");

        verify(webDriverMock);
    }

    @Test
    public void testTestStart_withCustomFieldsFromBothContexts() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        Capture<Map<String, Object>> paramsCapture = newCapture();
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .withCustomFields(new CustomField("shared", "fromExecutionContext"))
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        expect(webDriverMock.executeScript(eq("mobile:test:start"), capture(paramsCapture))).andReturn(1000);
        replay(webDriverMock);

        TestContext testContext = new TestContext.Builder()
                .withCustomFields(new CustomField("shared", "fromTestContext"), new CustomField("onlyTest", "v"))
                .build();
        client.testStart("abc", testContext);

        @SuppressWarnings("unchecked")
        java.util.List<String> customFieldsPairs = (java.util.List<String>) paramsCapture.getValue().get("customFields");
        // The context custom field "shared" wins over the execution-context one because it's added first
        assertTrue(customFieldsPairs.contains("shared=fromTestContext"));
        assertTrue(customFieldsPairs.contains("onlyTest=v"));
        // The duplicate "shared" name from the execution context must not be added twice
        assertEquals(customFieldsPairs.size(), 2);

        verify(webDriverMock);
    }

    @Test(expectedExceptions = ReportiumException.class,
            expectedExceptionsMessageRegExp = "Custom field name cannot be empty")
    public void testTestStart_blankCustomFieldNameThrows() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);
        replay(webDriverMock);

        TestContext testContext = new TestContext.Builder()
                .withCustomFields(new CustomField("", "v"))
                .build();
        client.testStart("abc", testContext);
    }

    @Test
    public void testStepStart() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        Capture<Map<String, Object>> paramsCapture = newCapture();
        expect(webDriverMock.executeScript(eq("mobile:step:start"), capture(paramsCapture))).andReturn(1);
        replay(webDriverMock);

        client.stepStart("my step");

        assertEquals(paramsCapture.getValue().get("name"), "my step");
        verify(webDriverMock);
    }

    @Test
    public void testStepEnd_noMessage() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        Capture<Map<String, Object>> paramsCapture = newCapture();
        expect(webDriverMock.executeScript(eq("mobile:step:end"), capture(paramsCapture))).andReturn(1);
        replay(webDriverMock);

        client.stepEnd();

        assertEquals(paramsCapture.getValue().get("message"), null);
        verify(webDriverMock);
    }

    @Test
    public void testStepEnd_withMessage() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        Capture<Map<String, Object>> paramsCapture = newCapture();
        expect(webDriverMock.executeScript(eq("mobile:step:end"), capture(paramsCapture))).andReturn(1);
        replay(webDriverMock);

        client.stepEnd("done");

        assertEquals(paramsCapture.getValue().get("message"), "done");
        verify(webDriverMock);
    }

    @Test
    public void testReportiumAssert() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        Capture<Map<String, Object>> paramsCapture = newCapture();
        expect(webDriverMock.executeScript(eq("mobile:status:assert"), capture(paramsCapture))).andReturn(1);
        replay(webDriverMock);

        client.reportiumAssert("my message", true);

        assertEquals(paramsCapture.getValue().get("message"), "my message");
        assertEquals(paramsCapture.getValue().get("status"), true);
        verify(webDriverMock);
    }

    @Test
    public void testTestStop_withTagsAndCustomFieldsInTestContext() {
        DriverWithCapabilities webDriverMock = createMock(DriverWithCapabilities.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();
        PerfectoReportiumClient client = new PerfectoReportiumClient(context);

        Capture<Map<String, Object>> paramsCapture = newCapture();
        expect(webDriverMock.executeScript(eq("mobile:test:end"), capture(paramsCapture))).andReturn(1);
        replay(webDriverMock);

        TestContext testContext = new TestContext.Builder()
                .withTestExecutionTags("tag1")
                .withCustomFields(new CustomField("name1", "value1"))
                .build();
        client.testStop(TestResultFactory.createSuccess(), testContext);

        Map<String, Object> params = paramsCapture.getValue();
        assertEquals(params.get("success"), true);
        assertTrue(((java.util.List<?>) params.get("tags")).contains("tag1"));
        assertTrue(((java.util.List<?>) params.get("customFields")).contains("name1=value1"));

        verify(webDriverMock);
    }

    @Test
    public void testConstructor_reportsMultipleExecutions() {
        DriverWithCapabilities webDriverMock1 = createMock(DriverWithCapabilities.class);
        DriverWithCapabilities webDriverMock2 = createMock(DriverWithCapabilities.class);
        Capabilities capabilities1 = createMock(Capabilities.class);
        Capabilities capabilities2 = createMock(Capabilities.class);

        expect(webDriverMock1.getCapabilities()).andReturn(capabilities1);
        expect(capabilities1.getCapability(eq(Constants.Capabilities.executionId))).andReturn("ext-1");
        expect(webDriverMock2.getCapabilities()).andReturn(capabilities2);
        expect(capabilities2.getCapability(eq(Constants.Capabilities.executionId))).andReturn("ext-2");

        Capture<Map<String, Object>> paramsCapture = newCapture();
        expect(webDriverMock1.executeScript(eq("mobile:execution:multiple"), capture(paramsCapture))).andReturn(1);

        replay(webDriverMock1, webDriverMock2, capabilities1, capabilities2);

        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock1, "  alias!! 1  ")
                .withWebDriver(webDriverMock2, "alias 2")
                .build();
        new PerfectoReportiumClient(context);

        String json = (String) paramsCapture.getValue().get("externalIdAliases");
        assertTrue(json.contains("\"externalId\":\"ext-1\""));
        assertTrue(json.contains("\"alias\":\"alias 1\""));
        assertTrue(json.contains("\"externalId\":\"ext-2\""));
        assertTrue(json.contains("\"alias\":\"alias 2\""));

        verify(webDriverMock1, webDriverMock2, capabilities1, capabilities2);
    }
}
