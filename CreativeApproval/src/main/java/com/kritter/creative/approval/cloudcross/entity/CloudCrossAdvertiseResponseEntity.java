package com.kritter.creative.approval.cloudcross.entity;

import java.util.Date;

/**
 * Created by hamlin on 16-9-8.
 */
public class CloudCrossAdvertiseResponseEntity {
    private String regName;
    private String memo;
    private Date updated;
    private String name;
    private String state;
    private Integer industryId;
    private Integer advertiserId;
    private Integer dspId;

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }

    public Integer getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Integer advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Integer getDspId() {
        return dspId;
    }

    public void setDspId(Integer dspId) {
        this.dspId = dspId;
    }
}
