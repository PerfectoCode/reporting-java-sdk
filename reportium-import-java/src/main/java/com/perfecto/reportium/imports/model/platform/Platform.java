package com.perfecto.reportium.imports.model.platform;

import java.util.Objects;

/**
 * Created by michaeld on 23/02/2016.
 */
public class Platform {

    private final String deviceId;
    private final DeviceType deviceType;
    private final String os;
    private final String osVersion;
    private final String screenResolution;
    private final String location;
    private final MobileInfo mobileInfo;
    private final BrowserInfo browserInfo;

    private Platform(Builder builder) {
        deviceId = builder.deviceId;
        deviceType = builder.deviceType;
        os = builder.os;
        osVersion = builder.osVersion;
        screenResolution = builder.screenResolution;
        location = builder.location;
        mobileInfo = builder.mobileInfo;
        browserInfo = builder.browserInfo;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public String getOs() {
        return os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public String getLocation() {
        return location;
    }

    public MobileInfo getMobileInfo() {
        return mobileInfo;
    }

    public BrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Platform platform = (Platform) o;
        return Objects.equals(deviceId, platform.deviceId) &&
                deviceType == platform.deviceType &&
                Objects.equals(os, platform.os) &&
                Objects.equals(osVersion, platform.osVersion) &&
                Objects.equals(screenResolution, platform.screenResolution) &&
                Objects.equals(location, platform.location) &&
                Objects.equals(mobileInfo, platform.mobileInfo) &&
                Objects.equals(browserInfo, platform.browserInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, deviceType, os, osVersion, screenResolution, location, mobileInfo, browserInfo);
    }

    public static final class Builder {
        private String deviceId;
        private DeviceType deviceType;
        private String os;
        private String osVersion;
        private String screenResolution;
        private String location;
        private MobileInfo mobileInfo;
        private BrowserInfo browserInfo;

        public Builder() {
        }

        public Builder(Platform copy) {
            this.deviceId = copy.deviceId;
            this.deviceType = copy.deviceType;
            this.os = copy.os;
            this.osVersion = copy.osVersion;
            this.screenResolution = copy.screenResolution;
            this.location = copy.location;
            this.mobileInfo = copy.mobileInfo;
            this.browserInfo = copy.browserInfo;
        }

        public Builder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder withDeviceType(DeviceType deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public Builder withOs(String os) {
            this.os = os;
            return this;
        }

        public Builder withOsVersion(String osVersion) {
            this.osVersion = osVersion;
            return this;
        }

        public Builder withScreenResolution(String screenResolution) {
            this.screenResolution = screenResolution;
            return this;
        }

        public Builder withLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder withMobileInfo(MobileInfo mobileInfo) {
            this.mobileInfo = mobileInfo;
            return this;
        }

        public Builder withBrowserInfo(BrowserInfo browserInfo) {
            this.browserInfo = browserInfo;
            return this;
        }

        public Platform build() {
            validatePlatform();
            normalizePlatform();
            return new Platform(this);
        }

        private void validatePlatform() {
            if (mobileInfo != null && browserInfo != null) {
                throw new IllegalArgumentException("A platform cannot have both 'mobileInfo' or 'browserInfo'");
            }
        }

        private void normalizePlatform() {
            if (deviceType == null) {
                if (mobileInfo != null) {
                    deviceType = DeviceType.MOBILE;
                } else if (browserInfo != null) {
                    deviceType = DeviceType.DESKTOP;
                }
            }
        }
    }
}
