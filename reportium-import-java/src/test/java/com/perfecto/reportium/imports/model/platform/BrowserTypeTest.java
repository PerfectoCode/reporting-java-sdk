package com.perfecto.reportium.imports.model.platform;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

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
}
