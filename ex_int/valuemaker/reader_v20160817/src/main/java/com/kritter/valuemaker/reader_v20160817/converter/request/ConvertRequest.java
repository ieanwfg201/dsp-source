package com.kritter.valuemaker.reader_v20160817.converter.request;

import RTB.VamRealtimeBidding.VamRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;

import java.util.ArrayList;
import java.util.List;

public class ConvertRequest {

    public static VamBidRequestParentNodeDTO convert(VamRequest request) {

        if (request == null) {
            return null;
        }

        VamBidRequestParentNodeDTO bidRequestParentNodeDTO = new VamBidRequestParentNodeDTO();

        BidRequestImpressionDTO bidRequestImpressionDTO = new BidRequestImpressionDTO();
        UUIDGenerator uuidGenerator = new UUIDGenerator();
        bidRequestImpressionDTO.setBidRequestImpressionId(uuidGenerator.generateUniversallyUniqueIdentifier().toString());

        bidRequestParentNodeDTO.setBidRequestImpressionArray(new BidRequestImpressionDTO[]{bidRequestImpressionDTO});

        BidRequestSiteDTO bidRequestSiteDTO = new BidRequestSiteDTO();
        bidRequestParentNodeDTO.setBidRequestSite(bidRequestSiteDTO);

        BidRequestAppDTO bidRequestAppDTO = new BidRequestAppDTO();
        bidRequestParentNodeDTO.setBidRequestApp(bidRequestAppDTO);

        BidRequestDeviceDTO bidRequestDeviceDTO = new BidRequestDeviceDTO();
        bidRequestParentNodeDTO.setBidRequestDevice(bidRequestDeviceDTO);

        BidRequestUserDTO bidRequestUserDTO = new BidRequestUserDTO();
        bidRequestParentNodeDTO.setBidRequestUser(bidRequestUserDTO);


        if (request.hasId()) {
            bidRequestParentNodeDTO.setBidRequestId(request.getId());
        }
        if (request.hasTMax()) {
            bidRequestParentNodeDTO.setMaxTimeoutForBidSubmission(request.getTMax());
        }

        if (request.hasCookie()) {
            bidRequestParentNodeDTO.getBidRequestUser().setConsumerCustomData(request.getCookie());
        }
        if (request.hasUserAgent()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDeviceUserAgent(request.getUserAgent());
        }

        if (request.hasDnt()) {
            if (request.getDnt()) {
                bidRequestParentNodeDTO.getBidRequestDevice().setDoNotTrackDevice(1);
            } else {
                bidRequestParentNodeDTO.getBidRequestDevice().setDoNotTrackDevice(0);
            }
        }

        if (request.hasIp()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setIpV4AddressClosestToDevice(request.getIp());
        }

        if (request.hasLanguage()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setBrowserLanguage(request.getLanguage());
        }

        if (request.hasDeviceType()) {
            switch (request.getDeviceType().getNumber()) {
                case 1:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceType(2);
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceType(1);
                    break;
                case 3:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceType(1);
                    break;
                case 4:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceType(3);
                    break;
            }
        }


        if (request.hasMediaId()) {
            bidRequestParentNodeDTO.getBidRequestSite().setSiteIdOnExchange(request.getDomain());
        }
        if (request.hasDomain()) {
            bidRequestParentNodeDTO.getBidRequestSite().setSiteDomain(request.getDomain());
        }
        if (request.hasPage()) {
            bidRequestParentNodeDTO.getBidRequestSite().setSitePageURL(request.getPage());
        }
        if (request.hasReferer()) {
            bidRequestParentNodeDTO.getBidRequestSite().setRefererURL(request.getReferer());
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
            display(bidRequestParentNodeDTO, request.getDisplay(0));
        } else if (request.hasVamVideo()) {
            video(bidRequestParentNodeDTO, request.getVamVideo());
        } else if (request.hasVamMobile()) {
            mobile(bidRequestParentNodeDTO, request.getVamMobile());
        } else if (request.hasVamMobileVideo()) {
            mobileVideo(bidRequestParentNodeDTO, request.getVamMobileVideo());
        } else {
            return bidRequestParentNodeDTO;
        }


        bidRequestParentNodeDTO.setExtensionObject(request);

        return bidRequestParentNodeDTO;
    }

    private static void display(VamBidRequestParentNodeDTO bidRequestParentNodeDTO, VamRequest.Display display) {
        bidRequestParentNodeDTO.setSitePlatform(SITE_PLATFORM.WAP);
        BidRequestImpressionDTO impressionDTO = bidRequestParentNodeDTO.getBidRequestImpressionArray()[0];
        BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObjectDTO = new BidRequestImpressionBannerObjectDTO();
        if (display.hasAdspaceId()) {
            impressionDTO.setAdTagOrPlacementId(String.valueOf(display.getAdspaceId()));
        }
        if (display.hasBidfloor()) {
//            impressionDTO.setBidFloorPrice((double) display.getBidfloor() / 100);
        }
        //screen_level
        //id固定为1
        if (display.hasWidth()) {
            bidRequestImpressionBannerObjectDTO.setBannerWidthInPixels(display.getWidth());
        }
        if (display.hasHeight()) {
            bidRequestImpressionBannerObjectDTO.setBannerHeightInPixels(display.getHeight());
        }
        //adformat,adform

        if (display.getExcludedCatCount() > 0) {
            List<Integer> excluded_cat = display.getExcludedCatList();
            Short[] battr = new Short[excluded_cat.size()];
            for (int i = 0; i < excluded_cat.size(); i++) {
                battr[i] = excluded_cat.get(i).shortValue();
            }
            bidRequestImpressionBannerObjectDTO.setBlockedCreativeAttributes(battr);
            bidRequestParentNodeDTO.setBattr(display.getExcludedCatList());
        }

        if (display.getExcludedAdvCount() > 0) {
            String[] excluded_adv = new String[]{};
            excluded_adv = display.getExcludedAdvList().toArray(excluded_adv);
            bidRequestParentNodeDTO.setBlockedAdvertiserDomainsForBidRequest(excluded_adv);
        }

        impressionDTO.setBidRequestImpressionBannerObject(bidRequestImpressionBannerObjectDTO);

    }

    private static void mobile(VamBidRequestParentNodeDTO bidRequestParentNodeDTO, VamRequest.Mobile mobile) {
        bidRequestParentNodeDTO.setSitePlatform(SITE_PLATFORM.APP);
        BidRequestImpressionDTO impressionDTO = bidRequestParentNodeDTO.getBidRequestImpressionArray()[0];
        BidRequestImpressionBannerObjectDTO bidRequestImpressionBannerObjectDTO = new BidRequestImpressionBannerObjectDTO();
        if (mobile.hasAdspaceId()) {
            impressionDTO.setAdTagOrPlacementId(String.valueOf(mobile.getAdspaceId()));
        }
        if (mobile.hasBidfloor()) {
//            impressionDTO.setBidFloorPrice((double) mobile.getBidfloor() / 100);
        }
        if (mobile.hasWidth()) {
            bidRequestImpressionBannerObjectDTO.setBannerWidthInPixels(mobile.getWidth());
        }
        if (mobile.hasHeight()) {
            bidRequestImpressionBannerObjectDTO.setBannerHeightInPixels(mobile.getHeight());
        }
        //adformat

        if (mobile.hasBrand()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDeviceManufacturer(mobile.getBrand());
        }
        if (mobile.hasModel()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDeviceModel(mobile.getModel());
        }

        if (mobile.hasOs()) {
            switch (mobile.getOs()) {
                case 0:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystem("Other");
                    break;
                case 1:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystem("iOS");
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystem("Android");
                    break;
                default:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystem("Other");
                    break;
            }
        }
        if (mobile.hasOsVersion()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystemVersion(mobile.getOsVersion());
        }


        if (mobile.hasImei()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setMD5HashedDeviceId(mobile.getImei());
        }

        if (mobile.hasMac()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setHashedMD5MacAddressOfDevice(mobile.getMac());
        }
        if (mobile.hasAid()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setMD5HashedDevicePlatformId(mobile.getAid());
        }
        if (mobile.hasAaid()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setAaidMd5(mobile.getAaid());
        }
        if (mobile.hasIDFA()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setIfa(mobile.getIDFA());
        }
        if (mobile.hasOpenUDID()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setOpenUDIDMd5(mobile.getOpenUDID());
        }
        if (mobile.hasSource() && mobile.getSource() != 1) {
            bidRequestParentNodeDTO.setSitePlatform(SITE_PLATFORM.WAP);
        }
        if (mobile.hasPgn()) {
            bidRequestParentNodeDTO.getBidRequestApp().setApplicationBundleName(mobile.getPgn());
        }
        if (mobile.hasAppName()) {
            bidRequestParentNodeDTO.getBidRequestApp().setApplicationName(mobile.getAppName());
        }

        if (mobile.hasScreenWidth()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDevicePhysicalWidthInPixels(mobile.getScreenWidth());
        }
        if (mobile.hasScreenHeight()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDevicePhysicalHeightInPixels(mobile.getScreenHeight());
        }

        if (mobile.hasNetwork()) {
            switch (mobile.getNetwork()) {
                case 0:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(0);
                    break;
                case 1:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(2);
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(4);
                    break;
                case 3:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(5);
                    break;
                case 4:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(6);
                    break;
                default:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(0);
                    break;
            }
        }

        if (mobile.hasOperateId()) {
            switch (mobile.getOperateId()) {
                case 0:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("5");
                    break;
                case 1:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("1");
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("2");
                    break;
                case 3:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("3");
                    break;
                default:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("4");
                    break;
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
            bidRequestParentNodeDTO.getBidRequestDevice().setGeoObject(bidRequestGeoDTO);
        }

        if (mobile.hasFullScreen()) {
            if (mobile.getFullScreen()) {
                impressionDTO.setIsAdInterstitial(1);
            } else {
                impressionDTO.setIsAdInterstitial(0);
            }
        }
        //ad_location
        if (mobile.hasAppCategory()) {
            bidRequestParentNodeDTO.getBidRequestApp().setContentCategoriesApplication(new String[]{String.valueOf(mobile.getAppCategory())});
        }
        //adform
        //mpn
        if (mobile.hasGender()) {
            switch (mobile.getGender()) {
                case 0:
                    bidRequestParentNodeDTO.getBidRequestUser().setGender("O");
                    break;
                case 1:
                    bidRequestParentNodeDTO.getBidRequestUser().setGender("M");
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestUser().setGender("F");
                    break;
                default:
                    bidRequestParentNodeDTO.getBidRequestUser().setGender("O");
                    break;

            }
        }
        if (mobile.hasBd()) {
            bidRequestParentNodeDTO.getBidRequestUser().setYearOfBirth(mobile.getBd());
        }
        //screen_level

        impressionDTO.setBidRequestImpressionBannerObject(bidRequestImpressionBannerObjectDTO);

    }

    private static void video(VamBidRequestParentNodeDTO bidRequestParentNodeDTO, VamRequest.Video video) {
        bidRequestParentNodeDTO.setSitePlatform(SITE_PLATFORM.WAP);
        BidRequestImpressionDTO impressionDTO = bidRequestParentNodeDTO.getBidRequestImpressionArray()[0];
        BidRequestImpressionVideoObjectDTO bidRequestImpressionVideoObjectDTO = new BidRequestImpressionVideoObjectDTO();
        if (video.hasAdspaceId()) {
            impressionDTO.setAdTagOrPlacementId(String.valueOf(video.getAdspaceId()));
        }
        if (video.hasBidfloor()) {
//            impressionDTO.setBidFloorPrice((double) video.getBidfloor() / 100);
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
                    break;
                case 1:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(-1);
                    break;
                case 2:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(-2);
                    break;
                case 4:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(1);
                    break;
                default:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(1);
                    break;

            }
        }

        if (video.getVideoAdformatCount() > 0) {
            List<VamRequest.Video.ADFORMAT> adformats = video.getVideoAdformatList();
            if (adformats != null && adformats.size() != 0) {
                String[] mimeTypes = new String[]{};
                List<String> list = new ArrayList<String>();
                for (VamRequest.Video.ADFORMAT af : adformats) {
                    switch (af.getNumber()) {
                        case 3:
                            list.add("video/x-swx-shockwave-flash");
                            break;
                        case 5:
                            list.add("video/x-flv");
                            break;
                        case 6:
                            list.add("video/mp4");
                            break;
                    }
                }
                mimeTypes = list.toArray(mimeTypes);
                bidRequestImpressionVideoObjectDTO.setMimeTypesSupported(mimeTypes);
            }
        }

        if (video.hasKeyword()) {
            bidRequestParentNodeDTO.getBidRequestApp().setAppKeywordsCSV(video.getKeyword());
        }
        if (video.getExcludedCatCount() > 0) {
            List<Integer> excluded_cat = video.getExcludedCatList();
            Integer[] battr = new Integer[excluded_cat.size()];
            for (int i = 0; i < excluded_cat.size(); i++) {
                battr[i] = excluded_cat.get(i);
            }
            bidRequestImpressionVideoObjectDTO.setBlockedCreativeAttributes(battr);
            bidRequestParentNodeDTO.setBattr(video.getExcludedCatList());
        }

        if (video.getExcludedAdvCount() > 0) {
            String[] excluded_adv = new String[]{};
            excluded_adv = video.getExcludedAdvList().toArray(excluded_adv);
            bidRequestParentNodeDTO.setBlockedAdvertiserDomainsForBidRequest(excluded_adv);
        }
//        ad_tech
        impressionDTO.setBidRequestImpressionVideoObject(bidRequestImpressionVideoObjectDTO);

    }

    private static void mobileVideo(VamBidRequestParentNodeDTO bidRequestParentNodeDTO, VamRequest.Mobile_Video mobileVideo) {
        bidRequestParentNodeDTO.setSitePlatform(SITE_PLATFORM.APP);
        BidRequestImpressionDTO impressionDTO = bidRequestParentNodeDTO.getBidRequestImpressionArray()[0];
        BidRequestImpressionVideoObjectDTO bidRequestImpressionVideoObjectDTO = new BidRequestImpressionVideoObjectDTO();
        if (mobileVideo.hasAdspaceId()) {
            impressionDTO.setAdTagOrPlacementId(String.valueOf(mobileVideo.getAdspaceId()));
        }
        if (mobileVideo.hasBidfloor()) {
//            impressionDTO.setBidFloorPrice((double) mobileVideo.getBidfloor() / 100);
        }
        //adformat

        if (mobileVideo.hasBrand()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDeviceManufacturer(mobileVideo.getBrand());
        }
        if (mobileVideo.hasModel()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDeviceModel(mobileVideo.getModel());
        }

        if (mobileVideo.hasOs()) {
            switch (mobileVideo.getOs()) {
                case 0:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystem("Other");
                    break;
                case 1:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystem("iOS");
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystem("Android");
                    break;
                default:
                    bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystem("Other");
                    break;
            }
        }
        if (mobileVideo.hasOsVersion()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDeviceOperatingSystemVersion(mobileVideo.getOsVersion());
        }

        if (mobileVideo.hasImei()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setMD5HashedDeviceId(mobileVideo.getImei());
        }

        if (mobileVideo.hasMac()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setHashedMD5MacAddressOfDevice(mobileVideo.getMac());
        }
        if (mobileVideo.hasAid()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setMD5HashedDevicePlatformId(mobileVideo.getAid());
        }
        if (mobileVideo.hasAaid()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setAaidMd5(mobileVideo.getAaid());
        }
        if (mobileVideo.hasIDFA()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setIfa(mobileVideo.getIDFA());
        }
        if (mobileVideo.hasOpenUDID()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setOpenUDIDMd5(mobileVideo.getIDFA());
        }
        if (mobileVideo.hasSource() && mobileVideo.getSource() != 1) {
            bidRequestParentNodeDTO.setSitePlatform(SITE_PLATFORM.WAP);
        }
        if (mobileVideo.hasPgn()) {
            bidRequestParentNodeDTO.getBidRequestApp().setApplicationBundleName(mobileVideo.getPgn());
        }
        if (mobileVideo.hasAppName()) {
            bidRequestParentNodeDTO.getBidRequestApp().setApplicationName(mobileVideo.getAppName());
        }

        if (mobileVideo.hasScreenWidth()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDevicePhysicalWidthInPixels(mobileVideo.getScreenWidth());
        }
        if (mobileVideo.hasScreenHeight()) {
            bidRequestParentNodeDTO.getBidRequestDevice().setDevicePhysicalHeightInPixels(mobileVideo.getScreenHeight());
        }

        if (mobileVideo.hasNetwork()) {
            switch (mobileVideo.getNetwork()) {
                case 0:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(0);
                    break;
                case 1:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(2);
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(4);
                    break;
                case 3:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(5);
                    break;
                case 4:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(6);
                    break;
                default:
                    bidRequestParentNodeDTO.getBidRequestDevice().setConnectionType(0);
                    break;
            }
        }

        if (mobileVideo.hasOperateId()) {
            switch (mobileVideo.getOperateId()) {
                case 0:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("5");
                    break;
                case 1:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("1");
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("2");
                    break;
                case 3:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("3");
                    break;
                default:
                    bidRequestParentNodeDTO.getBidRequestDevice().setCarrier("4");
                    break;
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
            bidRequestParentNodeDTO.getBidRequestDevice().setGeoObject(bidRequestGeoDTO);
        }
        //mpn

        if (mobileVideo.hasGender()) {
            switch (mobileVideo.getGender()) {
                case 0:
                    bidRequestParentNodeDTO.getBidRequestUser().setGender("O");
                    break;
                case 1:
                    bidRequestParentNodeDTO.getBidRequestUser().setGender("M");
                    break;
                case 2:
                    bidRequestParentNodeDTO.getBidRequestUser().setGender("F");
                    break;
                default:
                    bidRequestParentNodeDTO.getBidRequestUser().setGender("O");
                    break;

            }
        }
        if (mobileVideo.hasBd()) {
            bidRequestParentNodeDTO.getBidRequestUser().setYearOfBirth(mobileVideo.getBd());
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
                    break;
                case 1:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(-1);
                    break;
                case 2:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(-2);
                    break;
                case 4:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(1);
                    break;
                default:
                    bidRequestImpressionVideoObjectDTO.setStartDelayInSeconds(1);
                    break;

            }
        }

        // mines
        if (mobileVideo.getAdformatCount() > 0) {
            List<VamRequest.Mobile_Video.ADFORMAT> adformats = mobileVideo.getAdformatList();
            if (adformats != null && adformats.size() != 0) {
                String[] mimeTypes = new String[]{};
                List<String> list = new ArrayList<String>();
                for (VamRequest.Mobile_Video.ADFORMAT af : adformats) {
                    switch (af.getNumber()) {
                        case 3:
                            list.add("video/x-swx-shockwave-flash");
                            break;
                        case 5:
                            list.add("video/x-flv");
                            break;
                        case 6:
                            list.add("video/mp4");
                            break;
                    }
                }
                mimeTypes = list.toArray(mimeTypes);
                bidRequestImpressionVideoObjectDTO.setMimeTypesSupported(mimeTypes);
            }
        }


        if (mobileVideo.hasKeyword()) {
            bidRequestParentNodeDTO.getBidRequestApp().setAppKeywordsCSV(mobileVideo.getKeyword());
        }

        if (mobileVideo.getExcludedCatCount() > 0) {
            List<Integer> excluded_cat = mobileVideo.getExcludedCatList();
            Integer[] battr = new Integer[excluded_cat.size()];
            for (int i = 0; i < excluded_cat.size(); i++) {
                battr[i] = excluded_cat.get(i);
            }
            bidRequestImpressionVideoObjectDTO.setBlockedCreativeAttributes(battr);
            bidRequestParentNodeDTO.setBattr(mobileVideo.getExcludedCatList());
        }

        if (mobileVideo.getExcludedAdvCount() > 0) {
            String[] excluded_adv = new String[]{};
            excluded_adv = mobileVideo.getExcludedAdvList().toArray(excluded_adv);
            bidRequestParentNodeDTO.setBlockedAdvertiserDomainsForBidRequest(excluded_adv);
        }
        impressionDTO.setBidRequestImpressionVideoObject(bidRequestImpressionVideoObjectDTO);

    }


}
