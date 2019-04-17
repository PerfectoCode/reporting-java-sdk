package com.perfecto.reportium.testng;

import com.perfecto.reportium.client.DigitalZoomClient;
import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.result.TestResultFactory;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;

/**
 * Created by michaeld on 01-Jul-17.
 */
public class CustomTestNgListener extends ReportiumTestNgListener {
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // TestNg rule marks test that is being retried as skipped
        // Need to report it as failure to DigitalZoom
        if (testResult.getStatus() == ITestResult.SKIP) {
            DigitalZoomClient reportiumClient = getReportiumClient();
            if (reportiumClient != null) {
                reportiumClient.testStop(TestResultFactory.createFailure(testResult.getThrowable()));
            }
        } else {
            super.afterInvocation(method, testResult);
        }
    }
}
