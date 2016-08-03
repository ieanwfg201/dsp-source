package com.kritter.tencent.reader_v20150313.converter.request;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestGeoDTO;

import RTB.Tencent.Request.Device.Geo;

public class ConvertGeo {
    /**
        message Geo {
            optional float latitude = 1; //Latitude. Value scope: -90 to 90. South is set to negative value.
            optional float longitude = 2; //Longitude. Value scope: -180 to 180. West is set to negative value.
        }
     */
    public static BidRequestGeoDTO convert(Geo geo){
        if(geo == null){
            return null;
        }
        BidRequestGeoDTO openrtbGeo = new BidRequestGeoDTO();
        if(geo.hasLatitude()){
            openrtbGeo.setGeoLatitude(geo.getLatitude());
        }
        if(geo.hasLongitude()){
            openrtbGeo.setGeoLongitude(geo.getLongitude());
        }
        return openrtbGeo;
    }
}
