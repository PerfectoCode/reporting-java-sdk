package com.perfecto.reportium.model.util;

import com.perfecto.reportium.BaseSdkTest;
import com.perfecto.reportium.client.Constants;
import com.perfecto.reportium.model.Project;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class ProjectPopulatorTest extends BaseSdkTest {
    private static String SOURCE_PROJECT_NAME = "My custom project name";
    private static String SOURCE_PROJECT_VERSION = "1.23456";
    private static String ENV_TARGET_PROJECT_VERSION = "9.876543";
    private static String ENV_TARGET_PROJECT_NAME = "this is my name";

    @BeforeMethod
    public void setup() {
        backupSystemProperties(
                Constants.SDK.projectNameParameterNameV2,
                Constants.SDK.projectNameParameterNameV1,
                Constants.SDK.projectVersionParameterNameV2,
                Constants.SDK.projectVersionParameterNameV1
        );
    }

    @Test
    public void testFullProject() {
        Project src = new Project();
        src.setName(SOURCE_PROJECT_NAME);
        src.setVersion(SOURCE_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);

        Assert.assertTrue(target != src, "New project expected");
        assertNotNull(target);
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertEquals(target.getVersion(), src.getVersion());
    }

    @Test
    public void testProject_nameOnly() {
        Project src = new Project();
        src.setName(SOURCE_PROJECT_NAME);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);

        Assert.assertTrue(target != src, "New project expected");
        assertNotNull(target);
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertNull(target.getVersion());
    }

    @Test
    public void testProject_versionOnly() {
        Project src = new Project();
        src.setVersion(SOURCE_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);

        Assert.assertTrue(target != src, "New project expected");
        assertNotNull(target);
        Assert.assertNull(target.getName());
        Assert.assertEquals(target.getVersion(), src.getVersion());
    }

    @Test
    public void testFullProjectWithEnvVariables() {
        Project src = new Project();
        src.setName(SOURCE_PROJECT_NAME);
        src.setVersion(SOURCE_PROJECT_VERSION);

        System.setProperty(Constants.SDK.projectNameParameterNameV1, ENV_TARGET_PROJECT_NAME);
        System.setProperty(Constants.SDK.projectVersionParameterNameV1, ENV_TARGET_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);

        Assert.assertTrue(target != src, "New project expected");
        assertNotNull(target);
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertEquals(target.getVersion(), src.getVersion());
    }

    @Test
    public void testPartialProject() {
        Project src = new Project();
        src.setName(SOURCE_PROJECT_NAME);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);
        assertNotNull(target);
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertEquals(target.getVersion(), src.getVersion());
    }

    @Test
    public void testPartialProjectWithEnvVariables() {
        Project src = new Project();
        src.setName(SOURCE_PROJECT_NAME);

        System.setProperty(Constants.SDK.projectNameParameterNameV1, ENV_TARGET_PROJECT_NAME);
        System.setProperty(Constants.SDK.projectVersionParameterNameV1, ENV_TARGET_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);
        assertNotNull(target);
        Assert.assertEquals(target.getName(), src.getName());
        Assert.assertEquals(target.getVersion(), ENV_TARGET_PROJECT_VERSION);
    }

    @Test
    public void testEmptyProject() {
        Project src = new Project();

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);

        Assert.assertNull(target, "Method is expected to return null for empty project");
    }

    @Test
    public void testEmptyProjectWithEnvVariables() {
        Project src = new Project();

        System.setProperty(Constants.SDK.projectNameParameterNameV1, ENV_TARGET_PROJECT_NAME);
        System.setProperty(Constants.SDK.projectVersionParameterNameV1, ENV_TARGET_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);
        assertNotNull(target);
        Assert.assertEquals(target.getName(), ENV_TARGET_PROJECT_NAME);
        Assert.assertEquals(target.getVersion(), ENV_TARGET_PROJECT_VERSION);
    }

    @Test
    public void testEmptyProjectWithEnvVariables_nameOnly() {
        Project src = new Project();

        System.setProperty(Constants.SDK.projectNameParameterNameV1, ENV_TARGET_PROJECT_NAME);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);
        assertNotNull(target);
        Assert.assertEquals(target.getName(), ENV_TARGET_PROJECT_NAME);
        Assert.assertNull(target.getVersion());
    }

    @Test
    public void testEmptyProjectWithEnvVariables_versionOnly() {
        Project src = new Project();

        System.setProperty(Constants.SDK.projectVersionParameterNameV1, ENV_TARGET_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(src);
        assertNotNull(target);
        Assert.assertNull(target.getName());
        Assert.assertEquals(target.getVersion(), ENV_TARGET_PROJECT_VERSION);
    }

    @Test
    public void testNullProject() {
        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(null);

        Assert.assertNull(target, "Method is expected to return null");
    }

    @Test
    public void testNullProjectWithEnvVariables() {
        System.setProperty(Constants.SDK.projectNameParameterNameV2, ENV_TARGET_PROJECT_NAME);
        System.setProperty(Constants.SDK.projectVersionParameterNameV2, ENV_TARGET_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(null);
        assertNotNull(target);
        Assert.assertEquals(target.getName(), ENV_TARGET_PROJECT_NAME);
        Assert.assertEquals(target.getVersion(), ENV_TARGET_PROJECT_VERSION);
    }

    @Test
    public void testNullProjectWithEnvVariables_nameOnly() {
        System.setProperty(Constants.SDK.projectNameParameterNameV2, ENV_TARGET_PROJECT_NAME);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(null);
        assertNotNull(target);
        Assert.assertEquals(target.getName(), ENV_TARGET_PROJECT_NAME);
        Assert.assertNull(target.getVersion());
    }

    @Test
    public void testNullProjectWithEnvVariables_versionOnly() {
        System.setProperty(Constants.SDK.projectVersionParameterNameV2, ENV_TARGET_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(null);
        assertNotNull(target);
        Assert.assertNull(target.getName());
        Assert.assertEquals(target.getVersion(), ENV_TARGET_PROJECT_VERSION);
    }

    @Test
    public void testNullProjectWithEnvVariables_oldParameterNames() {
        System.setProperty(Constants.SDK.projectNameParameterNameV1, ENV_TARGET_PROJECT_NAME);
        System.setProperty(Constants.SDK.projectVersionParameterNameV1, ENV_TARGET_PROJECT_VERSION);

        Project target = ExecutionContextPopulator.populateMissingProjectPropertiesFromEnvVariables(null);
        assertNotNull(target);
        Assert.assertEquals(target.getName(), ENV_TARGET_PROJECT_NAME);
        Assert.assertEquals(target.getVersion(), ENV_TARGET_PROJECT_VERSION);
    }
}
