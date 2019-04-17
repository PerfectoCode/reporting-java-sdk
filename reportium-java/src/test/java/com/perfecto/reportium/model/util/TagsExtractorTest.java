package com.perfecto.reportium.model.util;

import com.perfecto.reportium.BaseSdkTest;
import com.perfecto.reportium.client.Constants;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * This unit test runs in the context of a developer machine and on CI.
 * the results will differ in those profiles.
 * <p>
 * Another alternative is to add a "CI" profile in pom.xml and run different unit tests for the same class
 * in the different profiles.
 * <p>
 * Though this solution is not clean, it is simpler to understand and maintain.
 */
public class TagsExtractorTest extends BaseSdkTest {

    private boolean isCI;
    private boolean hasReportiumTags;

    @BeforeClass
    public void checkExecutionProfile() {
        isCI = (!StringUtils.isEmpty(System.getProperty("CI")) ||
                !StringUtils.isEmpty(System.getProperty("JENKINS_URL")));

        hasReportiumTags = !StringUtils.isEmpty(System.getProperty(Constants.SDK.jvmTagsParameterNameV1));
    }

    @BeforeMethod
    public void setup() {
        backupSystemProperties(Constants.SDK.jvmTagsParameterNameV1, Constants.SDK.jvmTagsParameterNameV2);
    }

    @Test
    public void testVanillaTags() {
        List<String> systemContextTags = TagsExtractor.getPredefinedContextTags();

        /*
         * This unit test runs in the context of a developer machine and on CI.
         * the results will differ in those profiles.
         *
         * Another alternative is to add a "CI" profile in pom.xml and run different unit tests for the same class
         * in the different profiles.
         */
        if (isCI || hasReportiumTags) {
            Assert.assertTrue(systemContextTags.size() > 0, "System context tags expected to exist");
        } else {
            Assert.assertTrue(systemContextTags.size() == 0, "System context tags not expected");
        }
    }

    @Test
    public void testCustomTags() {
        System.setProperty(Constants.SDK.jvmTagsParameterNameV2, "AAA,BBB,CCC,DDD");
        List<String> systemContextTags = TagsExtractor.getPredefinedContextTags();

        Assert.assertTrue(systemContextTags.contains("AAA"));
        Assert.assertTrue(systemContextTags.contains("BBB"));
        Assert.assertTrue(systemContextTags.contains("CCC"));
    }

    @Test
    public void testSingleCustomTag() {
        System.setProperty(Constants.SDK.jvmTagsParameterNameV2, "12345");
        List<String> systemContextTags = TagsExtractor.getPredefinedContextTags();

        Assert.assertTrue(systemContextTags.contains("12345"));
    }

    @Test
    public void testSingleCustomTagOld() {
        System.setProperty(Constants.SDK.jvmTagsParameterNameV1, "54321");
        List<String> systemContextTags = TagsExtractor.getPredefinedContextTags();

        Assert.assertTrue(systemContextTags.contains("54321"));
    }
}
