package com.kritter.valuemaker.reader_v20160817.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VamBidRequestParentNodeDTO extends BidRequestParentNodeDTO {

    private String appOrSite; //app or site

    private List<Integer> battr;

    public String getAppOrSite() {
        return appOrSite;
    }

    public void setAppOrSite(String appOrSite) {
        this.appOrSite = appOrSite;
    }

    public List<Integer> getBattr() {
        return battr;
    }

    public void setBattr(List<Integer> battr) {
        this.battr = battr;
    }
}
