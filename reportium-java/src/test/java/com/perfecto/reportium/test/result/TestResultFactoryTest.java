package com.perfecto.reportium.test.result;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link TestResultFactory}
 */
public class TestResultFactoryTest {

    @Test
    public void testConstructorIsAccessible() {
        // Exercises the implicit default constructor
        assertNotNull(new TestResultFactory());
    }

    @Test
    public void testCreateSuccess() {
        TestResult result = TestResultFactory.createSuccess();
        assertTrue(result instanceof TestResultSuccess);
    }

    @Test
    public void testCreateFailureWithMessageOnly() {
        TestResult result = TestResultFactory.createFailure("boom");
        assertTrue(result instanceof TestResultFailure);
        TestResultFailure failure = (TestResultFailure) result;
        assertEquals(failure.getMessage(), "boom");
        assertNull(failure.getFailureReasonName());
    }

    @Test
    public void testCreateFailureWithThrowableOnly() {
        Throwable throwable = new RuntimeException("boom");
        TestResult result = TestResultFactory.createFailure(throwable);
        assertTrue(result instanceof TestResultFailure);
        TestResultFailure failure = (TestResultFailure) result;
        assertTrue(failure.getMessage().contains("RuntimeException"));
        assertNull(failure.getFailureReasonName());
    }

    @Test
    public void testCreateFailureWithThrowableAndReason() {
        Throwable throwable = new RuntimeException("boom");
        TestResult result = TestResultFactory.createFailure(throwable, "myReason");
        assertTrue(result instanceof TestResultFailure);
        TestResultFailure failure = (TestResultFailure) result;
        assertTrue(failure.getMessage().contains("RuntimeException"));
        assertEquals(failure.getFailureReasonName(), "myReason");
    }

    @Test
    public void testCreateFailureWithMessageAndThrowable() {
        Throwable throwable = new RuntimeException("boom");
        TestResult result = TestResultFactory.createFailure("custom message", throwable);
        assertTrue(result instanceof TestResultFailure);
        TestResultFailure failure = (TestResultFailure) result;
        assertTrue(failure.getMessage().startsWith("custom message"));
        assertNull(failure.getFailureReasonName());
    }

    @Test
    public void testCreateFailureFull() {
        Throwable throwable = new RuntimeException("boom");
        TestResult result = TestResultFactory.createFailure("custom message", throwable, "myReason");
        assertTrue(result instanceof TestResultFailure);
        TestResultFailure failure = (TestResultFailure) result;
        assertTrue(failure.getMessage().startsWith("custom message"));
        assertEquals(failure.getFailureReasonName(), "myReason");
    }
}
