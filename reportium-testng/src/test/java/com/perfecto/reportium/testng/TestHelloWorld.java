package com.perfecto.reportium.testng;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Listeners(SampleTestNgListener.class)
public class TestHelloWorld {

//    @Test(retryAnalyzer = RetryAnalyzer.class)
//    public void test1() {
//        assertTrue(false);
//        Assert.assertNotEquals("str1", "str2", "str1 should be different than str2");
//    }
//
//    @Test(enabled = false)
//    public void test2() {
//        // Makes the test fail
//        Assert.assertEquals("str1", "str2", "Expected failure - str1 is different than str2");
//    }
//
//    @Test(invocationCount = 3, testName = "Invoked multiple times")
//    public void test3() {
//        // Gets invoked X times
//        assertTrue(true);
//    }

    @Test(expectedExceptions = { RuntimeException.class })
    public void test4() {
        throw new RuntimeException("Should not fail the test");
    }
}
