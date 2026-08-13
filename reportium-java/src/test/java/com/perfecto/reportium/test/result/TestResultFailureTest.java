package com.perfecto.reportium.test.result;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.testng.annotations.Test;

import static com.perfecto.reportium.test.result.TestResultFailure.MESSAGE_MAX_LENGTH;
import static com.perfecto.reportium.test.result.TestResultFailure.TRIMMED_TEXT_SUFFIX;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * TestContext unit test
 */
public class TestResultFailureTest {

    @Test
    public void testLongMessage() {
        TestResultFailure failure1 = new TestResultFailure("reason", new RuntimeException(StringUtils.repeat("a", MESSAGE_MAX_LENGTH)), null);
        assertEquals(MESSAGE_MAX_LENGTH, failure1.getMessage().length());

        TestResultFailure failure2 = new TestResultFailure("reason", new RuntimeException(StringUtils.repeat("a", MESSAGE_MAX_LENGTH + 1)), null);
        assertEquals(MESSAGE_MAX_LENGTH, failure2.getMessage().length());
        assertTrue(failure2.getMessage().endsWith(TRIMMED_TEXT_SUFFIX));
    }

    @Test
    public void testReasonSameAsExceptionMessage() {
        RuntimeException throwable = new RuntimeException("reason");
        TestResultFailure failure = new TestResultFailure(throwable.getMessage(), throwable, null);
        assertEquals(ExceptionUtils.getStackTrace(throwable), failure.getMessage());
    }

    @Test
    public void testNoThrowableAndBlankReason() {
        TestResultFailure failure = new TestResultFailure(null, null, null);
        assertNull(failure.getMessage());
    }

    @Test
    public void testNoThrowableAndBlankReason_emptyString() {
        TestResultFailure failure = new TestResultFailure("   ", null, null);
        assertNull(failure.getMessage());
    }

    @Test
    public void testNoThrowableWithReasonOnly() {
        TestResultFailure failure = new TestResultFailure("plain reason", null, null);
        assertEquals(failure.getMessage(), "plain reason");
    }

    @Test
    public void testGetFailureReasonName() {
        TestResultFailure failure = new TestResultFailure("reason", null, "myFailureReason");
        assertEquals(failure.getFailureReasonName(), "myFailureReason");
    }

    @Test
    public void testToString() {
        TestResultFailure failure = new TestResultFailure("plain reason", null, "myFailureReason");
        String toString = failure.toString();
        assertTrue(toString.contains("plain reason"));
        assertTrue(toString.contains("myFailureReason"));
    }

    @Test
    public void testVisitDispatchesToVisitor() {
        TestResultFailure failure = new TestResultFailure("plain reason", null, null);
        TestResultVisitor visitorMock = createMock(TestResultVisitor.class);
        visitorMock.visit(failure);
        replay(visitorMock);

        failure.visit(visitorMock);

        verify(visitorMock);
    }
}
