package com.perfecto.reportium.testng;

import org.testng.*;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by eitanp on 20/3/16.
 */
public class PerfectoReporter implements IReporter {

    Logger logger = Logger.getLogger(getClass().getName());

    private void print(String title, List<ITestResult> results) {
        logger.info(title);
        for (ITestResult result : results) {
            logger.info(title + " Method " + result.getMethod().getMethodName());
        }
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        logger.info("Generating report");

        for (ISuite suite : suites) {
            List<ITestResult> failedList = new ArrayList<>();
            List<ITestResult> passedList = new ArrayList<>();
            List<ITestResult> skippedList = new ArrayList<>();
            String suiteName = suite.getName();
            Map<String, ISuiteResult> suiteResults = suite.getResults();
            for (ISuiteResult sr : suiteResults.values()) {
                ITestContext tc = sr.getTestContext();
                if (tc.getFailedTests().getAllResults() != null && tc.getFailedTests().getAllResults().size() > 0) {
                    failedList.addAll(tc.getFailedTests().getAllResults());
                }
                if (tc.getPassedTests().getAllResults() != null && tc.getPassedTests().getAllResults().size() > 0) {
                    passedList.addAll(tc.getPassedTests().getAllResults());
                }
                if (tc.getSkippedTests().getAllResults() != null && tc.getSkippedTests().getAllResults().size() > 0) {
                    skippedList.addAll(tc.getSkippedTests().getAllResults());
                }
            }
            print(suiteName + " Failed", failedList);
            print(suiteName + " Skipped", skippedList);
            print(suiteName + " Passed", passedList);
        }
        List<String> output = Reporter.getOutput();
        logger.info(output.toString());
    }
}
