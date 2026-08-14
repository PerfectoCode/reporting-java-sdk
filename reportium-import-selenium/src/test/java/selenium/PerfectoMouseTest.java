package selenium;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Mouse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Test case for {@link PerfectoMouse}.
 * <p>
 * PerfectoMouse is a thin delegate: every method notifies the {@link PerfectoWebDriverEventListener}
 * before and after delegating to the wrapped {@link Mouse}. These tests verify the call order/arguments
 * and that exceptions from the wrapped mouse propagate (without firing the "after" notification).
 */
public class PerfectoMouseTest {

    private IMocksControl mocksControl;
    private Mouse mouseMock;
    private PerfectoWebDriverEventListener listenerMock;
    private Coordinates coordinatesMock;
    private PerfectoMouse tested;

    @BeforeMethod
    public void beforeMethod() {
        mocksControl = EasyMock.createControl();
        mouseMock = mocksControl.createMock(Mouse.class);
        listenerMock = mocksControl.createMock(PerfectoWebDriverEventListener.class);
        coordinatesMock = mocksControl.createMock(Coordinates.class);
        tested = new PerfectoMouse(mouseMock, listenerMock);
    }

    @Test
    public void click_delegatesAndNotifiesListener() {
        listenerMock.beforeClick(coordinatesMock);
        mouseMock.click(coordinatesMock);
        listenerMock.afterClick(coordinatesMock);
        mocksControl.replay();

        tested.click(coordinatesMock);

        mocksControl.verify();
    }

    @Test
    public void click_propagatesExceptionFromWrappedMouse() {
        WebDriverException exception = new WebDriverException("boom");

        listenerMock.beforeClick(coordinatesMock);
        mouseMock.click(coordinatesMock);
        EasyMock.expectLastCall().andThrow(exception);
        mocksControl.replay();

        try {
            tested.click(coordinatesMock);
            fail("Expected exception to propagate");
        } catch (WebDriverException e) {
            assertEquals(e, exception);
        }

        mocksControl.verify();
    }

    @Test
    public void doubleClick_delegatesAndNotifiesListener() {
        listenerMock.beforeDoubleClick(coordinatesMock);
        mouseMock.doubleClick(coordinatesMock);
        listenerMock.afterDoubleClick(coordinatesMock);
        mocksControl.replay();

        tested.doubleClick(coordinatesMock);

        mocksControl.verify();
    }

    @Test
    public void mouseDown_delegatesAndNotifiesListener() {
        listenerMock.beforeMouseDown();
        mouseMock.mouseDown(coordinatesMock);
        listenerMock.afterMouseDown();
        mocksControl.replay();

        tested.mouseDown(coordinatesMock);

        mocksControl.verify();
    }

    @Test
    public void mouseUp_delegatesAndNotifiesListener() {
        listenerMock.beforeMouseUp(coordinatesMock);
        mouseMock.mouseUp(coordinatesMock);
        listenerMock.afterMouseUp(coordinatesMock);
        mocksControl.replay();

        tested.mouseUp(coordinatesMock);

        mocksControl.verify();
    }

    @Test
    public void mouseMove_delegatesAndNotifiesListener() {
        listenerMock.beforeMouseMove(coordinatesMock);
        mouseMock.mouseMove(coordinatesMock);
        listenerMock.afterMouseMove(coordinatesMock);
        mocksControl.replay();

        tested.mouseMove(coordinatesMock);

        mocksControl.verify();
    }

    @Test
    public void mouseMove_withOffsets_delegatesAndNotifiesListener() {
        listenerMock.beforeMouseMove(coordinatesMock, 5L, 10L);
        mouseMock.mouseMove(coordinatesMock, 5L, 10L);
        listenerMock.afterMouseMove(coordinatesMock, 5L, 10L);
        mocksControl.replay();

        tested.mouseMove(coordinatesMock, 5L, 10L);

        mocksControl.verify();
    }

    @Test
    public void mouseMove_withOffsets_propagatesExceptionFromWrappedMouse() {
        WebDriverException exception = new WebDriverException("boom");

        listenerMock.beforeMouseMove(coordinatesMock, 5L, 10L);
        mouseMock.mouseMove(coordinatesMock, 5L, 10L);
        EasyMock.expectLastCall().andThrow(exception);
        mocksControl.replay();

        try {
            tested.mouseMove(coordinatesMock, 5L, 10L);
            fail("Expected exception to propagate");
        } catch (WebDriverException e) {
            assertEquals(e, exception);
        }

        mocksControl.verify();
    }

    @Test
    public void contextClick_delegatesAndNotifiesListener() {
        listenerMock.beforeContextClick(coordinatesMock);
        mouseMock.contextClick(coordinatesMock);
        listenerMock.afterContextClick(coordinatesMock);
        mocksControl.replay();

        tested.contextClick(coordinatesMock);

        mocksControl.verify();
    }
}
