package com.perfecto.reportium.imports.model.platform;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class PlatformTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void build_withBothMobileAndBrowserInfo_throws() {
        new Platform.Builder()
                .withMobileInfo(new MobileInfo.Builder().withModel("model").build())
                .withBrowserInfo(new BrowserInfo.Builder().withBrowserType(BrowserType.CHROME).build())
                .build();
    }

    @Test
    public void build_withMobileInfo_normalizesDeviceTypeToMobile() {
        MobileInfo mobileInfo = new MobileInfo.Builder().withModel("iPhone").build();
        Platform platform = new Platform.Builder()
                .withMobileInfo(mobileInfo)
                .build();

        assertEquals(platform.getDeviceType(), DeviceType.MOBILE);
        assertEquals(platform.getMobileInfo(), mobileInfo);
        assertNull(platform.getBrowserInfo());
    }

    @Test
    public void build_withBrowserInfo_normalizesDeviceTypeToDesktop() {
        BrowserInfo browserInfo = new BrowserInfo.Builder().withBrowserType(BrowserType.CHROME).build();
        Platform platform = new Platform.Builder()
                .withBrowserInfo(browserInfo)
                .build();

        assertEquals(platform.getDeviceType(), DeviceType.DESKTOP);
        assertEquals(platform.getBrowserInfo(), browserInfo);
        assertNull(platform.getMobileInfo());
    }

    @Test
    public void build_withExplicitDeviceType_doesNotNormalize() {
        MobileInfo mobileInfo = new MobileInfo.Builder().withModel("iPhone").build();
        Platform platform = new Platform.Builder()
                .withDeviceType(DeviceType.DESKTOP)
                .withMobileInfo(mobileInfo)
                .build();

        assertEquals(platform.getDeviceType(), DeviceType.DESKTOP);
    }

    @Test
    public void build_withNoMobileOrBrowserInfo_deviceTypeStaysNull() {
        Platform platform = new Platform.Builder().build();
        assertNull(platform.getDeviceType());
        assertNull(platform.getMobileInfo());
        assertNull(platform.getBrowserInfo());
    }

    @Test
    public void build_allFieldsSet() {
        Platform platform = new Platform.Builder()
                .withDeviceId("device-1")
                .withOs("Android")
                .withOsVersion("10")
                .withScreenResolution("1080x1920")
                .withLocation("US")
                .build();

        assertEquals(platform.getDeviceId(), "device-1");
        assertEquals(platform.getOs(), "Android");
        assertEquals(platform.getOsVersion(), "10");
        assertEquals(platform.getScreenResolution(), "1080x1920");
        assertEquals(platform.getLocation(), "US");
    }

    @Test
    public void copyConstructor_copiesAllFields() {
        Platform original = new Platform.Builder()
                .withDeviceId("device-1")
                .withOs("Android")
                .withOsVersion("10")
                .withScreenResolution("1080x1920")
                .withLocation("US")
                .withMobileInfo(new MobileInfo.Builder().withModel("Pixel").build())
                .build();

        Platform copy = new Platform.Builder(original).build();

        assertEquals(copy, original);
    }

    @Test
    public void equals_and_hashCode() {
        Platform platform1 = new Platform.Builder().withDeviceId("device-1").withOs("Android").build();
        Platform platform2 = new Platform.Builder().withDeviceId("device-1").withOs("Android").build();
        Platform platform3 = new Platform.Builder().withDeviceId("device-2").withOs("iOS").build();

        assertTrue(platform1.equals(platform1));
        assertTrue(platform1.equals(platform2));
        assertEquals(platform1.hashCode(), platform2.hashCode());
        assertFalse(platform1.equals(platform3));
        assertFalse(platform1.equals(null));
        assertFalse(platform1.equals("not a platform"));
    }
}
