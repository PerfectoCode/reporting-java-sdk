package com.perfecto.reportium.imports.model.event;

public class StepStartEvent extends TestEvent {
    private String description;
    private long startTime;

    public StepStartEvent() {
        super(EventType.STEP_START);
    }

    public StepStartEvent(String externalId, String testId, long order) {
        this();
        setExternalId(externalId);
        setTestId(testId);
        setOrder(order);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
