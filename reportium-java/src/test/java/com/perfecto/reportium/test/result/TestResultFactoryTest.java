package com.perfecto.reportium.test.result;

import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.*;

/**
 * Created by yurig on 04-May-17.
 */
public class TestResultFactoryTest {
    @Test
    public void testCreateSuccess() {
        TestResult testResult = TestResultFactory.createSuccess();
        assertNotNull(testResult);
        assertTrue(testResult instanceof TestResultSuccess);
    }

    @Test
    public void testCreateFailure_withoutStackTrace() {
        String message = "message";
        TestResult testResult = TestResultFactory.createFailure(message);
        assertNotNull(testResult);
        assertTrue(testResult instanceof TestResultFailure);
        TestResultFailure testResultFailure = (TestResultFailure) testResult;
        assertEquals(message, testResultFailure.getMessage());
        assertNull(testResultFailure.getFailureReasonName());
    }

    @Test
    public void testCreateFailure_withStackTrace() {
        String message = "message";
        String exceptionMessage = "Exception message";
        String exceptionType = RuntimeException.class.getSimpleName();
        TestResult testResult = TestResultFactory.createFailure(message, new RuntimeException(exceptionMessage));
        assertNotNull(testResult);
        assertTrue(testResult instanceof TestResultFailure);
        TestResultFailure testResultFailure = (TestResultFailure) testResult;
        assertThat(testResultFailure.getMessage(), CoreMatchers.containsString(message));
        assertThat(testResultFailure.getMessage(), CoreMatchers.containsString(exceptionMessage));
        assertThat(testResultFailure.getMessage(), CoreMatchers.containsString(exceptionType));
        assertNull(testResultFailure.getFailureReasonName());
    }

    @Test
    public void testCreateFailure_withStackTraceAndFailureReason() {
        String message = "message";
        String exceptionMessage = "Exception message";
        String failureReason = "failureReason";
        TestResult testResult = TestResultFactory.createFailure(message, new RuntimeException(exceptionMessage), failureReason);
        assertNotNull(testResult);
        assertTrue(testResult instanceof TestResultFailure);
        TestResultFailure testResultFailure = (TestResultFailure) testResult;
        assertThat(testResultFailure.getMessage(), CoreMatchers.containsString(exceptionMessage));
        assertEquals(failureReason, testResultFailure.getFailureReasonName());
    }
}
