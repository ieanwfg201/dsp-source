package com.kritter.valuemaker.reader_v20160817.converter.request;

import RTB.VamRealtimeBidding.VamRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;

import java.util.List;

public class ConvertRequest {

    static VamBidRequestParentNodeDTO openrtbRequest = new VamBidRequestParentNodeDTO();

    public static VamBidRequestParentNodeDTO convert(VamRequest request) {

        if (request == null) {
            return null;
        }

        BidRequestImpressionDTO bidRequestImpressionDTO = new BidRequestImpressionDTO();
        UUIDGenerator uuidGenerator= new UUIDGenerator();
        bidRequestImpressionDTO.setBidRequestImpressionId(uuidGenerator.generateUniversallyUniqueIdentifier().toString());

        openrtbRequest.setBidRequestImpressionArray(new BidRequestImpressionDTO[]{bidRequestImpressionDTO});

        BidRequestSiteDTO bidRequestSiteDTO = new BidRequestSiteDTO();
        openrtbRequest.setBidRequestSite(bidRequestSiteDTO);

        BidRequestAppDTO bidRequestAppDTO = new BidRequestAppDTO();
        openrtbRequest.setBidRequestApp(bidRequestAppDTO);

        BidRequestDeviceDTO bidRequestDeviceDTO = new BidRequestDeviceDTO();
        openrtbRequest.setBidRequestDevice(bidRequestDeviceDTO);

        BidRequestUserDTO bidRequestUserDTO = new BidRequestUserDTO();
        openrtbRequest.setBidRequestUser(bidRequestUserDTO);


        if (request.hasId()) {
            openrtbRequest.setBidRequestId(request.getId());
        }
        if (request.hasTMax()) {
            openrtbRequest.setMaxTimeoutForBidSubmission(request.getTMax());
        }

        if (request.hasCookie()) {
            openrtbRequest.getBidRequestUser().setConsumerCustomData(request.getCookie());
        }
        if (request.hasUserAgent()) {
            openrtbRequest.getBidRequestDevice().setDeviceUserAgent(request.getUserAgent());
        }

        if (request.hasDnt()) {
            if (request.getDnt()) {
                openrtbRequest.getBidRequestDevice().setDoNotTrackDevice(1);
            } else {
                openrtbRequest.getBidRequestDevice().setDoNotTrackDevice(0);
            }
        }

        if (request.hasIp()) {
            openrtbRequest.getBidRequestDevice().setIpV4AddressClosestToDevice(request.getIp());
        }

        if (request.hasLanguage()) {
            openrtbRequest.getBidRequestDevice().setBrowserLanguage(request.getLanguage());
        }

        if (request.hasDeviceType()) {
            switch (request.getDeviceType().getNumber()) {
                case 1:
                    openrtbRequest.getBidRequestDevice().setDeviceType(2);
                case 2:
                    openrtbRequest.getBidRequestDevice().setDeviceType(1);
                case 3:
                    openrtbRequest.getBidRequestDevice().setDeviceType(1);
                case 4:
                    openrtbRequest.getBidRequestDevice().setDeviceType(3);
            }
        }


        if (request.hasMediaId()) {
            openrtbRequest.getBidRequestSite().setSiteIdOnExchange(request.getDomain());
        }
        if (request.hasDomain()) {
            openrtbRequest.getBidRequestSite().setSiteDomain(request.getDomain());
        }
        if (request.hasPage()) {
            openrtbRequest.getBidRequestSite().setSitePageURL(request.getPage());
        }
        if (request.hasReferer()) {
            openrtbRequest.getBidRequestSite().setRefererURL(request.getReferer());
        }


        //vertical,data_source,data_source,premium_price

        if (request.getPmpInfoCount() > 0 && request.getPmpInfo(0) != null) {

            BidRequestPMPDTO bidRequestPMPDTO = new BidRequestPMPDTO();
            bidRequestPMPDTO.setPrivateAuction(0);

            BidRequestDealDTO bidRequestDealDTO = new BidRequestDealDTO();

            VamRequest.PmpInfo pmpInfo = request.getPmpInfo(0);
            if (pmpInfo.hasDealId()) {
                bidRequestDealDTO.setDealId(String.valueOf(pmpInfo.getDealId()));
            }
            if (pmpInfo.hasPreferPrice()) {
                bidRequestDealDTO.setBidFloor((float) pmpInfo.getPreferPrice() / 100);
            }
            bidRequestPMPDTO.setPrivateAuctionDeals(new BidRequestDealDTO[]{bidRequestDealDTO});
            bidRequestImpressionDTO.setBidRequestPMPDTO(bidRequestPMPDTO);
        }


        if (request.getDisplayCount() > 0 && request.getDisplay(0) != null) {
            display(openrtbRequest, request.getDisplay(0));
        } else if (request.hasVamVideo()) {
            video(openrtbRequest, request.getVamVideo());
        } else if (request.hasVamMobile()) {
            mobile(openrtbRequest, request.getVamMobile());
        } else if (request.hasVamMobileVideo()) {
            mobileVideo(openrtbRequest, request.getVamMobileVideo());
        } else {
            return openrtbRequest;
        }


        openrtbRequest.setExtensionObject(request);

        return openrtbRequest;
    }

    private static void display(VamBidRequestParentNodeDTO openrtbRequest, VamRequest.Display display) {
        openrtbRequest.setAppOrSite("site");
        BidRequestImpressionDTO impressionDTO = openrtbRequest.getBidRequestImpressionArray()[0];
        BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObjectDTO = new BidRequestImpressionBannerObjectDTO();
        if (display.hasAdspaceId()) {
            impressionDTO.setAdTagOrPlacementId(String.valueOf(display.getAdspaceId()));
        }
        if (display.hasBidfloor()) {
            impressionDTO.setBidFloorPrice((double) display.getBidfloor() / 100);
        }
        //screen_level
        //id固定为1
        if (display.hasWidth()) {
            bidRequestImpressionBannerObjectDTO.setBannerWidthInPixels(display.getWidth());
        }
        if (display.hasHeight()) {
            bidRequestImpressionBannerObjectDTO.setBannerHeightInPixels(display.getHeight());
        }
        //adformat

//        adform

        if (display.getExcludedCatCount() > 0) {
            List<Integer> excluded_cat = display.getExcludedCatList();
            Short[] battr = new Short[excluded_cat.size()];
            for (int i = 0; i < excluded_cat.size(); i++) {
                battr[i] = excluded_cat.get(i).shortValue();
            }
            bidRequestImpressionBannerObjectDTO.setBlockedCreativeAttributes(battr);
        }

        if (display.getExcludedAdvCount() > 0) {
            String[] excluded_adv = new String[]{};
            excluded_adv = display.getExcludedAdvList().toArray(excluded_adv);
            openrtbRequest.setBlockedAdvertiserDomainsForBidRequest(excluded_adv);
        }

        impressionDTO.setBidRequestImpressionBannerObject(bidRequestImpressionBannerObjectDTO);

    }

    private static void mobile(VamBidRequestParentNodeDTO openrtbRequest, VamRequest.Mobile mobile) {
        openrtbRequest.setAppOrSite("app");
        BidRequestImpressionDTO impressionDTO = openrtbRequest.getBidRequestImpressionArray()[0];
        BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObjectDTO = new BidRequestImpressionBannerObjectDTO();
        if (mobile.hasAdspaceId()) {
            impressionDTO.setAdTagOrPlacementId(String.valueOf(mobile.getAdspaceId()));
        }
        if (mobile.hasBidfloor()) {
            impressionDTO.setBidFloorPrice((double) mobile.getBidfloor() / 100);
        }
        if (mobile.hasWidth()) {
            bidRequestImpressionBannerObjectDTO.setBannerWidthInPixels(mobile.getWidth());
        }
        if (mobile.hasHeight()) {
            bidRequestImpressionBannerObjectDTO.setBannerHeightInPixels(mobile.getHeight());
        }
        //adformat

        if (mobile.hasBrand()) {
            openrtbRequest.getBidRequestDevice().setDeviceManufacturer(mobile.getBrand());
        }
        if (mobile.hasModel()) {
            openrtbRequest.getBidRequestDevice().setDeviceModel(mobile.getModel());
        }

        if (mobile.hasOs()) {
            switch (mobile.getOs()) {
                case 0:
                    openrtbRequest.getBidRequestDevice().setDeviceOperatingSystem("Other");
                case 1:
                    openrtbRequest.getBidRequestDevice().setDeviceOperatingSystem("iOS");
                case 2:
                    openrtbRequest.getBidRequestDevice().setDeviceOperatingSystem("Android");
                default:
                    openrtbRequest.getBidRequestDevice().setDeviceOperatingSystem("Other");
            }
        }
        if (mobile.hasOsVersion()) {
            openrtbRequest.getBidRequestDevice().setDeviceOperatingSystemVersion(mobile.getOsVersion());
        }


        if (mobile.hasImei()) {
            openrtbRequest.getBidRequestDevice().setMD5HashedDeviceId(mobile.getImei());
        }

        if (mobile.hasMac()) {
            openrtbRequest.getBidRequestDevice().setHashedMD5MacAddressOfDevice(mobile.getMac());
        }
        if (mobile.hasAid()) {
            openrtbRequest.getBidRequestDevice().setMD5HashedDevicePlatformId(mobile.getAid());
        }
        //aaid

        if (mobile.hasIDFA()) {
            openrtbRequest.getBidRequestDevice().setIfa(mobile.getIDFA());
        }
        //OpenUDID
        //source
        if (mobile.hasPgn()) {
            openrtbRequest.getBidRequestApp().setApplicationBundleName(mobile.getPgn());
        }
        if (mobile.hasAppName()) {
            openrtbRequest.getBidRequestApp().setApplicationName(mobile.getAppName());
        }

        if (mobile.hasScreenWidth()) {
            openrtbRequest.getBidRequestDevice().setDevicePhysicalWidthInPixels(mobile.getScreenWidth());
        }
        if (mobile.hasScreenHeight()) {
            openrtbRequest.getBidRequestDevice().setDevicePhysicalHeightInPixels(mobile.getScreenHeight());
        }

        if (mobile.hasNetwork()) {
            switch (mobile.getNetwork()) {
                case 0:
                    openrtbRequest.getBidRequestDevice().setConnectionType(0);
                case 1:
                    openrtbRequest.getBidRequestDevice().setConnectionType(2);
                case 2:
                    openrtbRequest.getBidRequestDevice().setConnectionType(4);
                case 3:
                    openrtbRequest.getBidRequestDevice().setConnectionType(5);
                case 4:
                    openrtbRequest.getBidRequestDevice().setConnectionType(6);
                default:
                    openrtbRequest.getBidRequestDevice().setConnectionType(0);
            }
        }

        //TODO 字母是否这么写
        if (mobile.hasOperateId()) {
            switch (mobile.getOperateId()) {
                case 0:
                    openrtbRequest.getBidRequestDevice().setCarrier("UNKNOWN");
                case 1:
                    openrtbRequest.getBidRequestDevice().setCarrier("Mobile");
                case 2:
                    openrtbRequest.getBidRequestDevice().setCarrier("Unicom");
                case 3:
                    openrtbRequest.getBidRequestDevice().setCarrier("Telecom");
                default:
                    openrtbRequest.getBidRequestDevice().setCarrier("UNKNOWN");
            }
        }

        if (mobile.hasCorner()) {
            BidRequestGeoDTO bidRequestGeoDTO = new BidRequestGeoDTO();
            if (mobile.getCorner().hasLatitude()) {
                bidRequestGeoDTO.setGeoLatitude(mobile.getCorner().getLatitude());
            }
            if (mobile.getCorner().hasLongitude()) {
                bidRequestGeoDTO.setGeoLongitude(mobile.getCorner().getLongitude());
            }
            openrtbRequest.getBidRequestDevice().setGeoObject(bidRequestGeoDTO);
        }

        if (mobile.hasFullScreen()) {
            if (mobile.getFullScreen()) {
                impressionDTO.setIsAdInterstitial(1);
            } else {
                impressionDTO.setIsAdInterstitial(0);
            }
        }
        //ad_location
        //TODO code需要转换
        if (mobile.hasAppCategory()) {
//            openrtbRequest.getBidRequestApp().setContentCategoriesApplication();
        }
        //adform
        //mpn
        if (mobile.hasGender()) {
            switch (mobile.getGender()) {
                case 0:
                    openrtbRequest.getBidRequestUser().setGender("O");
                case 1:
                    openrtbRequest.getBidRequestUser().setGender("M");
                case 2:
                    openrtbRequest.getBidRequestUser().setGender("F");
                default:
                    openrtbRequest.getBidRequestUser().setGender("O");

            }
        }
        if (mobile.hasBd()) {
            openrtbRequest.getBidRequestUser().setYearOfBirth(mobile.getBd());
        }
        //screen_level

        impressionDTO.setBidRequestImpressionBannerObject(bidRequestImpressionBannerObjectDTO);

    }

    private static void video(VamBidRequestParentNodeDTO openrtbRequest, VamRequest.Video video) {
        openrtbRequest.setAppOrSite("site");
        BidRequestImpressionDTO impressionDTO = openrtbRequest.getBidRequestImpressionArray()[0];
        BidRequestImpressionVideoObjectDTO bidRequestImpressionVideoObjectDTO = new BidRequestImpressionVideoObjectDTO();
        if (video.hasAdspaceId()) {
            impressionDTO.setAdTagOrPlacementId(String.valueOf(video.getAdspaceId()));
        }
        if (video.hasBidfloor()) {
            impressionDTO.setBidFloorPrice((double) video.getBidfloor() / 100);
        }

        if (video.hasLinear()) {
            if (video.getLinear().getNumber() == 1) {
                bidRequestImpressionVideoObjectDTO.setIsVideoLinear(1);
            } else {
                bidRequestImpressionVideoObjectDTO.setIsVideoLinear(2);
            }
        }

        if (video.hasVamProtocol()) {
            Integer[] number = {video.getVamProtocol().getNumber()};
            bidRequestImpressionVideoObjectDTO.setVideoBidResponseProtocol(number);
        }

        if (video.hasWidth()) {
            bidRequestImpressionVideoObjectDTO.setWidthVideoPlayerInPixels(video.getWidth());
        }
        if (video.hasHeight()) {
            bidRequestImpressionVideoObjectDTO.setHeightVideoPlayerInPixels(video.getHeight());
        }
        if (video.hasMaxDuration()) {
            bidRequestImpressionVideoObjectDTO.setMaxDurationOfVideo(video.getMaxDuration());
        }
        if (video.hasMinDuration()) {
            bidRequestImpressionVideoObjectDTO.setMinimumDurationOfVideo(video.getMinDuration());
        }

        if (video.hasVideoAdform()) {
            switch (video.getVideoAdform()) {
                case 0:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(0);
                case 1:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(-1);
                case 2:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(-2);
                case 4:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(1);
                default:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(1);

            }
        }

        if (video.getMimesCount() > 0) {
            String[] mimeTypes = new String[]{};
            mimeTypes = video.getMimesList().toArray(mimeTypes);
            bidRequestImpressionVideoObjectDTO.setMimeTypesSupported(mimeTypes);
        }
//        video_adformat
        if (video.hasKeyword()) {
            openrtbRequest.getBidRequestApp().setAppKeywordsCSV(video.getKeyword());
        }
        if (video.getExcludedCatCount() > 0) {
            List<Integer> excluded_cat = video.getExcludedCatList();
            Integer[] battr = new Integer[excluded_cat.size()];
            for (int i = 0; i < excluded_cat.size(); i++) {
                battr[i] = excluded_cat.get(i);
            }
            bidRequestImpressionVideoObjectDTO.setBlockedCreativeAttributes(battr);
        }

        if (video.getExcludedAdvCount() > 0) {
            String[] excluded_adv = new String[]{};
            excluded_adv = video.getExcludedAdvList().toArray(excluded_adv);
            openrtbRequest.setBlockedAdvertiserDomainsForBidRequest(excluded_adv);
        }
//        ad_tech
        impressionDTO.setBidRequestImpressionVideoObject(bidRequestImpressionVideoObjectDTO);

    }

    private static void mobileVideo(VamBidRequestParentNodeDTO openrtbRequest, VamRequest.Mobile_Video mobileVideo) {
        openrtbRequest.setAppOrSite("app");
        BidRequestImpressionDTO impressionDTO = openrtbRequest.getBidRequestImpressionArray()[0];
        BidRequestImpressionVideoObjectDTO bidRequestImpressionVideoObjectDTO = new BidRequestImpressionVideoObjectDTO();
        if (mobileVideo.hasAdspaceId()) {
            impressionDTO.setAdTagOrPlacementId(String.valueOf(mobileVideo.getAdspaceId()));
        }
        if (mobileVideo.hasBidfloor()) {
            impressionDTO.setBidFloorPrice((double) mobileVideo.getBidfloor() / 100);
        }
        //adformat

        if (mobileVideo.hasBrand()) {
            openrtbRequest.getBidRequestDevice().setDeviceManufacturer(mobileVideo.getBrand());
        }
        if (mobileVideo.hasModel()) {
            openrtbRequest.getBidRequestDevice().setDeviceModel(mobileVideo.getModel());
        }

        if (mobileVideo.hasOs()) {
            switch (mobileVideo.getOs()) {
                case 0:
                    openrtbRequest.getBidRequestDevice().setDeviceOperatingSystem("Other");
                case 1:
                    openrtbRequest.getBidRequestDevice().setDeviceOperatingSystem("iOS");
                case 2:
                    openrtbRequest.getBidRequestDevice().setDeviceOperatingSystem("Android");
                default:
                    openrtbRequest.getBidRequestDevice().setDeviceOperatingSystem("Other");
            }
        }
        if (mobileVideo.hasOsVersion()) {
            openrtbRequest.getBidRequestDevice().setDeviceOperatingSystemVersion(mobileVideo.getOsVersion());
        }

        if (mobileVideo.hasImei()) {
            openrtbRequest.getBidRequestDevice().setMD5HashedDeviceId(mobileVideo.getImei());
        }

        if (mobileVideo.hasMac()) {
            openrtbRequest.getBidRequestDevice().setHashedMD5MacAddressOfDevice(mobileVideo.getMac());
        }
        if (mobileVideo.hasAid()) {
            openrtbRequest.getBidRequestDevice().setMD5HashedDevicePlatformId(mobileVideo.getAid());
        }
        //aaid

        if (mobileVideo.hasIDFA()) {
            openrtbRequest.getBidRequestDevice().setIfa(mobileVideo.getIDFA());
        }
        //OpenUDID
        //source
        if (mobileVideo.hasPgn()) {
            openrtbRequest.getBidRequestApp().setApplicationBundleName(mobileVideo.getPgn());
        }
        if (mobileVideo.hasAppName()) {
            openrtbRequest.getBidRequestApp().setApplicationName(mobileVideo.getAppName());
        }

        if (mobileVideo.hasScreenWidth()) {
            openrtbRequest.getBidRequestDevice().setDevicePhysicalWidthInPixels(mobileVideo.getScreenWidth());
        }
        if (mobileVideo.hasScreenHeight()) {
            openrtbRequest.getBidRequestDevice().setDevicePhysicalHeightInPixels(mobileVideo.getScreenHeight());
        }

        if (mobileVideo.hasNetwork()) {
            switch (mobileVideo.getNetwork()) {
                case 0:
                    openrtbRequest.getBidRequestDevice().setConnectionType(0);
                case 1:
                    openrtbRequest.getBidRequestDevice().setConnectionType(2);
                case 2:
                    openrtbRequest.getBidRequestDevice().setConnectionType(4);
                case 3:
                    openrtbRequest.getBidRequestDevice().setConnectionType(5);
                case 4:
                    openrtbRequest.getBidRequestDevice().setConnectionType(6);
                default:
                    openrtbRequest.getBidRequestDevice().setConnectionType(0);
            }
        }

        //TODO 字母是否这么写
        if (mobileVideo.hasOperateId()) {
            switch (mobileVideo.getOperateId()) {
                case 0:
                    openrtbRequest.getBidRequestDevice().setCarrier("UNKNOWN");
                case 1:
                    openrtbRequest.getBidRequestDevice().setCarrier("Mobile");
                case 2:
                    openrtbRequest.getBidRequestDevice().setCarrier("Unicom");
                case 3:
                    openrtbRequest.getBidRequestDevice().setCarrier("Telecom");
                default:
                    openrtbRequest.getBidRequestDevice().setCarrier("UNKNOWN");
            }
        }

        if (mobileVideo.hasCorner()) {
            BidRequestGeoDTO bidRequestGeoDTO = new BidRequestGeoDTO();
            if (mobileVideo.getCorner().hasLatitude()) {
                bidRequestGeoDTO.setGeoLatitude(mobileVideo.getCorner().getLatitude());
            }
            if (mobileVideo.getCorner().hasLongitude()) {
                bidRequestGeoDTO.setGeoLongitude(mobileVideo.getCorner().getLongitude());
            }
            openrtbRequest.getBidRequestDevice().setGeoObject(bidRequestGeoDTO);
        }
        //mpn

        if (mobileVideo.hasGender()) {
            switch (mobileVideo.getGender()) {
                case 0:
                    openrtbRequest.getBidRequestUser().setGender("O");
                case 1:
                    openrtbRequest.getBidRequestUser().setGender("M");
                case 2:
                    openrtbRequest.getBidRequestUser().setGender("F");
                default:
                    openrtbRequest.getBidRequestUser().setGender("O");

            }
        }
        if (mobileVideo.hasBd()) {
            openrtbRequest.getBidRequestUser().setYearOfBirth(mobileVideo.getBd());
        }

        if (mobileVideo.hasLinear()) {
            if (mobileVideo.getLinear().getNumber() == 1) {
                bidRequestImpressionVideoObjectDTO.setIsVideoLinear(1);
            } else {
                bidRequestImpressionVideoObjectDTO.setIsVideoLinear(2);
            }
        }

        if (mobileVideo.hasVamProtocol()) {
            Integer[] number = {mobileVideo.getVamProtocol().getNumber()};
            bidRequestImpressionVideoObjectDTO.setVideoBidResponseProtocol(number);
        }

        if (mobileVideo.hasWidth()) {
            bidRequestImpressionVideoObjectDTO.setWidthVideoPlayerInPixels(mobileVideo.getWidth());
        }
        if (mobileVideo.hasHeight()) {
            bidRequestImpressionVideoObjectDTO.setHeightVideoPlayerInPixels(mobileVideo.getHeight());
        }
        if (mobileVideo.hasMaxDuration()) {
            bidRequestImpressionVideoObjectDTO.setMaxDurationOfVideo(mobileVideo.getMaxDuration());
        }
        if (mobileVideo.hasMinDuration()) {
            bidRequestImpressionVideoObjectDTO.setMinimumDurationOfVideo(mobileVideo.getMinDuration());
        }

        if (mobileVideo.hasVideoAdform()) {
            switch (mobileVideo.getVideoAdform()) {
                case 0:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(0);
                case 1:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(-1);
                case 2:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(-2);
                case 4:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(1);
                default:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(1);

            }
        }

        if (mobileVideo.getMimesCount() > 0) {
            String[] mimeTypes = new String[]{};
            mimeTypes = mobileVideo.getMimesList().toArray(mimeTypes);
            bidRequestImpressionVideoObjectDTO.setMimeTypesSupported(mimeTypes);
        }

        // adformat

        if (mobileVideo.hasKeyword()) {
            openrtbRequest.getBidRequestApp().setAppKeywordsCSV(mobileVideo.getKeyword());
        }

        if (mobileVideo.getExcludedCatCount() > 0) {
            List<Integer> excluded_cat = mobileVideo.getExcludedCatList();
            Integer[] battr = new Integer[excluded_cat.size()];
            for (int i = 0; i < excluded_cat.size(); i++) {
                battr[i] = excluded_cat.get(i);
            }
            bidRequestImpressionVideoObjectDTO.setBlockedCreativeAttributes(battr);
        }

        if (mobileVideo.getExcludedAdvCount() > 0) {
            String[] excluded_adv = new String[]{};
            excluded_adv = mobileVideo.getExcludedAdvList().toArray(excluded_adv);
            openrtbRequest.setBlockedAdvertiserDomainsForBidRequest(excluded_adv);
        }
        impressionDTO.setBidRequestImpressionVideoObject(bidRequestImpressionVideoObjectDTO);

    }


}
