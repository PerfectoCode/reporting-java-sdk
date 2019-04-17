package com.perfecto.reportium.model.util;

import com.perfecto.reportium.BaseSdkTest;
import com.perfecto.reportium.client.Constants;
import com.perfecto.reportium.model.Job;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Job}
 */
public class JobPopulatorTest extends BaseSdkTest {

    private static int SOURCE_JOB_NUMBER = 123456;
    private static String SOURCE_JOB_NAME = "My custom job name";
    private static String SOURCE_JOB_BRANCH = "job-branch";

    private static long ENV_TARGET_JOB_NUMBER = 9876543L;
    private static String ENV_TARGET_JOB_NAME = "this is my name";
    private static String ENV_TARGET_JOB_BRANCH = "my-branch";

    @BeforeMethod
    public void setup() {
        backupSystemProperties(
                Constants.SDK.jobNumberParameterNameV2,
                Constants.SDK.jobNumberParameterNameV1,
                Constants.SDK.jobNameParameterNameV2,
                Constants.SDK.jobNameParameterNameV1
        );
    }

    @Test
    public void testFullJob() {
        Job src = new Job();
        src.setNumber(SOURCE_JOB_NUMBER);
        src.setName(SOURCE_JOB_NAME);
        src.setBranch(SOURCE_JOB_BRANCH);

        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(src);

        Assert.assertTrue(target != src, "New job expected");
        Assert.assertEquals(target.getNumber(), src.getNumber());
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertEquals(target.getBranch(), src.getBranch());
    }

    @Test
    public void testFullJobWithEnvVariables() {
        Job src = new Job();
        src.setNumber(SOURCE_JOB_NUMBER);
        src.setName(SOURCE_JOB_NAME);
        src.setBranch(SOURCE_JOB_BRANCH);

        System.setProperty(Constants.SDK.jobNumberParameterNameV1, Long.toString(ENV_TARGET_JOB_NUMBER));
        System.setProperty(Constants.SDK.jobNameParameterNameV1, ENV_TARGET_JOB_NAME);
        System.setProperty(Constants.SDK.jobBranchParameterBranchV1, ENV_TARGET_JOB_BRANCH);

        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(src);

        Assert.assertTrue(target != src, "New job expected");
        Assert.assertEquals(target.getNumber(), src.getNumber());
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertEquals(target.getBranch(), src.getBranch());
    }

    @Test
    public void testPartialJob() {
        Job src = new Job();
        src.setName(SOURCE_JOB_NAME);

        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(src);

        Assert.assertEquals(target.getNumber(), src.getNumber());
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertEquals(null, src.getBranch());
    }

    @Test
    public void testPartialJobWithEnvVariables() {
        Job src = new Job();
        src.setName(SOURCE_JOB_NAME);

        System.setProperty(Constants.SDK.jobNumberParameterNameV1, Long.toString(ENV_TARGET_JOB_NUMBER));
        System.setProperty(Constants.SDK.jobNameParameterNameV1, ENV_TARGET_JOB_NAME);
        System.setProperty(Constants.SDK.jobBranchParameterBranchV1, ENV_TARGET_JOB_BRANCH);

        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(src);

        Assert.assertEquals(target.getNumber(), ENV_TARGET_JOB_NUMBER);
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertEquals(target.getBranch(), ENV_TARGET_JOB_BRANCH);
    }

    @Test
    public void testEmptyJob() {
        Job src = new Job();

        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(src);

        Assert.assertNull(target, "Method is expected to return null for nameless jobs");
    }

    @Test
    public void testEmptyJobWithEnvVariables() {
        Job src = new Job();

        System.setProperty(Constants.SDK.jobNumberParameterNameV1, Long.toString(ENV_TARGET_JOB_NUMBER));
        System.setProperty(Constants.SDK.jobBranchParameterBranchV1, ENV_TARGET_JOB_BRANCH);

        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(src);

        Assert.assertNull(target, "Method is expected to return null for nameless jobs");
    }

    @Test
    public void testNullJob() {
        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(null);

        Assert.assertNull(target, "Method is expected to return null for nameless jobs");
    }

    @Test
    public void testNullJobWithEnvVariables() {
        System.setProperty(Constants.SDK.jobNumberParameterNameV2, Long.toString(ENV_TARGET_JOB_NUMBER));
        System.setProperty(Constants.SDK.jobNameParameterNameV2, ENV_TARGET_JOB_NAME);
        System.setProperty(Constants.SDK.jobBranchParameterBranchV2, ENV_TARGET_JOB_BRANCH);

        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(null);

        Assert.assertEquals(target.getNumber(), ENV_TARGET_JOB_NUMBER);
        Assert.assertEquals(target.getName(), ENV_TARGET_JOB_NAME);
        Assert.assertEquals(target.getBranch(), ENV_TARGET_JOB_BRANCH);
    }

    @Test
    public void testNullJobWithEnvVariables_oldParameterNames() {
        System.setProperty(Constants.SDK.jobNumberParameterNameV1, Long.toString(ENV_TARGET_JOB_NUMBER));
        System.setProperty(Constants.SDK.jobNameParameterNameV1, ENV_TARGET_JOB_NAME);
        System.setProperty(Constants.SDK.jobBranchParameterBranchV1, ENV_TARGET_JOB_BRANCH);

        Job target = ExecutionContextPopulator.populateMissingJobPropertiesFromEnvVariables(null);

        Assert.assertEquals(target.getNumber(), ENV_TARGET_JOB_NUMBER);
        Assert.assertEquals(target.getName(), ENV_TARGET_JOB_NAME);
        Assert.assertEquals(target.getBranch(), ENV_TARGET_JOB_BRANCH);
    }
}
