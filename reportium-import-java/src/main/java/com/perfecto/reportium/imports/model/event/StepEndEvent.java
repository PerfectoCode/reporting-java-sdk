package com.perfecto.reportium.imports.model.event;

public class StepEndEvent extends TestEvent {
    private String message;
    private long endTime;

    public StepEndEvent() {
        super(EventType.TEST_END);
    }

    public StepEndEvent(String externalId, String testId, long order) {
        this();
        setExternalId(externalId);
        setTestId(testId);
        setOrder(order);
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
}
