package com.kritter.valuemaker.reader_v20160817.converter.response;

import RTB.VamRealtimeBidding;
import RTB.VamRealtimeBidding.VamResponse;

import java.util.List;


public class ConvertResponse {

    public static VamResponse convert(
            VamRealtimeBidding.VamRequest vamRequest,
            String crid,
            int price,
            String win_url,
            String clk_url,
            String show_url,
            List<String> clkTracker,
            List<String> impTracker) {

        VamRealtimeBidding.VamResponse.Builder vamResponseBuilder = VamRealtimeBidding.VamResponse.newBuilder();
        vamResponseBuilder.setId(vamRequest.getId());
        vamResponseBuilder.setCur("CNY");

        VamRealtimeBidding.VamResponse.Bid.Builder vamBidBuilder = VamRealtimeBidding.VamResponse.Bid.newBuilder();
        vamBidBuilder.setCrid(crid);
        vamBidBuilder.setPrice(price);
        if (vamRequest.getDisplayCount() > 0) {
            vamBidBuilder.setId(vamRequest.getDisplay(0).getId());
        }
        vamBidBuilder.setWinnoticeUrl(win_url);
        vamBidBuilder.setCmflag(1); //TODO cookie map是否需要打开

//        vamBidBuilder.addAdvertiserName("");//该字段暂未使用

        if (vamRequest.getPmpInfoCount() > 0) {
            VamRealtimeBidding.VamRequest.PmpInfo pmpInfo = vamRequest.getPmpInfo(0);
            if (pmpInfo != null) {
                vamBidBuilder.setDealId(pmpInfo.getDealId());
            }
        }

        if (vamRequest.getDisplayCount() > 0) {
            VamRealtimeBidding.VamResponse.Bid.Display.Builder dispalyBuilder = VamRealtimeBidding.VamResponse.Bid.Display.newBuilder();
            dispalyBuilder.setClickUrl(clk_url);
            dispalyBuilder.setShowUrl(show_url);
            vamBidBuilder.setDisplayBidding(dispalyBuilder);
        } else if (vamRequest.hasVamMobile()) {
            VamRealtimeBidding.VamResponse.Bid.Mobile.Builder mobileBuilder = VamRealtimeBidding.VamResponse.Bid.Mobile.newBuilder();
            mobileBuilder.addClickUrls(clk_url);
            if (clkTracker != null && clkTracker.size() > 0) {
                mobileBuilder.addAllClickUrls(clkTracker);
            }

            mobileBuilder.addShowUrls(show_url);
            if (impTracker != null && impTracker.size() > 0) {
                mobileBuilder.addAllShowUrls(impTracker);
            }

            vamBidBuilder.setMobileBidding(mobileBuilder);
        } else if (vamRequest.hasVamVideo() || vamRequest.hasVamMobileVideo()) {
            VamRealtimeBidding.VamResponse.Bid.Video.Builder videoBuilder = VamRealtimeBidding.VamResponse.Bid.Video.newBuilder();
            videoBuilder.addClickUrls(clk_url);
            if (clkTracker != null && clkTracker.size() > 0) {
                videoBuilder.addAllClickUrls(clkTracker);
            }

            videoBuilder.addShowUrls(show_url);
            if (impTracker != null && impTracker.size() > 0) {
                videoBuilder.addAllShowUrls(impTracker);
            }

            //TODO
            VamRealtimeBidding.VamResponse.Bid.Video.Event.Builder eventBuilder = VamRealtimeBidding.VamResponse.Bid.Video.Event.newBuilder();
            eventBuilder.setEventName("");
            eventBuilder.setTrackUrl("");
            eventBuilder.setOffset("");

            videoBuilder.addEvents(eventBuilder);
            vamBidBuilder.setVideoBidding(videoBuilder);
        } else {
            return null;
        }
        vamResponseBuilder.addBid(vamBidBuilder);
        return vamResponseBuilder.build();
    }
}
