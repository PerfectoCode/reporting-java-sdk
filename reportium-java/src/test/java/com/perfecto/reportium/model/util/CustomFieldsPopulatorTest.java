package com.perfecto.reportium.model.util;

import com.perfecto.reportium.client.Constants;
import com.perfecto.reportium.model.CustomField;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class CustomFieldsPopulatorTest {
    private Set<CustomField> srcCustomFields;
    private Set<CustomField> envCustomFields;

    private String existingCustomFields;

    private static String ENV_TARGET_VALID = "name1= , name2 = value2, name3=value3";
    private static String ENV_TARGET_INVALID_1 = "=value1";
    private static String ENV_TARGET_INVALID_2 = "invalid";

    @BeforeClass
    public void before() {
        srcCustomFields = new HashSet<>();
        srcCustomFields.add(new CustomField("name1", "value1"));
        srcCustomFields.add(new CustomField("name4 ", "value4"));
    }

    @BeforeMethod
    public void setup() {
        existingCustomFields = System.getProperty(Constants.SDK.jvmCustomFieldsParameterName);
    }

    @AfterMethod
    public void teardown() {
        restoreParamValue(Constants.SDK.jvmCustomFieldsParameterName, existingCustomFields);
    }

    private void restoreParamValue(String key, String previousValue) {
        // Restore system properties
        if (previousValue != null) {
            // Throws an NPE if existingCustomFields is null
            System.setProperty(key, previousValue);
        } else {
            // If this prop didn't have a value before we need to make sure it is nullified now
            System.clearProperty(key);
        }
    }

    @Test
    public void testCustomFields_allResources() {
        System.setProperty(Constants.SDK.jvmCustomFieldsParameterName, ENV_TARGET_VALID);
        Set<CustomField> target = ExecutionContextPopulator.populateMissingCustomFieldsPropertiesFromEnvVariables(srcCustomFields);

        envCustomFields = new HashSet<>();
        envCustomFields.add(new CustomField("name1", ""));
        envCustomFields.add(new CustomField("name2", "value2"));
        envCustomFields.add(new CustomField("name3", "value3"));

        Set<CustomField> src = new HashSet<>();
        src.addAll(envCustomFields);
        src.add(new CustomField("name4 ", "value4"));

        assertThat(target, containsInAnyOrder(src.toArray()));
    }

    @Test
    public void testCustomFields_envVariablesOnly_valid() {
        System.setProperty(Constants.SDK.jvmCustomFieldsParameterName, ENV_TARGET_VALID);
        envCustomFields = new HashSet<>();
        envCustomFields.add(new CustomField("name1", ""));
        envCustomFields.add(new CustomField("name2", "value2"));
        envCustomFields.add(new CustomField("name3", "value3"));

        Set<CustomField> target = ExecutionContextPopulator.populateMissingCustomFieldsPropertiesFromEnvVariables(null);

        assertThat(target, containsInAnyOrder(envCustomFields.toArray()));
    }


    @Test
    public void testCustomFields_envVariables_invalid() {
        List<String> invalidEnvCustomFields = Arrays.asList(ENV_TARGET_INVALID_1, ENV_TARGET_INVALID_2);

        for (String invalidEnvCustomField : invalidEnvCustomFields) {
            System.setProperty(Constants.SDK.jvmCustomFieldsParameterName, invalidEnvCustomField);
            try {
                ExecutionContextPopulator.populateMissingCustomFieldsPropertiesFromEnvVariables(srcCustomFields);
                Assert.fail("Populate invalid env custom field :" + invalidEnvCustomField + " should throw exception");
            } catch (RuntimeException e) {
                Assert.assertEquals(e.getMessage(), String.format(ExecutionContextPopulator.INVALID_ENV_CUSTOM_FIELD_ERROR, invalidEnvCustomField));
            }
        }
    }


    @Test
    public void testCustomFields_srcOnly() {
        System.setProperty(Constants.SDK.jvmCustomFieldsParameterName, "");
        Set<CustomField> target = ExecutionContextPopulator.populateMissingCustomFieldsPropertiesFromEnvVariables(srcCustomFields);
        assertThat(target, containsInAnyOrder(srcCustomFields.toArray()));
    }

    @Test
    public void testEmptyCustomFields() {
        Set<CustomField> emptySrc = new HashSet<>();
        Set<CustomField> target = ExecutionContextPopulator.populateMissingCustomFieldsPropertiesFromEnvVariables(emptySrc);
        Assert.assertEquals(target, new HashSet<>(), "Method is expected to return empty set for no custom fields");
    }

    @Test
    public void testNullCustomFields() {
        Set<CustomField> target = ExecutionContextPopulator.populateMissingCustomFieldsPropertiesFromEnvVariables(null);
        Assert.assertEquals(target, new HashSet<>(), "Method is expected to return empty set for null src as argument");
    }
}
