package com.perfecto.reportium.imports.model.event;

public class TestEvent {
    private EventType eventType;
    private String externalId;
    private String testId;
    private long order;

    public TestEvent(EventType eventType, String externalId, String testId, long order) {
        this.eventType = eventType;
        this.externalId = externalId;
        this.testId = testId;
        this.order = order;
    }

    public TestEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }
}
