package com.kritter.tencent.reader_v20150313.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestDeviceExtension {    
    @JsonProperty("openudid")
    @Setter
    private String openudid;
    @JsonProperty("md5idfa")
    @Setter
    private String md5idfa;
    @JsonProperty("sha1idfa")
    @Setter
    private String sha1idfa;
    public String getOpenudid() {
        return openudid;
    }
    public String getMd5idfa() {
        return md5idfa;
    }
    public String getSha1idfa() {
        return sha1idfa;
    }

}
