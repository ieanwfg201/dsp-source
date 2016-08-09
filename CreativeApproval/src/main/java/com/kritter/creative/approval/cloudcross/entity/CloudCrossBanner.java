package com.kritter.creative.approval.cloudcross.entity;

/**
 * Created by hamlin on 16-7-31.
 */
public class CloudCrossBanner {
    private Integer bannerId;
    private Integer advertiserId;//必须 Integer 对应 DSP 系统的广告主 ID
    private String path;//path 必须 String 创意 URL
    private Integer width;//width 必须 Integer 创意宽度
    private Integer height;//height height 必须 Integer 创意高度
    private Integer rwidth;//rwidth rwidth 必须 Integer 创意宽度 - 比例
    private Integer rheight;//rheight rheight

    public Integer getBannerId() {
        return bannerId;
    }

    public void setBannerId(Integer bannerId) {
        this.bannerId = bannerId;
    }

    public Integer getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Integer advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getRwidth() {
        return rwidth;
    }

    public void setRwidth(Integer rwidth) {
        this.rwidth = rwidth;
    }

    public Integer getRheight() {
        return rheight;
    }

    public void setRheight(Integer rheight) {
        this.rheight = rheight;
    }
}
