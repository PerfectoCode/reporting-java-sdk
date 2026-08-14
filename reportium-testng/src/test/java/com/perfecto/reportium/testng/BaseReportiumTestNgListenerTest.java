package com.perfecto.reportium.testng;

import com.perfecto.reportium.client.DigitalZoomClient;
import com.perfecto.reportium.client.ReportiumClientProvider;
import com.perfecto.reportium.exception.ReportiumException;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFailure;
import com.perfecto.reportium.test.result.TestResultSuccess;
import org.testng.IClass;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * Unit tests for {@link BaseReportiumTestNgListener}.
 * <p>
 * Since the class is abstract but declares no abstract methods, an anonymous subclass with the
 * default (base) behavior is used to exercise it directly.
 */
public class BaseReportiumTestNgListenerTest {

    /** Marker class used only so getRealClass().getSimpleName() has a predictable value. */
    private static class DummyTestClass {
    }

    private BaseReportiumTestNgListener newListener() {
        return new BaseReportiumTestNgListener() {
        };
    }

    @AfterMethod
    public void teardown() throws Exception {
        // ReportiumClientProvider only exposes get()/set(), so the ThreadLocal it backs must be
        // reset via reflection to avoid leaking state into other tests on this thread.
        Field field = ReportiumClientProvider.class.getDeclaredField("reportiumClient");
        field.setAccessible(true);
        ((ThreadLocal<?>) field.get(null)).remove();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ITestResult mockTestResultWithName(String simpleClassName, String methodName) {
        IClass testClass = createMock(IClass.class);
        expect((Class) testClass.getRealClass()).andReturn(DummyTestClass.class).anyTimes();
        replay(testClass);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getTestClass()).andReturn(testClass).anyTimes();
        expect(testResult.getName()).andReturn(methodName).anyTimes();
        return testResult;
    }

    @Test
    public void testBeforeInvocation_testMethod_reportsStart() {
        BaseReportiumTestNgListener listener = newListener();

        IInvokedMethod method = createMock(IInvokedMethod.class);
        expect(method.isTestMethod()).andReturn(true);
        replay(method);

        ITestResult testResult = mockTestResultWithName("DummyTestClass", "myTest");
        replay(testResult);

        DigitalZoomClient client = createMock(DigitalZoomClient.class);
        client.testStart(eq("DummyTestClass::myTest"), isA(TestContext.class));
        expectLastCall().once();
        replay(client);
        ReportiumClientProvider.set(client);

        listener.beforeInvocation(method, testResult);

        verify(method, testResult, client);
    }

    @Test
    public void testBeforeInvocation_nonTestMethod_doesNothing() {
        BaseReportiumTestNgListener listener = newListener();

        IInvokedMethod method = createMock(IInvokedMethod.class);
        expect(method.isTestMethod()).andReturn(false);
        replay(method);

        // No stubs configured: any call on this mock during replay fails the test.
        ITestResult testResult = createMock(ITestResult.class);
        replay(testResult);

        listener.beforeInvocation(method, testResult);

        verify(method, testResult);
    }

    @Test
    public void testAfterInvocation_nonTestMethod_doesNothing() {
        BaseReportiumTestNgListener listener = newListener();

        IInvokedMethod method = createMock(IInvokedMethod.class);
        expect(method.isTestMethod()).andReturn(false);
        replay(method);

        ITestResult testResult = createMock(ITestResult.class);
        replay(testResult);

        listener.afterInvocation(method, testResult);

        verify(method, testResult);
    }

    @Test
    public void testAfterInvocation_success_reportsSuccess() {
        assertAfterInvocationReportsResult(ITestResult.SUCCESS, TestResultSuccess.class);
    }

    @Test
    public void testAfterInvocation_successPercentageFailure_reportsSuccess() {
        assertAfterInvocationReportsResult(ITestResult.SUCCESS_PERCENTAGE_FAILURE, TestResultSuccess.class);
    }

    @Test
    public void testAfterInvocation_failure_reportsFailure() {
        BaseReportiumTestNgListener listener = newListener();

        IInvokedMethod method = createMock(IInvokedMethod.class);
        expect(method.isTestMethod()).andReturn(true);
        replay(method);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getStatus()).andReturn(ITestResult.FAILURE);
        expect(testResult.getThrowable()).andReturn(new RuntimeException("boom"));
        replay(testResult);

        DigitalZoomClient client = createMock(DigitalZoomClient.class);
        client.testStop(isA(TestResultFailure.class));
        expectLastCall().once();
        replay(client);
        ReportiumClientProvider.set(client);

        listener.afterInvocation(method, testResult);

        verify(method, testResult, client);
    }

    @Test
    public void testAfterInvocation_skip_doesNotCallClient() {
        BaseReportiumTestNgListener listener = newListener();

        IInvokedMethod method = createMock(IInvokedMethod.class);
        expect(method.isTestMethod()).andReturn(true);
        replay(method);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getStatus()).andReturn(ITestResult.SKIP);
        replay(testResult);

        // No expectations configured; any invocation on the client fails the test.
        DigitalZoomClient client = createMock(DigitalZoomClient.class);
        replay(client);
        ReportiumClientProvider.set(client);

        listener.afterInvocation(method, testResult);

        verify(method, testResult, client);
    }

    @Test
    public void testAfterInvocation_unexpectedStatus_throwsReportiumException() {
        BaseReportiumTestNgListener listener = newListener();

        IInvokedMethod method = createMock(IInvokedMethod.class);
        expect(method.isTestMethod()).andReturn(true);
        replay(method);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getStatus()).andReturn(ITestResult.STARTED);
        replay(testResult);

        DigitalZoomClient client = createMock(DigitalZoomClient.class);
        replay(client);
        ReportiumClientProvider.set(client);

        try {
            listener.afterInvocation(method, testResult);
            fail("Expected a ReportiumException to be thrown");
        } catch (ReportiumException e) {
            assertEquals(e.getMessage(), "Unexpected status " + ITestResult.STARTED);
        }

        verify(method, testResult, client);
    }

    @Test
    public void testAfterInvocation_nullClient_doesNothing() throws Exception {
        // Use a fresh thread so ReportiumClientProvider's ThreadLocal is guaranteed to be unset,
        // regardless of what other tests in this suite have stored on the current thread.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<Void> future = executor.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    BaseReportiumTestNgListener listener = newListener();

                    IInvokedMethod method = createMock(IInvokedMethod.class);
                    expect(method.isTestMethod()).andReturn(true);
                    replay(method);

                    ITestResult testResult = createMock(ITestResult.class);
                    expect(testResult.getStatus()).andReturn(ITestResult.SUCCESS);
                    replay(testResult);

                    assertNull(ReportiumClientProvider.get());

                    // Should not throw, even though there is no client to report to.
                    listener.afterInvocation(method, testResult);

                    verify(method, testResult);
                    return null;
                }
            });
            future.get();
        } finally {
            executor.shutdown();
        }
    }

    @Test
    public void testGetTestName() {
        ITestResult testResult = mockTestResultWithName("DummyTestClass", "someMethod");
        replay(testResult);

        BaseReportiumTestNgListener listener = newListener();
        assertEquals(listener.getTestName(testResult), "DummyTestClass::someMethod");

        verify(testResult);
    }

    @Test
    public void testGetJob_defaultsToNull() {
        BaseReportiumTestNgListener listener = newListener();
        assertNull(listener.getJob());
    }

    @Test
    public void testGetProject_defaultsToNull() {
        BaseReportiumTestNgListener listener = newListener();
        assertNull(listener.getProject());
    }

    @Test
    public void testGetTags_defaultsToNull() {
        BaseReportiumTestNgListener listener = newListener();
        ITestResult testResult = createMock(ITestResult.class);
        assertNull(listener.getTags(testResult));
    }

    @Test
    public void testGetReportiumClient_delegatesToProvider() {
        DigitalZoomClient client = createMock(DigitalZoomClient.class);
        replay(client);
        ReportiumClientProvider.set(client);

        BaseReportiumTestNgListener listener = newListener();
        assertSame(listener.getReportiumClient(), client);
    }

    private <T extends com.perfecto.reportium.test.result.TestResult> void assertAfterInvocationReportsResult(
            int status, Class<T> expectedResultType) {
        BaseReportiumTestNgListener listener = newListener();

        IInvokedMethod method = createMock(IInvokedMethod.class);
        expect(method.isTestMethod()).andReturn(true);
        replay(method);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getStatus()).andReturn(status);
        replay(testResult);

        DigitalZoomClient client = createMock(DigitalZoomClient.class);
        client.testStop(isA(expectedResultType));
        expectLastCall().once();
        replay(client);
        ReportiumClientProvider.set(client);

        listener.afterInvocation(method, testResult);

        verify(method, testResult, client);
    }
}
