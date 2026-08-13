package com.perfecto.reportium.client;

import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link LoggerReportiumClient}
 */
public class LoggerReportiumClientTest {

    private static final class CapturingHandler extends Handler {
        private LogRecord lastRecord;

        @Override
        public void publish(LogRecord record) {
            lastRecord = record;
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        String getLastMessage() {
            return lastRecord == null ? null : lastRecord.getMessage();
        }
    }

    private final LoggerReportiumClient client = new LoggerReportiumClient();
    private final Logger logger = Logger.getLogger("ReportiumLogger");
    private CapturingHandler handler;

    @BeforeMethod
    public void setup() {
        handler = new CapturingHandler();
        logger.setLevel(Level.ALL);
        logger.addHandler(handler);
    }

    @AfterMethod
    public void teardown() {
        logger.removeHandler(handler);
    }

    @Test
    public void testTestStart() {
        client.testStart("myTest", new TestContext("tag1"));
        assertTrue(handler.getLastMessage().contains("Starting test - myTest"));
        assertTrue(handler.getLastMessage().contains("tag1"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testTestStep() {
        client.testStep("step description");
        assertEquals(handler.getLastMessage(), "Executing step - step description");
    }

    @Test
    public void testStepStart() {
        client.stepStart("step description");
        assertEquals(handler.getLastMessage(), "Starting step - step description");
    }

    @Test
    public void testStepEnd() {
        client.stepEnd();
        assertEquals(handler.getLastMessage(), "Ending step");
    }

    @Test
    public void testStepEndWithMessage() {
        client.stepEnd("done");
        assertEquals(handler.getLastMessage(), "Ending step - done");
    }

    @Test
    public void testReportiumAssert() {
        client.reportiumAssert("assertion message", true);
        assertTrue(handler.getLastMessage().contains("assertion message"));
        assertTrue(handler.getLastMessage().contains("true"));
    }

    @Test
    public void testTestStop() {
        client.testStop(TestResultFactory.createSuccess());
        assertTrue(handler.getLastMessage().startsWith("Test result:"));
    }

    @Test
    public void testTestStopWithContext() {
        client.testStop(TestResultFactory.createSuccess(), new TestContext("tag1"));
        assertTrue(handler.getLastMessage().startsWith("Test result:"));
        assertTrue(handler.getLastMessage().contains("with test context"));
    }

    @Test
    public void testGetReportUrl() {
        assertEquals(client.getReportUrl(), "N/A - local logger");
    }
}
