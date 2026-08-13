package com.perfecto.reportium.model.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by eitanp on 12/4/16.
 */
public class ListUtilsTest {

    @Test
    public void testMergeDisjointLists() {
        List<String> list1 = Arrays.asList("123", "456");
        List<String> list2 = Arrays.asList("789");
        List<String> merged = ListUtils.mergeLists(list1, list2);
        Assert.assertEquals(merged.size(), 3);
        Assert.assertTrue(merged.contains("123"));
        Assert.assertTrue(merged.contains("456"));
        Assert.assertTrue(merged.contains("789"));
    }

    @Test
    public void testMergeWithEmptyList() {
        List<String> list1 = Arrays.asList("123", "456");
        List<String> list2 = Collections.emptyList();
        List<String> merged = ListUtils.mergeLists(list1, list2);
        Assert.assertEquals(merged.size(), 2);
        Assert.assertTrue(merged.contains("123"));
        Assert.assertTrue(merged.contains("456"));
    }

    @Test
    public void testMergeWithDuplicates() {
        List<String> list1 = Arrays.asList("123", "456");
        List<String> list2 = Arrays.asList("456", "789");
        List<String> merged = ListUtils.mergeLists(list1, list2);
        Assert.assertEquals(merged.size(), 3);
        Assert.assertTrue(merged.contains("123"));
        Assert.assertTrue(merged.contains("456"));
        Assert.assertTrue(merged.contains("789"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Arguments cannot be null")
    public void testMergeWithNullList() {
        List<String> list1 = Arrays.asList("123", "456");
        List<String> list2 = null;
        ListUtils.mergeLists(list1, list2);
    }
}
