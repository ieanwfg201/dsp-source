package com.kritter.geo.common.entity;

/**
 * Created by hamlin on 16-10-21.
 */
public class SourceFileEntity {
    private String startIP;
    private String endIP;
    private String code;

    public SourceFileEntity() {
    }

    public SourceFileEntity(String startIP, String endIP, String code) {
        this.startIP = startIP;
        this.endIP = endIP;
        this.code = code;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
