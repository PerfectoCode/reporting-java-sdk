package com.perfecto.reportium.client;

import com.perfecto.reportium.test.result.TestResultFactory;
import com.perfecto.reportium.test.result.TestResultFailure;
import com.perfecto.reportium.test.result.TestResultSuccess;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test case for {@link PerfectoTestResultVisitor}
 */
public class PerfectoTestResultVisitorTest {

    @Test
    public void testVisitSuccess() {
        Map<String, Object> params = new HashMap<>();
        PerfectoTestResultVisitor visitor = new PerfectoTestResultVisitor(params);

        visitor.visit((TestResultSuccess) TestResultFactory.createSuccess());

        assertEquals(params.get("success"), true);
    }

    @Test
    public void testVisitFailureWithFailureReason() {
        Map<String, Object> params = new HashMap<>();
        PerfectoTestResultVisitor visitor = new PerfectoTestResultVisitor(params);
        TestResultFailure failure = (TestResultFailure) TestResultFactory.createFailure("boom", null, "myReason");

        visitor.visit(failure);

        assertEquals(params.get("success"), false);
        assertEquals(params.get("failureDescription"), "boom");
        assertEquals(params.get("failureReason"), "myReason");
    }

    @Test
    public void testVisitFailureWithoutFailureReason() {
        Map<String, Object> params = new HashMap<>();
        PerfectoTestResultVisitor visitor = new PerfectoTestResultVisitor(params);
        TestResultFailure failure = (TestResultFailure) TestResultFactory.createFailure("boom", null, "   ");

        visitor.visit(failure);

        assertEquals(params.get("success"), false);
        assertEquals(params.get("failureDescription"), "boom");
        assertFalse(params.containsKey("failureReason"));
    }
}
