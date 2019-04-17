package com.perfecto.reportium.imports.model.event;

import com.perfecto.reportium.imports.model.command.InternalCommand;

public class CommandEvent extends TestEvent {
    private InternalCommand command;

    public CommandEvent() {
        super(EventType.COMMAND_EVENT);
    }

    public CommandEvent(String externalId, String testId, long order) {
        this();
        setExternalId(externalId);
        setTestId(testId);
        setOrder(order);
    }

    public InternalCommand getCommand() {
        return command;
    }

    public void setCommand(InternalCommand command) {
        this.command = command;
    }
}
