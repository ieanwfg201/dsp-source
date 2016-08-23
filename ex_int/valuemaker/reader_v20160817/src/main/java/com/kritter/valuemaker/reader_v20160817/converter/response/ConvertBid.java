package com.kritter.valuemaker.reader_v20160817.converter.response;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseBidEntity;

import RTB.VamRealtimeBidding.VamResponse;
import RTB.VamRealtimeBidding.VamResponse.Bid;

public class ConvertBid {

    public static Bid convert(String crid, float price, String win_url, String show_url,String click_url,
            String dealId){
        VamResponse.Bid.Builder bidBuilder =  VamResponse.Bid.newBuilder();
        bidBuilder.setId(1);
        bidBuilder.setCmflag(1);
        if(crid != null){
            bidBuilder.setCrid(crid);
        }
        bidBuilder.setPrice((int)price);
        if(win_url != null){
            bidBuilder.setWinnoticeUrl(win_url);
        }
        if(dealId != null){
            bidBuilder.setDealId(Integer.parseInt(dealId));
        }
        return bidBuilder.build();
    }
    public static Bid convert(BidResponseBidEntity openrtbBid){
        if(openrtbBid == null){
            return null;
        }
        return convert(openrtbBid.getCreativeId(), openrtbBid.getPrice(),
                openrtbBid.getWinNotificationUrl(),openrtbBid.getSampleImageUrl(),
                null,openrtbBid.getDealId());
    }
}
