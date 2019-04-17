package selenium;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import com.perfecto.reportium.imports.model.attachment.ScreenshotAttachment;
import com.perfecto.reportium.imports.model.command.Command;
import com.perfecto.reportium.imports.model.command.CommandParameter;
import com.perfecto.reportium.imports.model.command.CommandStatus;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.remote.RemoteWebElement;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PerfectoWebDriverEventListenerImpl implements PerfectoWebDriverEventListener {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final ReportiumImportClient reportiumImportClient;
    private ThreadLocal<Command.Builder> commandBuilder = new ThreadLocal<>();

    public PerfectoWebDriverEventListenerImpl(ReportiumImportClient reportiumImportClient) {
        this.reportiumImportClient = reportiumImportClient;
    }

    @Override
    public void beforeAlertAccept(WebDriver driver) {
        before("AlertAccept");
    }

    @Override
    public void afterAlertAccept(WebDriver driver) {
        after();
    }

    @Override
    public void beforeAlertDismiss(WebDriver driver) {
        before("AlertDismiss");
    }

    @Override
    public void afterAlertDismiss(WebDriver driver) {
        after();
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        before("NavigateTo");
        this.commandBuilder.get().addParameter(new CommandParameter("url", url));
    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {
        after();
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        before("NavigateBack");
    }

    @Override
    public void afterNavigateBack(WebDriver driver) {
        after();
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        before("NavigateForward");
    }

    @Override
    public void afterNavigateForward(WebDriver driver) {
        after();
    }

    @Override
    public void beforeNavigateRefresh(WebDriver driver) {
        before("NavigateRefresh");
    }

    @Override
    public void afterNavigateRefresh(WebDriver driver) {
        after();
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
        before("FindBy");
        this.commandBuilder.get().addParameter(new CommandParameter("by", by.toString()));
    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        after();
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        before("ClickOn");
        if (element != null) {
            this.commandBuilder.get().addParameter(new CommandParameter("element", element.toString()));
        }
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        after();
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        before("ChangeValueOf");
        if (element != null) {
            this.commandBuilder.get().addParameter(new CommandParameter("element", element.toString()));
        }
        if (keysToSend != null && keysToSend.length > 0) {
            this.commandBuilder.get().addParameter(new CommandParameter("keys", StringUtils.join(keysToSend, ", ")));
        }
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        after();
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        before("Script");
        this.commandBuilder.get().addParameter(new CommandParameter("script", script));
    }

    @Override
    public void afterScript(String script, WebDriver driver) {
        after();
    }

    @Override
    public void beforeSwitchToWindow(String windowName, WebDriver webDriver) {
        before("SwitchToWindow");
        this.commandBuilder.get().addParameter(new CommandParameter("windowName", windowName));
    }

    @Override
    public void afterSwitchToWindow(String windowName, WebDriver webDriver) {
        after();
    }

    // keyboard

    public void beforeSendKeys(CharSequence... charSequences) {
        before("SendKeys");
        if (charSequences != null && charSequences.length > 0) {
            this.commandBuilder.get().addParameter(new CommandParameter("keys", StringUtils.join(charSequences, ", ")));
        }
    }

    public void afterSendKeys(CharSequence... charSequences) {
        after();
    }

    public void beforePressKey(CharSequence charSequence) {
        before("PressKey");
        if (charSequence != null) {
            this.commandBuilder.get().addParameter(new CommandParameter("key", charSequence.toString()));
        }
    }

    public void afterPressKey(CharSequence charSequence) {
        after();
    }

    public void beforeReleaseKey(CharSequence charSequence) {
        before("ReleaseKey");
        if (charSequence != null) {
            this.commandBuilder.get().addParameter(new CommandParameter("key", charSequence.toString()));
        }
    }

    public void afterReleaseKey(CharSequence charSequence) {
        after();
    }

    @Override
    public void beforeGetText(WebElement webElement, WebDriver webDriver) {
        before("GetText");
    }

    @Override
    public void afterGetText(WebElement webElement, WebDriver webDriver, String text) {
        if (text != null) {
            this.commandBuilder.get().addParameter(new CommandParameter("text", text));
        }
        after();
    }

    // mouse
    @Override
    public void beforeClick(Coordinates where) {
        before("Click");
        addCoordinatesParameter(where);
    }

    @Override
    public void afterClick(Coordinates where) {
        after();
    }

    @Override
    public void beforeDoubleClick(Coordinates where) {
        before("DoubleClick");
        addCoordinatesParameter(where);
    }

    @Override
    public void afterDoubleClick(Coordinates where) {
        after();
    }

    @Override
    public void beforeMouseDown() {
        before("MouseDown");
    }

    @Override
    public void afterMouseDown() {
        after();
    }

    @Override
    public void beforeMouseUp(Coordinates where) {
        before("MouseUp");
        addCoordinatesParameter(where);
    }

    @Override
    public void afterMouseUp(Coordinates where) {
        after();
    }

    @Override
    public void beforeMouseMove(Coordinates where) {
        before("MouseMove");
        addCoordinatesParameter(where);
    }

    @Override
    public void afterMouseMove(Coordinates where) {
        after();
    }

    @Override
    public void beforeMouseMove(Coordinates where, long xOffset, long yOffset) {
        before("MouseMove");
        addCoordinatesParameter(where);
        this.commandBuilder.get().addParameter(new CommandParameter("xOffset", Long.toString(xOffset)));
        this.commandBuilder.get().addParameter(new CommandParameter("yOffset", Long.toString(yOffset)));
    }

    @Override
    public void afterMouseMove(Coordinates where, long xOffset, long yOffset) {
        after();
    }

    @Override
    public void beforeContextClick(Coordinates where) {
        before("ContextClick");
        addCoordinatesParameter(where);
    }

    @Override
    public void afterContextClick(Coordinates where) {
        after();
    }

    // screenshot

    @Override
    public <X> void beforeGetScreenshotAs(OutputType<X> target) {
        before("Screenshot");
    }

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {
        Command.Builder builder = this.commandBuilder.get();
        if (builder == null) {
            return;
        }

        builder.withEndTime(System.currentTimeMillis());
        if (screenshot instanceof File) {
            File file = (File) screenshot;
            builder.withScreenshotAttachments(new ScreenshotAttachment.Builder()
                    .withAbsolutePath(file.getAbsolutePath())
                    .build());
        } else if (screenshot instanceof byte[]) {
            byte[] bytes = (byte[]) screenshot;
            addByteArrayAttachment(bytes);
        } else if (screenshot instanceof String) {
            String base64Png = (String) screenshot;
            byte[] bytes = Base64.decodeBase64(base64Png);
            addByteArrayAttachment(bytes);
        }
        after();
    }

    private void addByteArrayAttachment(byte[] bytes) {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            this.commandBuilder.get().withScreenshotAttachments(new ScreenshotAttachment.Builder()
                    .withInputStream(inputStream)
                    .withExtension("png")
                    .build());
        } catch (IOException e) {
            String msg = "Failed to read screenshot. Reason: " + e.getMessage();
            logger.log(Level.SEVERE, msg, e);
            if (reportiumImportClient.getFailOnUploadFailure()) {
                throw new RuntimeException(msg, e);
            }
        }
    }

    @Override
    public void onException(Throwable throwable, WebDriver driver) {
        Command.Builder commandBuilder = this.commandBuilder.get();
        if (commandBuilder != null) {
            commandBuilder.withMessage(throwable.getMessage());
            after(CommandStatus.FAILURE);
        }
    }

    @Override
    public void onQuit() {
        reportiumImportClient.quit();
    }

    private void addCoordinatesParameter(Coordinates where) {
        if (where != null) {
            try {
                Object enclosingObject = ReflectionTestUtils.getField(where, "this$0");
                if (enclosingObject instanceof RemoteWebElement) {
                    this.commandBuilder.get().addParameter(new CommandParameter("coordinates", enclosingObject.toString()));
                }
            } catch (Throwable t) {

                // Just for protection. If there was exception, the command will be without parameter.
                logger.log(Level.WARNING, "Unable to extract parameter from coordinates", t);
            }
        }
    }

    private void before(String commandName) {
        this.commandBuilder.set(new Command.Builder().withName(commandName).withStartTime(System.currentTimeMillis()));
    }

    private void after() {
        after(CommandStatus.SUCCESS);
    }

    private void after(CommandStatus commandStatus) {
        Command.Builder builder = this.commandBuilder.get();
        if (builder == null) {
            return;
        }

        this.commandBuilder.remove();

        builder.withEndTime(System.currentTimeMillis()).withStatus(commandStatus);
        final Command command = builder.build();
        reportiumImportClient.command(command);
    }
}
