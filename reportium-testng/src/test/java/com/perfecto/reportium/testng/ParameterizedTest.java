package com.perfecto.reportium.testng;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created by eitanp on 20/3/16.
 */
public class ParameterizedTest {

    private String state = "foo";

    //This method will provide data to any test method that declares that its Data Provider is named "test1"
    @DataProvider(name = "test1", parallel = true)
    public Object[][] createData1() {
        return new Object[][]{
                {"Cedric", 36},
                {"Anne", 37},
        };
    }

    @BeforeTest
    public void setState() {
        state = "bar";
        System.out.println("BeforeTest");
    }

    @AfterSuite
    public void afterParameterizedTestSuite() {
        // Do nothing
        System.out.println("afterTest");
    }

    //This test method declares that its data should be supplied by the Data Provider named "test1"
    @Test(dataProvider = "test1")
    public void verifyData1(String n1, Integer n2) {
        Reporter.log(n1 + " " + n2, true); // Prints "Anne 37", and then "Cedric 36"
        Assert.assertEquals(state, "bar");
    }

}
