package com.kritter.geo.common.entity;

/**
 * Created by hamlin on 16-10-21.
 */
public class KritterCountryInput {
    private String startIP;
    private String endIP;
    private String countryCode;
    private String countryName;

    public String getStartIP() {
        return startIP;
    }

    public void setStartIP(String startIP) {
        this.startIP = startIP;
    }

    public String getEndIP() {
        return endIP;
    }

    public void setEndIP(String endIP) {
        this.endIP = endIP;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public KritterCountryInput(String startIP, String endIP, String countryCode, String countryName) {
        this.startIP = startIP;
        this.endIP = endIP;
        this.countryCode = countryCode;
        this.countryName = countryName;
    }

    public KritterCountryInput() {

    }
}
