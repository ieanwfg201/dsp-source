package com.kritter.creative.approval.cloudcross.entity;

/**
 * Created by hamlin on 16-9-8.
 */
public class CloudCrossAdvertieseStateResponseEntiry {
    private String refuseReason;
    private Integer state;
    private String stateValue;
    private Integer advertiserId;
    private Integer dspId;

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
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
