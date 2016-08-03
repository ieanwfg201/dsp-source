package com.kritter.tencent.reader_v20150313.converter.request;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestAppDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO;
import com.kritter.tencent.reader_v20150313.entity.TencentBidRequestParentNodeDTO;

import RTB.Tencent.Request;
import RTB.Tencent.Request.Impression;

public class ConvertRequest {

    /**
message Request {
    optional string id = 1; //bid request id, one bid request may contains multiple impressions.
    optional uint32 at = 2; //Auction type. 1: first pricing; 2: second pricing. Only type 2 is supported at present.
    optional Site site = 3; //Media site information.
    optional Device device = 4; //Device information.
    optional User user = 5; //User information.
    repeated Impression impression = 6; //Opportunity of ad impression. One request can contain multiple ad spaces. Each ad space represents one opportunity of ad impression.
    optional string DEPRECATED_inner_info = 7; //Ignore it. Useless field.
    optional App app = 8; //Mobile app information.
}
     */
    public static TencentBidRequestParentNodeDTO convert(Request request){
        if(request == null){
            return null;
        }
        TencentBidRequestParentNodeDTO openrtbRequest = new TencentBidRequestParentNodeDTO();
        if(request.hasId()){
            openrtbRequest.setBidRequestId(request.getId());
        }
        if(request.hasAt()){
            openrtbRequest.setAuctionType(request.getAt());
        }
        if(request.hasSite()){
            BidRequestSiteDTO bidRequestSite = ConvertSite.convert(request.getSite());
            if(bidRequestSite != null){
                openrtbRequest.setBidRequestSite(bidRequestSite);
            }
        }
        if(request.hasDevice()){
            BidRequestDeviceDTO device = ConvertDevice.convert(request.getDevice());
            if(device != null){
                openrtbRequest.setBidRequestDevice(device);
            }
        }
        if(request.hasUser()){
            BidRequestUserDTO user = ConvertUser.convert(request.getUser());
            if(user != null){
                openrtbRequest.setBidRequestUser(user);
            }
        }
        if(request.getImpressionList() != null && request.getImpressionList().size()>0){
            BidRequestImpressionDTO bidRequestImpressionArray[] = new BidRequestImpressionDTO[request.getImpressionList().size()];
            int count=0;
            for(Impression impression:request.getImpressionList()){
                BidRequestImpressionDTO bidRequestImpression = ConvertImpression.convert(impression);
                if(bidRequestImpression != null){
                    bidRequestImpressionArray[count] = bidRequestImpression;
                }
                count++;
            }
            openrtbRequest.setBidRequestImpressionArray(bidRequestImpressionArray);
        }
        /**
         * Deprecated
            if(request.getDEPRECATEDInnerInfo() != null){
            }
         */
        if(request.hasApp()){
            BidRequestAppDTO app = ConvertApp.convert(request.getApp());
            openrtbRequest.setBidRequestApp(app);
        }
        return openrtbRequest;
    }
}
