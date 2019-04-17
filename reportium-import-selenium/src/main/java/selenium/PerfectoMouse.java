package selenium;

import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Mouse;

public class PerfectoMouse implements Mouse {

    private Mouse mouse;
    private PerfectoWebDriverEventListener listener;

    public PerfectoMouse(Mouse mouse, PerfectoWebDriverEventListener listener) {
        this.mouse = mouse;
        this.listener = listener;
    }

    @Override
    public void click(Coordinates where) {
        listener.beforeClick(where);
        mouse.click(where);
        listener.afterClick(where);
    }

    @Override
    public void doubleClick(Coordinates where) {
        listener.beforeDoubleClick(where);
        mouse.doubleClick(where);
        listener.afterDoubleClick(where);
    }

    @Override
    public void mouseDown(Coordinates where) {
        listener.beforeMouseDown();
        mouse.mouseDown(where);
        listener.afterMouseDown();
    }

    @Override
    public void mouseUp(Coordinates where) {
        listener.beforeMouseUp(where);
        mouse.mouseUp(where);
        listener.afterMouseUp(where);
    }

    @Override
    public void mouseMove(Coordinates where) {
        listener.beforeMouseMove(where);
        mouse.mouseMove(where);
        listener.afterMouseMove(where);
    }

    @Override
    public void mouseMove(Coordinates where, long xOffset, long yOffset) {
        listener.beforeMouseMove(where, xOffset, yOffset);
        mouse.mouseMove(where, xOffset, yOffset);
        listener.afterMouseMove(where, xOffset, yOffset);
    }

    @Override
    public void contextClick(Coordinates where) {
        listener.beforeContextClick(where);
        mouse.contextClick(where);
        listener.afterContextClick(where);
    }
}
