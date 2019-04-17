package com.perfecto.reportium.imports.model.event;

import com.perfecto.reportium.imports.model.platform.Platform;
import com.perfecto.reportium.model.CustomField;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.Project;

import java.util.List;
import java.util.Set;

public class TestStartEvent extends TestEvent {

    private static final String REPORTIUM_SDK_ENGINE = "reportium_sdk";

    private String name;
    private long startTime;
    private List<Platform> platforms;
    private Job job;
    private Set<String> tags;
    private Set<CustomField> customFields;
    private Project project;
    private ExecutionEngine executionEngine;
    private String automationFramework;

    public TestStartEvent() {
        super(EventType.TEST_START);
    }

    public TestStartEvent(String externalId, String testId, long order, String sdkVersion) {
        this();
        setExternalId(externalId);
        setTestId(testId);
        setOrder(order);
        setExecutionEngine(new ExecutionEngine(REPORTIUM_SDK_ENGINE, sdkVersion, null));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ExecutionEngine getExecutionEngine() {
        return executionEngine;
    }

    public void setExecutionEngine(ExecutionEngine executionEngine) {
        this.executionEngine = executionEngine;
    }

    public String getAutomationFramework() {
        return automationFramework;
    }

    public void setAutomationFramework(String automationFramework) {
        this.automationFramework = automationFramework;
    }
}
