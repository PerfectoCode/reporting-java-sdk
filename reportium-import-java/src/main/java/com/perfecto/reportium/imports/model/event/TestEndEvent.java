package com.perfecto.reportium.imports.model.event;

import com.perfecto.reportium.imports.model.attachment.ArtifactData;
import com.perfecto.reportium.model.CustomField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestEndEvent extends TestEvent {
    private TestExecutionStatus status;
    private String message;
    private long endTime;
    private String testStartEventId;
    private int eventsCount;
    private List<ArtifactData> artifacts;
    private String failureReasonName;
    private Set<String> tags;
    private Set<CustomField> customFields;

    public TestEndEvent() {
        super(EventType.TEST_END);
        artifacts = new ArrayList<>();
        tags = new HashSet<>();
        customFields = new HashSet<>();
    }

    public TestEndEvent(String externalId, String testId, long order) {
        this();
        setExternalId(externalId);
        setTestId(testId);
        setOrder(order);
    }

    public TestExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(TestExecutionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTestStartEventId() {
        return testStartEventId;
    }

    public void setTestStartEventId(String testStartEventId) {
        this.testStartEventId = testStartEventId;
    }

    public int getEventsCount() {
        return eventsCount;
    }

    public void setEventsCount(int eventsCount) {
        this.eventsCount = eventsCount;
    }

    public List<ArtifactData> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<ArtifactData> artifacts) {
        this.artifacts = artifacts;
    }

    public String getFailureReasonName() {
        return failureReasonName;
    }

    public void setFailureReasonName(String failureReasonName) {
        this.failureReasonName = failureReasonName;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<CustomField> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Set<CustomField> customFields) {
        this.customFields = customFields;
    }
}
