package com.perfecto.reportium.imports.model.command;

import com.perfecto.reportium.imports.model.attachment.ScreenshotAttachment;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class CommandTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void withNoName() {
        new Command.Builder().withStatus(CommandStatus.SUCCESS).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void withEmptyName() {
        new Command.Builder().withName("").withStatus(CommandStatus.SUCCESS).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void withNoStatus() {
        new Command.Builder().withName("slim shady").withStatus(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void withNoCommandType() {
        new Command.Builder().withName("slim shady").withCommandType(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void withNegativeStartTime() {
        new Command.Builder().withName("slim shady").withStartTime(-1).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void withNegativeEndTime() {
        new Command.Builder().withName("slim shady").withEndTime(-1).build();
    }

    @Test
    public void withNullScreenshot() {
        ScreenshotAttachment screenshot = null;
        Command command = new Command.Builder().withName("xxx").withScreenshotAttachments(screenshot).build();
        assertEquals(0, command.getScreenshots().size());
    }

    @Test
    public void withNullScreenshotsVarargs() {
        Command command = new Command.Builder().withName("xxx").withScreenshotAttachments((ScreenshotAttachment[]) null).build();
        assertEquals(0, command.getScreenshots().size());
    }

    @Test
    public void withEmptyScreenshotsCollection() {
        Command command = new Command.Builder().withName("xxx").withScreenshotAttachments(Collections.<ScreenshotAttachment>emptyList()).build();
        assertEquals(0, command.getScreenshots().size());
    }

    @Test
    public void withNullScreenshotsCollection() {
        List<ScreenshotAttachment> screenshots = null;
        Command command = new Command.Builder().withName("xxx").withScreenshotAttachments(screenshots).build();
        assertEquals(0, command.getScreenshots().size());
    }

    @Test
    public void withNullParameter() {
        CommandParameter parameter = null;
        Command command = new Command.Builder().withName("xxx").withParameters(parameter).build();
        assertEquals(0, command.getParameters().size());
    }

    @Test
    public void withNullParametersVarargs() {
        Command command = new Command.Builder().withName("xxx").withParameters((CommandParameter[]) null).build();
        assertEquals(0, command.getParameters().size());
    }

    @Test
    public void withEmptyParametersCollection() {
        Command command = new Command.Builder().withName("xxx").withParameters(Collections.<CommandParameter>emptyList()).build();
        assertEquals(0, command.getParameters().size());
    }

    @Test
    public void withNullParametersCollection() {
        List<CommandParameter> parameters = null;
        Command command = new Command.Builder().withName("xxx").withParameters(parameters).build();
        assertEquals(0, command.getParameters().size());
    }

    @Test
    public void addParameter_addsSingleParameter() {
        CommandParameter parameter = new CommandParameter("name", "value");
        Command command = new Command.Builder().withName("xxx").addParameter(parameter).build();
        assertEquals(command.getParameters(), Arrays.asList(parameter));
    }

    @Test
    public void addScreenshotAttachment_addsSingleScreenshot() throws Exception {
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test", ".jpg");
        try {
            ScreenshotAttachment screenshot = new ScreenshotAttachment.Builder().withAbsolutePath(tempFile.toString()).build();
            Command command = new Command.Builder().withName("xxx").addScreenshotAttachment(screenshot).build();
            assertEquals(command.getScreenshots(), Arrays.asList(screenshot));
        } finally {
            java.nio.file.Files.delete(tempFile);
        }
    }

    @Test
    public void normalizeCommand_bothTimesZero_setToCurrentTime() {
        Command command = new Command.Builder().withName("xxx").build();
        assertTrue(command.getStartTime() > 0);
        assertEquals(command.getStartTime(), command.getEndTime());
    }

    @Test
    public void normalizeCommand_onlyStartTimeSet_endTimeCopiesStart() {
        Command command = new Command.Builder().withName("xxx").withStartTime(100).build();
        assertEquals(command.getStartTime(), 100);
        assertEquals(command.getEndTime(), 100);
    }

    @Test
    public void normalizeCommand_onlyEndTimeSet_startTimeCopiesEnd() {
        Command command = new Command.Builder().withName("xxx").withEndTime(200).build();
        assertEquals(command.getStartTime(), 200);
        assertEquals(command.getEndTime(), 200);
    }

    @Test
    public void normalizeCommand_bothTimesSet_notModified() {
        Command command = new Command.Builder().withName("xxx").withStartTime(100).withEndTime(200).build();
        assertEquals(command.getStartTime(), 100);
        assertEquals(command.getEndTime(), 200);
    }

    @Test
    public void build_allFieldsAccessible() {
        Command command = new Command.Builder()
                .withName("cmd")
                .withStatus(CommandStatus.FAILURE)
                .withMessage("msg")
                .withStartTime(1)
                .withEndTime(2)
                .withCommandType(CommandType.API)
                .build();

        assertEquals(command.getName(), "cmd");
        assertEquals(command.getStatus(), CommandStatus.FAILURE);
        assertEquals(command.getMessage(), "msg");
        assertEquals(command.getCommandType(), CommandType.API);
        assertNotNull(command.getParameters());
        assertNotNull(command.getScreenshots());
    }
}
