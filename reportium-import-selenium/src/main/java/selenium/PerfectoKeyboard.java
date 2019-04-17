package selenium;

import org.openqa.selenium.interactions.Keyboard;

public class PerfectoKeyboard implements Keyboard {

    private Keyboard keyboard;
    private PerfectoWebDriverEventListener listener;

    public PerfectoKeyboard(Keyboard keyboard, PerfectoWebDriverEventListener listener) {
        this.keyboard = keyboard;
        this.listener = listener;
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        listener.beforeSendKeys(charSequences);
        keyboard.sendKeys(charSequences);
        listener.afterSendKeys(charSequences);
    }

    @Override
    public void pressKey(CharSequence charSequence) {
        listener.beforePressKey(charSequence);
        keyboard.pressKey(charSequence);
        listener.afterPressKey(charSequence);
    }

    @Override
    public void releaseKey(CharSequence charSequence) {
        listener.beforeReleaseKey(charSequence);
        keyboard.releaseKey(charSequence);
        listener.afterReleaseKey(charSequence);
    }
}
