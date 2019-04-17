package selenium;

import com.perfecto.reportium.imports.client.ReportiumImportClient;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.Map;

public class PerfectoEventFiringWebDriver extends EventFiringWebDriver {
    private PerfectoWebDriverEventListener perfectoWebDriverListener;

    public PerfectoEventFiringWebDriver(WebDriver driver) {
        super(driver);
    }

    public PerfectoEventFiringWebDriver(WebDriver driver, ReportiumImportClient reportiumImportClient) {
        this(driver);

        if (driver instanceof RemoteWebDriver) {
            RemoteWebDriver remoteWebDriver = (RemoteWebDriver) driver;
            final Map<String, ?> capabilities = remoteWebDriver.getCapabilities().asMap();

            reportiumImportClient.getExecutionContext().updatePlatforms(capabilities);
        }
        this.perfectoWebDriverListener = new PerfectoWebDriverEventListenerImpl(reportiumImportClient);
        register(perfectoWebDriverListener);
    }

    @Override
    public Keyboard getKeyboard() {
        Keyboard keyboard = super.getKeyboard();
        PerfectoKeyboard perfectoKeyboard = new PerfectoKeyboard(keyboard, perfectoWebDriverListener);
        return perfectoKeyboard;
    }

    @Override
    public Mouse getMouse() {
        Mouse mouse = super.getMouse();
        PerfectoMouse perfectoMouse = new PerfectoMouse(mouse, perfectoWebDriverListener);
        return perfectoMouse;
    }



    @Override
    public void quit() {
        this.perfectoWebDriverListener.onQuit();
        super.quit();
    }
}
