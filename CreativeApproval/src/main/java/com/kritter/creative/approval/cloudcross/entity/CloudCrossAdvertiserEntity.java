package com.kritter.creative.approval.cloudcross.entity;

/**
 * Created by hamlin on 16-7-31.
 */
public class CloudCrossAdvertiserEntity {
    private Integer dspId;//dsp_id 必须 Integer 对接方 dspId
    private Integer advertiserId;//advertiser_id advertiserId 必须 Integer 对应 DSP 系统的广告主 IDindustry_id industryId 必须 Integer 行业 ID
    private Integer industryId;// industry_id industryId 必须 Integer 行业 ID
    private Integer name;//name name 必须 Integer 广告主名称
    private String regName;//reg_name regName 必须 String 广告主公司注册名
    private String homepage;//homepage homepage 推荐 String 公司网址
    private String tel;//tel tel 可选 String 电话
    private String email;//email email 可选 String 邮箱
    private String licencePath;//licence_path licencePath 推荐 String 营业执照路径
    private String idPath;//id_path idPath 推荐 String 法人代表身份证路径
    private String orgPath;//org_path orgPath 推荐 String 企业机构代码证路径
    private String cpiPath;//cpi_path cpiPath 推荐 String CPI 文件路径

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

    public Integer getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicencePath() {
        return licencePath;
    }

    public void setLicencePath(String licencePath) {
        this.licencePath = licencePath;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public String getOrgPath() {
        return orgPath;
    }

    public void setOrgPath(String orgPath) {
        this.orgPath = orgPath;
    }

    public String getCpiPath() {
        return cpiPath;
    }

    public void setCpiPath(String cpiPath) {
        this.cpiPath = cpiPath;
    }
}
