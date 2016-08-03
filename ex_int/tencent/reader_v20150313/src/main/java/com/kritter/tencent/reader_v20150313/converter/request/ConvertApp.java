package com.kritter.tencent.reader_v20150313.converter.request;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestAppDTO;

import RTB.Tencent.Request.App;

/**
 *     message App {
        optional string id = 1; //App ID. It is appeared as the package name on Android, such as com.rovio.angrybirds; appeared as AppStore ID on iOS, such as 327860; unavailable at present.
        optional string name = 2; //App name.
        optional string domain = 3; //App domain, unavailable at present.
        repeated string cat = 4; //Category information of the app in AppStore, Google Play or other app market, unavailable at present.
        repeated string sectioncat = 5; //Subcategory, unavailable at present.
    }
 *
 */
public class ConvertApp {
    public static BidRequestAppDTO convert(App app){
        if(app == null){
            return null;
        }
        BidRequestAppDTO openrtbApp= new BidRequestAppDTO();
        /** TODO When it gets available
        if(app.getId() != null){
            openrtbApp.setApplicationIdOnExchange(app.getId());
        }
        */
        if(app.hasName()){
            openrtbApp.setApplicationName(app.getName() );
        }
        if(app.hasDomain()){
            openrtbApp.setApplicationDomain(app.getDomain()); 
        }
        /** TODO When it gets available
        if(app.getCatList() != null){
        }
        */
        /** TODO When it gets available
        if(app.getSectioncatList() != null){
        }
        */
        return openrtbApp;
    }
}
