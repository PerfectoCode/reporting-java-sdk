package com.perfecto.reportium.imports.model.command;

import com.perfecto.reportium.imports.model.attachment.ScreenshotAttachment;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class CommandTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void withNoName() {
        new Command.Builder().withStatus(CommandStatus.SUCCESS).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void withNoStatus() {
        new Command.Builder().withName("slim shady").withStatus(null).build();
    }

    @Test
    public void withNullScreenshot() {
        ScreenshotAttachment screenshot = null;
        Command command = new Command.Builder().withName("xxx").withScreenshotAttachments(screenshot).build();
        assertEquals(0, command.getScreenshots().size());
    }

    @Test
    public void withNullParameter() {
        CommandParameter parameter = null;
        Command command = new Command.Builder().withName("xxx").withParameters(parameter).build();
        assertEquals(0, command.getParameters().size());
    }
}
