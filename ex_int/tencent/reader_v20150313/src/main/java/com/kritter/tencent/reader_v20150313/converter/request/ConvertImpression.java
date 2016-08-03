package com.kritter.tencent.reader_v20150313.converter.request;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDealDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionBannerObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionVideoObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestPMPDTO;

import RTB.Tencent.Request.Impression;

public class ConvertImpression {
    /**
    message Impression {
        optional string id = 1; //Unique ID of impression.
        optional string tagid = 2; //Ad space ID, which is similar to the ad space ID in resource report, such as Ent_F_Width1.
        optional float bidfloor = 3; //Base price of impression. Unit: fen/CPM.
        optional Banner banner = 4; //Banner ad space.
        optional Video video = 5; //Video ad space.
        optional string clientid = 6; //Invalid field.
        optional string tradecode = 7; //List of restriction industry code of this ad space, such as 018;014;008;006;001;004;002.
        optional string sns_lists = 8; //(Suspended) List of socialized display format supported by this ad space.
        optional string dealid = 9; //Only available for GPB, which is used to inform DSP about the advertising.
        repeated MaterialFormat adm_require = 10; //Invalid field.
    }
     */
    public static BidRequestImpressionDTO convert(Impression impression){
        if(impression == null){
            return null;
        }
        BidRequestImpressionDTO openrtbImpression = new BidRequestImpressionDTO();
        if(impression.hasId()){
            openrtbImpression.setBidRequestImpressionId(impression.getId());
        }
        if(impression.hasTagid()){
            openrtbImpression.setAdTagOrPlacementId(impression.getTagid());
        }
        if(impression.hasBidfloor()){
            openrtbImpression.setBidFloorPrice((double)impression.getBidfloor());
        }
        if(impression.hasBanner()){
            BidRequestImpressionBannerObjectDTO banner = ConvertBanner.convert(impression.getBanner());
            if(banner != null){
                openrtbImpression.setBidRequestImpressionBannerObject(banner);
            }
        }
        if(impression.hasVideo()){
            BidRequestImpressionVideoObjectDTO video = ConvertVideo.convert(impression.getVideo() );
            if(video != null){
                openrtbImpression.setBidRequestImpressionVideoObject(video);
            }
        }
        /**Invalid field
            if(impression.getClientid()!= null){
            }
         */
        /**
         * Figure out trade code
         */
        if(impression.getTradecode() != null){
        }
        /** Suspended
        if(impression.getSnsLists() != null){
        }
        */
        if(impression.getDealid() != null){
            BidRequestPMPDTO bidRequestPMPDTO = new BidRequestPMPDTO();
            BidRequestDealDTO privateAuctionDeals[] = new BidRequestDealDTO[1];
            BidRequestDealDTO deal = new BidRequestDealDTO();
            deal.setDealId(impression.getDealid());
            privateAuctionDeals[0] = deal;
            bidRequestPMPDTO.setPrivateAuctionDeals(privateAuctionDeals);
            bidRequestPMPDTO.setPrivateAuction(0);
            openrtbImpression.setBidRequestPMPDTO(bidRequestPMPDTO);
        }
        /**
         * Invalid field
            if(impression.getAdmRequireList() != null){
            }
         */
        return openrtbImpression;
    }
}
