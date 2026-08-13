package com.perfecto.reportium.testng;

import org.testng.IInvokedMethodListener;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Unit test for {@link ReportiumImportTestNgListener}.
 * <p>
 * The production class is a trivial one-line marker subclass of {@link BaseReportiumTestNgListener}
 * with no overridden behavior of its own (all logic is inherited and already covered by
 * {@link BaseReportiumTestNgListenerTest}). This test simply exercises its (implicit) constructor
 * and confirms it is wired into the expected type hierarchy.
 */
public class ReportiumImportTestNgListenerTest {

    @Test
    public void testInstantiation() {
        ReportiumImportTestNgListener listener = new ReportiumImportTestNgListener();

        assertTrue(listener instanceof BaseReportiumTestNgListener);
        assertTrue(listener instanceof IInvokedMethodListener);
    }
}
