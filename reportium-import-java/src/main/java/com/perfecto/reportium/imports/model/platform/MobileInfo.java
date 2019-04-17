package com.perfecto.reportium.imports.model.platform;

import java.util.Objects;

/**
 * Created by michaeld on 22/02/2016.
 */
public class MobileInfo {

    private final String imei;
    private final String imsi;
    private final String manufacturer;
    private final String model;
    private final String phoneNumber;
    private final String distributor;
    private final String description;
    private final String firmware; // SW Version
    private final String operator;
    private final String operatorCountry;

    private MobileInfo(Builder builder) {
        imei = builder.imei;
        imsi = builder.imsi;
        manufacturer = builder.manufacturer;
        model = builder.model;
        phoneNumber = builder.phoneNumber;
        distributor = builder.distributor;
        description = builder.description;
        firmware = builder.firmware;
        operator = builder.operator;
        operatorCountry = builder.operatorCountry;
    }

    public String getImei() {
        return imei;
    }

    public String getImsi() {
        return imsi;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDistributor() {
        return distributor;
    }

    public String getDescription() {
        return description;
    }

    public String getFirmware() {
        return firmware;
    }

    public String getOperator() {
        return operator;
    }

    public String getOperatorCountry() {
        return operatorCountry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileInfo that = (MobileInfo) o;
        return Objects.equals(imei, that.imei) &&
                Objects.equals(imsi, that.imsi) &&
                Objects.equals(manufacturer, that.manufacturer) &&
                Objects.equals(model, that.model) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(distributor, that.distributor) &&
                Objects.equals(description, that.description) &&
                Objects.equals(firmware, that.firmware) &&
                Objects.equals(operator, that.operator) &&
                Objects.equals(operatorCountry, that.operatorCountry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imei, imsi, manufacturer, model, phoneNumber, distributor, description, firmware, operator, operatorCountry);
    }

    public static final class Builder {
        private String imei;
        private String imsi;
        private String manufacturer;
        private String model;
        private String phoneNumber;
        private String distributor;
        private String description;
        private String firmware;
        private String operator;
        private String operatorCountry;

        public Builder() {
        }

        public Builder(MobileInfo copy) {
            this.imei = copy.imei;
            this.imsi = copy.imsi;
            this.manufacturer = copy.manufacturer;
            this.model = copy.model;
            this.phoneNumber = copy.phoneNumber;
            this.distributor = copy.distributor;
            this.description = copy.description;
            this.firmware = copy.firmware;
            this.operator = copy.operator;
            this.operatorCountry = copy.operatorCountry;
        }

        public Builder withImei(String imei) {
            this.imei = imei;
            return this;
        }

        public Builder withImsi(String imsi) {
            this.imsi = imsi;
            return this;
        }

        public Builder withManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withDistributor(String distributor) {
            this.distributor = distributor;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withFirmware(String firmware) {
            this.firmware = firmware;
            return this;
        }

        public Builder withOperator(String operator) {
            this.operator = operator;
            return this;
        }

        public Builder withOperatorCountry(String operatorCountry) {
            this.operatorCountry = operatorCountry;
            return this;
        }

        public MobileInfo build() {
            return new MobileInfo(this);
        }
    }
}
