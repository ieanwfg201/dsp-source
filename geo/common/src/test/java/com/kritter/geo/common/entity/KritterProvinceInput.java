package com.kritter.geo.common.entity;

/**
 * Created by hamlin on 16-10-21.
 */
public class KritterProvinceInput {
    private String startIP;
    private String endIP;
    private String country;
    private String provinceCode;
    private String provinceName;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public KritterProvinceInput(String startIP, String endIP, String country, String provinceCode, String provinceName) {

        this.startIP = startIP;
        this.endIP = endIP;
        this.country = country;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
    }

    public KritterProvinceInput() {

    }
}
