package com.perfecto.reportium.testng;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

/**
 * Test sample for playing with TestNG
 */
// @Listeners(com.perfecto.reportium.testng.ReportiumTestNgListener.class)
public class SkippedTest {

    @BeforeTest
    public void beforeTest() {
        Reporter.log("BeforeTest", true);
    }

    @BeforeClass
    public void beforeClass() {
        Reporter.log("BeforeClass", true);
    }

    @BeforeGroups
    public void beforeGroups() {
        Reporter.log("BeforeGroups", true);
    }

    @BeforeMethod
    public void beforeMethod() {
        Reporter.log("BeforeMethod", true);
    }

    @BeforeSuite
    public void beforeSuite() {
        Reporter.log("BeforeSuite", true);
    }

    @AfterTest
    public void afterTest() {
        Reporter.log("AfterTest", true);
    }

    @AfterClass
    public void afterClass() {
        Reporter.log("AfterClass", true);
    }

    @AfterGroups
    public void afterGroups() {
        Reporter.log("AfterGroups", true);
    }

    @AfterMethod
    public void afterMethod() {
        Reporter.log("AfterMethod", true);
    }

    @AfterSuite
    public void afterSuite() {
        Reporter.log("AfterSuite", true);
    }

    @Test(enabled = false, description = "Test is skipped for a good reason")
    public void mySkippedTest() {
        Reporter.log("Test is skipped!!", true);
    }

    @Test(enabled = false, alwaysRun = true, description = "Checking what gets printed to the report!?")
    public void skippedAlwaysRun() {
        Reporter.log("Test is skipped but always runs??", true);
    }

    @Test
    public void passme() {
        Reporter.log("Test is not skipped", true);
    }

    @Test(enabled = false)
    public void failme() {
        Assert.fail("Something bad happened");
    }
}
