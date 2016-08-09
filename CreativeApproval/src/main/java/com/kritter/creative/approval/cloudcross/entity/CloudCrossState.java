package com.kritter.creative.approval.cloudcross.entity;

/**
 * Created by hamlin on 16-7-31.
 */
public class CloudCrossState {
    /*
        only query banner state
     */
    private Integer bannerId;
    private Integer dspId;
    private Integer advertiserId;
    /*
    0:检查通过
    1:待检查(默认)
    2:检查未通过,原因通过 refuseReason 字段说明
     */
    private Integer state;
    private String refuseReason;

    public Integer getBannerId() {
        return bannerId;
    }

    public void setBannerId(Integer bannerId) {
        this.bannerId = bannerId;
    }

    public Integer getDspId() {
        return dspId;
    }

    public void setDspId(Integer dspId) {
        this.dspId = dspId;
    }

    public Integer getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Integer advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }
}
