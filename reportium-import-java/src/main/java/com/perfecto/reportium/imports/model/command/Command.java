package com.perfecto.reportium.imports.model.command;


import com.perfecto.reportium.imports.model.attachment.ScreenshotAttachment;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Command {
    private String name;
    private CommandStatus status;
    private String message;
    private long startTime;
    private long endTime;
    private List<CommandParameter> parameters;
    private List<ScreenshotAttachment> screenshots;
    private CommandType commandType;

    private Command(Builder builder) {
        name = builder.name;
        status = builder.status;
        message = builder.message;
        startTime = builder.startTime;
        endTime = builder.endTime;
        parameters = Collections.unmodifiableList(builder.parameters);
        commandType = builder.commandType;
        screenshots = Collections.unmodifiableList(builder.screenshots);
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

    public List<ScreenshotAttachment> getScreenshots() {
        return screenshots;
    }

    public static final class Builder {
        private String name;
        private CommandStatus status = CommandStatus.SUCCESS;
        private String message;
        private long startTime;
        private long endTime;
        private List<CommandParameter> parameters = new ArrayList<>();
        private CommandType commandType = CommandType.DEFAULT;
        private List<ScreenshotAttachment> screenshots = new ArrayList<>();

        public Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStatus(CommandStatus status) {
            this.status = status;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withStartTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withEndTime(long endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder addParameter(CommandParameter parameter) {
            return withParameters(parameter);
        }

        public Builder withParameters(CommandParameter... parameters) {
            if (parameters != null) {
                withParameters(Arrays.asList(parameters));
            }
            return this;
        }

        public Builder withParameters(Collection<CommandParameter> parameters) {
            if (parameters != null && !parameters.isEmpty()) {
                for (CommandParameter parameter : parameters) {
                    if (parameter != null) {
                        this.parameters.add(parameter);
                    }
                }
            }
            return this;
        }

        public Builder withCommandType(CommandType commandType) {
            this.commandType = commandType;
            return this;
        }

        public Builder addScreenshotAttachment(ScreenshotAttachment screenshot) {
            return withScreenshotAttachments(screenshot);
        }

        public Builder withScreenshotAttachments(ScreenshotAttachment... screenshots) {
            if (screenshots != null) {
                withScreenshotAttachments(Arrays.asList(screenshots));
            }
            return this;
        }

        public Builder withScreenshotAttachments(Collection<ScreenshotAttachment> screenshots) {
            if (screenshots != null && !screenshots.isEmpty()) {
                for (ScreenshotAttachment screenshot : screenshots) {
                    if (screenshot != null) {
                        this.screenshots.add(screenshot);
                    }
                }
            }
            return this;
        }

        public Command build() {
            validateCommand();
            normalizeCommand();

            return new Command(this);
        }

        private void validateCommand() {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Cannot build Command. 'name' cannot be empty or null");
            }

            if (status == null) {
                throw new IllegalArgumentException("Cannot build Command. 'status' cannot be null");
            }

            if (commandType == null) {
                throw new IllegalArgumentException("Cannot build Command. 'commandType' cannot be null");
            }

            if (startTime < 0) {
                throw new IllegalArgumentException("Cannot build Command. 'startTime' cannot be negative: " + startTime);
            }

            if (endTime < 0) {
                throw new IllegalArgumentException("Cannot build Command. 'endTime' cannot be negative: " + endTime);
            }
        }

        private void normalizeCommand() {
            long currentTime = System.currentTimeMillis();
            if (startTime == 0 && endTime == 0) {
                startTime = currentTime;
                endTime = currentTime;
            } else if (startTime == 0) {
                startTime = endTime;
            } else if (endTime == 0) {
                endTime = startTime;
            }
        }
    }
}
