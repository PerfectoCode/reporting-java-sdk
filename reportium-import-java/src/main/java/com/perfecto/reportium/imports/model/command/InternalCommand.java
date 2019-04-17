package com.perfecto.reportium.imports.model.command;

import java.util.List;

public class InternalCommand {
    private String name;
    private CommandStatus status;
    private String message;
    private long startTime;
    private long endTime;
    private List<CommandParameter> parameters;
    private CommandType commandType;
    private List<String> screenshots;

    public InternalCommand(Command command) {
        this.name = command.getName();
        this.status = command.getStatus();
        this.message = command.getMessage();
        this.startTime = command.getStartTime();
        this.endTime = command.getEndTime();
        this.parameters = command.getParameters();
        this.commandType = command.getCommandType();
    }

    public String getName() {
        return name;
    }

    public CommandStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public List<CommandParameter> getParameters() {
        return parameters;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }
}
