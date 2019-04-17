package com.perfecto.reportium.testng;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Created by eitanp on 20/3/16.
 */
public class TestListener implements ITestListener {

    private void log(String method,ITestResult result) {
        Reporter.log(method + " " + result.getInstanceName(), true);
    }

    @Override
    public void onTestStart(ITestResult result) {
       log("onTestStart", result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log("onTestSuccess", result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log("onTestFailure", result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log("onTestSkipped", result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log("onTestFailedButWithinSuccessPercentage", result);
    }

    @Override
    public void onStart(ITestContext context) {
        Reporter.log("onStart " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        Reporter.log("onFinish " + context.getName());
    }
}
