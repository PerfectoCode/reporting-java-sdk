package com.perfecto.reportium.testng.wd;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

/**
 * Created by eitanp on 20/3/16.
 */
public class SeleniumTest {

    @BeforeSuite
    public void setup() throws MalformedURLException {
        System.out.println("Run started");

        // TODO: Set your cloud host and credentials
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String host = "web-staging.perfectomobile.com";
        capabilities.setCapability("user", "");
        capabilities.setCapability("password", "");

        // TODO: Set the Web Machine configuration
        capabilities.setCapability("platformName", "Windows");
        capabilities.setCapability("platformVersion", "8.1");
        capabilities.setCapability("browserName", "Firefox");
        capabilities.setCapability("browserVersion", "40");

//        RemoteWebDriver driver = new RemoteWebDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
//        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }
    @Test(description = "The 'hello world' version of Selenium tests")
    public void cheese() {

    }

}
