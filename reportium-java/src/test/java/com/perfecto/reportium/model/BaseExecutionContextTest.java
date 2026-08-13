package com.perfecto.reportium.model;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link BaseExecutionContext} and its {@link BaseExecutionContext.Builder}.
 * <p>
 * {@link BaseExecutionContext.Builder} is protected, so it is exercised here directly (same package)
 * to cover the branches that aren't reachable through the public {@link PerfectoExecutionContext} subclass,
 * such as the collection-typed with* overloads and the copy constructor.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BaseExecutionContextTest {

    @Test
    public void testPlainBuild() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        BaseExecutionContext context = builder.build();
        assertNotNull(context);
        assertEquals(context.getContextTags().size(), 0);
        assertEquals(context.getCustomFields().size(), 0);
    }

    @Test
    public void testWithContextTags_collectionNull() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        builder.withContextTags((java.util.Collection<String>) null);
        BaseExecutionContext context = builder.build();
        assertEquals(context.getContextTags().size(), 0);
    }

    @Test
    public void testWithContextTags_collectionEmpty() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        builder.withContextTags(Collections.<String>emptyList());
        BaseExecutionContext context = builder.build();
        assertEquals(context.getContextTags().size(), 0);
    }

    @Test
    public void testWithContextTags_collectionWithValidTag() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        builder.withContextTags(Arrays.asList("tagA", "tagB"));
        BaseExecutionContext context = builder.build();
        assertTrue(context.getContextTags().containsAll(Arrays.asList("tagA", "tagB")));
    }

    @Test
    public void testWithContextTags_varargsNull() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        builder.withContextTags((String[]) null);
        BaseExecutionContext context = builder.build();
        assertEquals(context.getContextTags().size(), 0);
    }

    @Test
    public void testWithCustomFields_collectionNull() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        builder.withCustomFields((java.util.Collection<CustomField>) null);
        BaseExecutionContext context = builder.build();
        assertEquals(context.getCustomFields().size(), 0);
    }

    @Test
    public void testWithCustomFields_collectionEmpty() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        builder.withCustomFields(Collections.<CustomField>emptyList());
        BaseExecutionContext context = builder.build();
        assertEquals(context.getCustomFields().size(), 0);
    }

    @Test
    public void testWithCustomFields_collectionWithValidField() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        CustomField field = new CustomField("name", "value");
        builder.withCustomFields(Arrays.asList(field));
        BaseExecutionContext context = builder.build();
        assertTrue(context.getCustomFields().contains(field));
    }

    @Test
    public void testWithCustomFields_varargsNull() {
        BaseExecutionContext.Builder builder = new BaseExecutionContext.Builder();
        builder.withCustomFields((CustomField[]) null);
        BaseExecutionContext context = builder.build();
        assertEquals(context.getCustomFields().size(), 0);
    }

    @Test
    public void testCopyConstructor() {
        BaseExecutionContext.Builder original = new BaseExecutionContext.Builder();
        original.withJob(new Job("job", 1));
        original.withProject(new Project("proj", "1.0"));
        original.withCustomFields(new CustomField("name", "value"));
        BaseExecutionContext originalContext = original.build();

        BaseExecutionContext.Builder copyBuilder = new BaseExecutionContext.Builder(originalContext);
        BaseExecutionContext copiedContext = copyBuilder.build();

        assertEquals(copiedContext.getJob().getName(), "job");
        assertEquals(copiedContext.getProject().getName(), "proj");
        assertEquals(copiedContext.getCustomFields(), originalContext.getCustomFields());
        assertEquals(copiedContext.getContextTags(), originalContext.getContextTags());
    }
}
