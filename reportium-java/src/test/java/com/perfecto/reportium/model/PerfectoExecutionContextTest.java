package com.perfecto.reportium.model;

import com.perfecto.reportium.BaseSdkTest;
import com.perfecto.reportium.client.Constants;
import org.easymock.EasyMock;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PerfectoExecutionContextTest extends BaseSdkTest {

    @BeforeMethod
    public void setup() {
        backupSystemProperties(Constants.SDK.jvmTagsParameterNameV1, Constants.SDK.jvmTagsParameterNameV2);
    }

    @Test
    public void test_populateContextTagsFromSystemProperties() {
        String tagName = "tag1";
        System.setProperty(Constants.SDK.jvmTagsParameterNameV2, tagName);
        WebDriver webDriverMock = EasyMock.createNiceMock(WebDriver.class);
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .build();

        Assert.assertTrue(perfectoExecutionContext.getContextTags().contains(tagName));
    }

    @Test
    public void test_nullCustomField() {
        final CustomField customField = null;
        WebDriver webDriverMock = EasyMock.createNiceMock(WebDriver.class);
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .withCustomFields(customField)
                .build();

        assertEquals(0, perfectoExecutionContext.getCustomFields().size());
    }

    @Test
    public void test_nullContextTags() {
        final String tag = null;
        WebDriver webDriverMock = EasyMock.createNiceMock(WebDriver.class);
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withWebDriver(webDriverMock)
                .withContextTags(tag)
                .build();

        assertEquals(0, perfectoExecutionContext.getContextTags().size());
    }
}
