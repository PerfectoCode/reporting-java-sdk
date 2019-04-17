package selenium;

import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.support.events.WebDriverEventListener;

public interface PerfectoWebDriverEventListener extends WebDriverEventListener {

    // keyboard

    void beforeSendKeys(CharSequence... charSequences);

    void afterSendKeys(CharSequence... charSequences);

    void beforePressKey(CharSequence charSequence);

    void afterPressKey(CharSequence charSequence);

    void beforeReleaseKey(CharSequence charSequence);

    void afterReleaseKey(CharSequence charSequence);

    // mouse

    void beforeClick(Coordinates where);

    void afterClick(Coordinates where);

    void beforeDoubleClick(Coordinates where);

    void afterDoubleClick(Coordinates where);

    void beforeMouseDown();

    void afterMouseDown();

    void beforeMouseUp(Coordinates where);

    void afterMouseUp(Coordinates where);

    void beforeMouseMove(Coordinates where);

    void afterMouseMove(Coordinates where);

    void beforeMouseMove(Coordinates where, long xOffset, long yOffset);

    void afterMouseMove(Coordinates where, long xOffset, long yOffset);

    void beforeContextClick(Coordinates where);

    void afterContextClick(Coordinates where);

    void onQuit();
}
