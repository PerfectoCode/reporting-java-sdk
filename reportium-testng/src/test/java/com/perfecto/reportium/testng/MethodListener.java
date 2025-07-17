package com.perfecto.reportium.testng;

import org.testng.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eitanp on 20/3/16.
 */
public class MethodListener implements IInvokedMethodListener {

    private Map<String, Boolean> status(ITestNGMethod testMethod) {
        Map<String, Boolean> testStatus = new HashMap<>(10);

        testStatus.put("beforeClassConfiguration", testMethod.isBeforeClassConfiguration());
        testStatus.put("afterClassConfiguration", testMethod.isAfterClassConfiguration());
        testStatus.put("afterGroupsConfiguration", testMethod.isAfterGroupsConfiguration());
        testStatus.put("afterMethodConfiguration", testMethod.isAfterMethodConfiguration());
        testStatus.put("afterSuiteConfiguration", testMethod.isAfterSuiteConfiguration());
        testStatus.put("afterTestConfiguration", testMethod.isAfterTestConfiguration());
        testStatus.put("alwaysRun", testMethod.isAlwaysRun());
        testStatus.put("beforeGroupsConfiguration", testMethod.isBeforeGroupsConfiguration());
        testStatus.put("beforeMethodConfiguration", testMethod.isBeforeMethodConfiguration());
        testStatus.put("beforeSuiteConfiguration", testMethod.isBeforeSuiteConfiguration());
        testStatus.put("beforeTestConfiguration", testMethod.isBeforeTestConfiguration());
        testStatus.put("test", testMethod.isTest());

        return testStatus;

    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult, ITestContext iTestContext) {
        ITestNGMethod testMethod = iInvokedMethod.getTestMethod();
        Map<String, Boolean> status = status(testMethod);

        String phases = "";
        for (Map.Entry<String, Boolean> entry: status.entrySet()) {
            if (entry.getValue()) {
                phases += " " + entry.getKey();
            }
        }

        Reporter.log("name = " + testMethod.getMethodName() + " phase: " + phases, true);
        Reporter.log("before passed tests = " + iTestContext.getPassedTests().size(), true);
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult, ITestContext iTestContext) {
        Reporter.log("after passed tests = " + iTestContext.getPassedTests().size());
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }
}
