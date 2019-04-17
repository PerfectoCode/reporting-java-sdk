package com.perfecto.reportium.testng;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * Created by michaeld on 01-Jul-17.
 */
public class SampleTestNgListener implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        System.out.println("beforeInvocation " + iInvokedMethod.isTestMethod());
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        System.out.println("afterInvocation " + iInvokedMethod.isTestMethod() + " " + iTestResult.getStatus() + " " + iTestResult.getThrowable());
    }
}
