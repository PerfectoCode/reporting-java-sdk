package com.perfecto.reportium.test.result;

import org.testng.annotations.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link TestResultSuccess}
 */
public class TestResultSuccessTest {

    @Test
    public void testVisitDispatchesToVisitor() {
        TestResultSuccess success = new TestResultSuccess();
        TestResultVisitor visitorMock = createMock(TestResultVisitor.class);
        visitorMock.visit(success);
        replay(visitorMock);

        success.visit(visitorMock);

        verify(visitorMock);
    }

    @Test
    public void testToString() {
        TestResultSuccess success = new TestResultSuccess();
        assertTrue(success.toString().contains("TestResultSuccess"));
    }
}
