package com.kritter.geo.common.entity;

/**
 * Created by hamlin on 16-10-21.
 */
public class WordBookEntity {
    private String province;
    private String city;
    private String code;

    public WordBookEntity() {
    }

    public WordBookEntity(String province, String city, String code) {
        this.province = province;
        this.city = city;
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
