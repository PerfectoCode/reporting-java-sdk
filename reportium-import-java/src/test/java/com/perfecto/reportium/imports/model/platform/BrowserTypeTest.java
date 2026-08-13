package com.perfecto.reportium.imports.model.platform;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class BrowserTypeTest {

    @Test
    public void testInternetExplorer() {
        BrowserType browserType;

        browserType = BrowserType.getByName("internet explorer");
        assertEquals(BrowserType.INTERNET_EXPLORER, browserType);

        browserType = BrowserType.getByName("internet_explorer");
        assertEquals(BrowserType.INTERNET_EXPLORER, browserType);

        browserType = BrowserType.getByName("INTERNET EXPLORER");
        assertEquals(BrowserType.INTERNET_EXPLORER, browserType);

        browserType = BrowserType.getByName("INTERNET_EXPLORER");
        assertEquals(BrowserType.INTERNET_EXPLORER, browserType);
    }

    @Test
    public void getByName_null_returnsNull() {
        assertNull(BrowserType.getByName(null));
    }

    @Test
    public void getByName_unknownName_returnsNull() {
        assertNull(BrowserType.getByName("some unknown browser"));
    }

    @Test
    public void getByName_chrome() {
        assertEquals(BrowserType.getByName("chrome"), BrowserType.CHROME);
    }

    @Test
    public void getByName_firefox() {
        assertEquals(BrowserType.getByName("firefox"), BrowserType.FIREFOX);
    }

    @Test
    public void getByName_safari() {
        assertEquals(BrowserType.getByName("safari"), BrowserType.SAFARI);
    }

    @Test
    public void getByName_microsoftEdge() {
        assertEquals(BrowserType.getByName("microsoft edge"), BrowserType.MICROSOFT_EDGE);
    }
}
