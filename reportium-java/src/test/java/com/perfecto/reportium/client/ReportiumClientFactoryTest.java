package com.perfecto.reportium.client;

import com.perfecto.reportium.model.PerfectoExecutionContext;
import org.easymock.EasyMock;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link ReportiumClientFactory}
 */
public class ReportiumClientFactoryTest {

    private final ReportiumClientFactory factory = new ReportiumClientFactory();

    @Test
    public void testCreatePerfectoReportiumClient() {
        WebDriver webDriverMock = EasyMock.createNiceMock(WebDriver.class);
        PerfectoExecutionContext context = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();

        ReportiumClient client = factory.createPerfectoReportiumClient(context);
        assertNotNull(client);
        assertTrue(client instanceof PerfectoReportiumClient);
    }

    @Test
    public void testCreateLoggerClient() {
        ReportiumClient client = factory.createLoggerClient();
        assertNotNull(client);
        assertTrue(client instanceof LoggerReportiumClient);
    }
}
