package com.perfecto.reportium.imports.client;

import com.perfecto.reportium.imports.model.event.TestEndEvent;
import com.perfecto.reportium.imports.model.event.TestExecutionStatus;
import com.perfecto.reportium.test.result.TestResultFailure;
import com.perfecto.reportium.test.result.TestResultSuccess;
import com.perfecto.reportium.test.result.TestResultVisitor;

public class TestEndEventVisitor implements TestResultVisitor {

    private TestEndEvent testEndEvent;

    public TestEndEventVisitor(TestEndEvent testEndEvent) {
        this.testEndEvent = testEndEvent;
    }

    public void visit(TestResultSuccess testResultSuccess) {
        testEndEvent.setStatus(TestExecutionStatus.PASSED);
    }

    public void visit(TestResultFailure testResultFailure) {
        testEndEvent.setStatus(TestExecutionStatus.FAILED);
        testEndEvent.setMessage(testResultFailure.getMessage());
        testEndEvent.setFailureReasonName(testResultFailure.getFailureReasonName());
    }
}
