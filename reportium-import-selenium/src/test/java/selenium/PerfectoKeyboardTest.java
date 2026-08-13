package selenium;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Keyboard;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Test case for {@link PerfectoKeyboard}.
 * <p>
 * PerfectoKeyboard is a thin delegate: every method notifies the {@link PerfectoWebDriverEventListener}
 * before and after delegating to the wrapped {@link Keyboard}. These tests verify the call order and that
 * both the return path and the exception path propagate correctly.
 */
public class PerfectoKeyboardTest {

    private IMocksControl mocksControl;
    private Keyboard keyboardMock;
    private PerfectoWebDriverEventListener listenerMock;
    private PerfectoKeyboard tested;

    @BeforeMethod
    public void beforeMethod() {
        mocksControl = EasyMock.createControl();
        keyboardMock = mocksControl.createMock(Keyboard.class);
        listenerMock = mocksControl.createMock(PerfectoWebDriverEventListener.class);
        tested = new PerfectoKeyboard(keyboardMock, listenerMock);
    }

    @Test
    public void sendKeys_delegatesAndNotifiesListener() {
        CharSequence[] keys = new CharSequence[]{"a", "b"};

        listenerMock.beforeSendKeys(keys);
        keyboardMock.sendKeys(keys);
        listenerMock.afterSendKeys(keys);
        mocksControl.replay();

        tested.sendKeys(keys);

        mocksControl.verify();
    }

    @Test
    public void sendKeys_propagatesExceptionFromWrappedKeyboard() {
        CharSequence[] keys = new CharSequence[]{"a"};
        WebDriverException exception = new WebDriverException("boom");

        listenerMock.beforeSendKeys(keys);
        keyboardMock.sendKeys(keys);
        EasyMock.expectLastCall().andThrow(exception);
        mocksControl.replay();

        try {
            tested.sendKeys(keys);
            fail("Expected exception to propagate");
        } catch (WebDriverException e) {
            assertEquals(e, exception);
        }

        // afterSendKeys must not be called since the wrapped keyboard threw
        mocksControl.verify();
    }

    @Test
    public void pressKey_delegatesAndNotifiesListener() {
        listenerMock.beforePressKey("a");
        keyboardMock.pressKey("a");
        listenerMock.afterPressKey("a");
        mocksControl.replay();

        tested.pressKey("a");

        mocksControl.verify();
    }

    @Test
    public void pressKey_propagatesExceptionFromWrappedKeyboard() {
        WebDriverException exception = new WebDriverException("boom");

        listenerMock.beforePressKey("a");
        keyboardMock.pressKey("a");
        EasyMock.expectLastCall().andThrow(exception);
        mocksControl.replay();

        try {
            tested.pressKey("a");
            fail("Expected exception to propagate");
        } catch (WebDriverException e) {
            assertEquals(e, exception);
        }

        mocksControl.verify();
    }

    @Test
    public void releaseKey_delegatesAndNotifiesListener() {
        listenerMock.beforeReleaseKey("a");
        keyboardMock.releaseKey("a");
        listenerMock.afterReleaseKey("a");
        mocksControl.replay();

        tested.releaseKey("a");

        mocksControl.verify();
    }

    @Test
    public void releaseKey_propagatesExceptionFromWrappedKeyboard() {
        WebDriverException exception = new WebDriverException("boom");

        listenerMock.beforeReleaseKey("a");
        keyboardMock.releaseKey("a");
        EasyMock.expectLastCall().andThrow(exception);
        mocksControl.replay();

        try {
            tested.releaseKey("a");
            fail("Expected exception to propagate");
        } catch (WebDriverException e) {
            assertEquals(e, exception);
        }

        mocksControl.verify();
    }
}
