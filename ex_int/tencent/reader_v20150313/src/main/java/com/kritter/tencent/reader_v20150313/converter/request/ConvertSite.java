package com.kritter.tencent.reader_v20150313.converter.request;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;

import RTB.Tencent.Request.Site;

public class ConvertSite {

/**
 *     message Site {
        optional string name = 1; //Website name.
        optional string page = 2; //URL of the current page.
        optional string ref = 3; //Referrer url.
        optional string channel = 4; //Channel number.
    }
 */
    public static BidRequestSiteDTO convert(Site site){
        if(site == null){
            return null;
        }
        BidRequestSiteDTO openrtbSite = new BidRequestSiteDTO();
        if(site.hasName()){
            openrtbSite.setSiteName(site.getName() );
        }
        if(site.hasPage()){
            openrtbSite.setSitePageURL(site.getPage());
        }
        if(site.hasRef()){
            openrtbSite.setRefererURL(site.getRef());
        }
        /** TODO What is channel? 
            if(site.getChannel() != null){
            }
         */
        return openrtbSite;
    }
}
