package com.perfecto.reportium.test;

import com.perfecto.reportium.model.CustomField;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * TestContext unit test
 */
public class TestContextTest {

    @Test
    public void testEmptyContext() {
        TestContext context = new TestContext();
        assertEquals(context.getTestExecutionTags().size(), 0, "Context identifiers should be empty");
        assertEquals(context.getCustomFields().size(), 0, "Context identifiers should be empty");
    }


    @Test
    public void testPopulatedContextWithOneTag() {
        String testExecutionTag = "aaa";
        TestContext context = new TestContext(testExecutionTag);
        assertEquals(context.getTestExecutionTags().size(), 1, "Context identifiers should include 1 identifier");
        assertEquals(context.getTestExecutionTags().iterator().next(), testExecutionTag, "Should return the identifier it was created with");
    }

    @Test
    public void testPopulatedContextWithManyTags() {
        String testExecutionTag1 = "aaa";
        String testExecutionTag2 = "bbb";
        String testExecutionTag3 = "ccc";
        TestContext context = new TestContext.Builder()
                .withTestExecutionTags(testExecutionTag1, testExecutionTag2, testExecutionTag3)
                .build();
        assertEquals(context.getTestExecutionTags().size(), 3, "Context identifiers should include 3 identifiers");
        assertTrue(context.getTestExecutionTags().containsAll(Arrays.asList(testExecutionTag1, testExecutionTag2, testExecutionTag3)));
    }

    @Test
    public void testPopulatedContextCustomFields() {
        CustomField customField1 = new CustomField("name1", "v1");
        CustomField customField2 = new CustomField("name2", "v2");
        CustomField customField3 = new CustomField("name3", "v3");
        TestContext context = new TestContext.Builder()
                .withCustomFields(customField1, customField2, customField3, customField1)
                .build();
        assertEquals(context.getCustomFields().size(), 3, "Context identifiers should include 3 identifiers");
        assertTrue(context.getCustomFields().containsAll(Arrays.asList(customField1, customField2, customField3)), "Should return the identifier it was created with");
    }

    @Test
    public void testNullCustomFields() {
        CustomField customField = null;
        TestContext context = new TestContext.Builder()
                .withCustomFields(customField)
                .build();

        assertEquals(0, context.getCustomFields().size());
    }

    @Test
    public void testNullContextTags() {
        final String tag = null;
        TestContext context = new TestContext.Builder()
                .withTestExecutionTags(tag)
                .build();

        assertEquals(0, context.getTestExecutionTags().size());
    }

    @Test
    public void testNullContextTagsConstructor() {
        TestContext context = new TestContext(null, null, "");
        assertEquals(0, context.getTestExecutionTags().size());
    }

    @Test
    public void testNullVarargsArrayConstructor() {
        TestContext context = new TestContext((String[]) null);
        assertEquals(0, context.getTestExecutionTags().size());
    }

    @Test
    public void testWithTestExecutionTags_varargsNullArray() {
        TestContext context = new TestContext.Builder()
                .withTestExecutionTags((String[]) null)
                .build();
        assertEquals(0, context.getTestExecutionTags().size());
    }

    @Test
    public void testWithCustomFields_varargsNullArray() {
        TestContext context = new TestContext.Builder()
                .withCustomFields((CustomField[]) null)
                .build();
        assertEquals(0, context.getCustomFields().size());
    }

    @Test
    public void testWithTestExecutionTags_collectionNull() {
        TestContext context = new TestContext.Builder()
                .withTestExecutionTags((Collection<String>) null)
                .build();
        assertEquals(0, context.getTestExecutionTags().size());
    }

    @Test
    public void testWithTestExecutionTags_collectionEmpty() {
        TestContext context = new TestContext.Builder()
                .withTestExecutionTags(Collections.<String>emptyList())
                .build();
        assertEquals(0, context.getTestExecutionTags().size());
    }

    @Test
    public void testWithCustomFields_collectionNull() {
        TestContext context = new TestContext.Builder()
                .withCustomFields((Collection<CustomField>) null)
                .build();
        assertEquals(0, context.getCustomFields().size());
    }

    @Test
    public void testWithCustomFields_collectionEmpty() {
        TestContext context = new TestContext.Builder()
                .withCustomFields(Collections.<CustomField>emptyList())
                .build();
        assertEquals(0, context.getCustomFields().size());
    }

    @Test
    public void testCopyConstructor() {
        TestContext original = new TestContext.Builder()
                .withTestExecutionTags("tag1")
                .withCustomFields(new CustomField("name1", "value1"))
                .build();

        TestContext copy = new TestContext.Builder(original).build();

        assertEquals(copy.getTestExecutionTags(), original.getTestExecutionTags());
        assertEquals(copy.getCustomFields(), original.getCustomFields());
    }

    @Test
    public void testToString() {
        TestContext context = new TestContext.Builder()
                .withTestExecutionTags("tag1")
                .withCustomFields(new CustomField("name1", "value1"))
                .build();
        String toString = context.toString();
        assertTrue(toString.contains("tag1"));
        assertTrue(toString.contains("name1"));
    }
}
