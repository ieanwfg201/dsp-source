package com.kritter.creative.approval.cloudcross.entity;

import java.util.Date;

/**
 * Created by hamlin on 16-9-8.
 */
public class CloudCrossBannerResponseEntity {
    private Integer rheight ;
    private Integer id;
    private Integer height;
    private String memo;
    private Integer rwidth;
    private Date updated;
    private Integer width;
    private Integer state;
    private String path;
    private Integer advertiserId;
    private Integer dspId;

    public Integer getRheight() {
        return rheight;
    }

    public void setRheight(Integer rheight) {
        this.rheight = rheight;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getRwidth() {
        return rwidth;
    }

    public void setRwidth(Integer rwidth) {
        this.rwidth = rwidth;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
