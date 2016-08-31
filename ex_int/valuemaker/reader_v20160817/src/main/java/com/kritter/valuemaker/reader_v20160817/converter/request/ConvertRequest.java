package com.kritter.valuemaker.reader_v20160817.converter.request;

import RTB.VamRealtimeBidding.VamRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;

public class ConvertRequest {

    static VamBidRequestParentNodeDTO openrtbRequest = new VamBidRequestParentNodeDTO();

    public static VamBidRequestParentNodeDTO convert(VamRequest request) {


        if (request == null) {
            return null;
        }
        if (request.hasId()) {
            openrtbRequest.setBidRequestId(request.getId());
        }
        if (request.hasTMax()) {
            openrtbRequest.setMaxTimeoutForBidSubmission(request.getTMax());
        }


        if (request.getVamMobile() != null && request.getVamVideo().getExcludedAdvList() != null && request.getVamVideo().getExcludedAdvList().size() > 0) {
            openrtbRequest.setBlockedAdvertiserDomainsForBidRequest((String[]) request.getVamVideo().getExcludedAdvList().toArray(new String[request.getVamVideo().getExcludedAdvCount()]));
        }
        if (request.getDisplayCount() > 0 && request.getDisplay(0).getExcludedAdvList() != null && request.getDisplay(0).getExcludedAdvList().size() > 0) {
            openrtbRequest.setBlockedAdvertiserDomainsForBidRequest((String[]) request.getDisplay(0).getExcludedAdvList().toArray(new String[request.getDisplay(0).getExcludedAdvCount()]));
        }
        if (request.getVamMobileVideo() != null && request.getVamMobileVideo().getExcludedAdvList() != null && request.getVamMobileVideo().getExcludedAdvList().size() > 0) {
            openrtbRequest.setBlockedAdvertiserDomainsForBidRequest((String[]) request.getVamMobileVideo().getExcludedAdvList().toArray(new String[request.getVamMobileVideo().getExcludedAdvCount()]));
        }

        site(request);
        device(request);
        user(request);
        impressionList(request);
        app(request);

        return openrtbRequest;
    }

    public static void site(VamRequest request) {
        BidRequestSiteDTO site = new BidRequestSiteDTO();
        if (request.hasDomain()) {
            site.setSiteDomain(request.getDomain());
        }
        if (request.hasPage()) {
            site.setSitePageURL(request.getPage());
        }
        if (request.getVamVideo().hasKeyword()) {
            site.setSiteKeywordsCSV(request.getVamVideo().getKeyword());
        }
        openrtbRequest.setBidRequestSite(site);
    }

    public static void device(VamRequest request) {
        BidRequestDeviceDTO device = new BidRequestDeviceDTO();
        if (request.hasUserAgent()) {
            device.setDeviceUserAgent(request.getUserAgent());
        }
        if (request.hasDnt()) {
            if (request.getDnt()) {
                device.setDoNotTrackDevice(0);
            } else {
                device.setDoNotTrackDevice(1);
            }
        }
        if (request.hasIp()) {
            device.setIpV4AddressClosestToDevice(request.getIp());
        }
        if (request.hasLanguage()) {
            device.setBrowserLanguage(request.getLanguage());
        }
        if (request.hasDeviceType()) {
            switch (request.getDeviceType().getNumber()) {
                case 0:
                    device.setDeviceType(2);
                    break;
                case 1:
                    device.setDeviceType(1);
                    break;
                case 2:
                    device.setDeviceType(5);
                    break;
                case 3:
                    device.setDeviceType(3);
                    break;
            }
        }

        if (request.hasVamMobile()) {
            if (request.getVamMobile().hasBrand()) {
                device.setDeviceManufacturer(request.getVamMobile().getBrand());
            }
            if (request.getVamMobile().hasModel()) {
                device.setDeviceModel(request.getVamMobile().getModel());
            }
            if (request.getVamMobile().hasOs()) {
                if (request.getVamMobile().getOs() == 1) {
                    device.setDeviceOperatingSystem("android");
                } else if (request.getVamMobile().getOs() == 2) {
                    device.setDeviceOperatingSystem("ios");
                } else {
                    device.setDeviceOperatingSystem("other");
                }

            }
            if (request.getVamMobile().hasOsVersion()) {
                device.setDeviceOperatingSystemVersion(request.getVamMobile().getOsVersion());
            }
            if (request.getVamMobile().hasImei()) {
                device.setMD5HashedDeviceId(request.getVamMobile().getImei());
            }
            if (request.getVamMobile().hasMac()) {
                device.setHashedMD5MacAddressOfDevice(request.getVamMobile().getMac());
            }
            if (request.getVamMobile().hasAid()) {
                device.setMD5HashedDevicePlatformId(request.getVamMobile().getAid());
            }
            if (request.getVamMobile().hasIDFA()) {
                device.setIfa(request.getVamMobile().getIDFA());
            }
            if (request.getVamMobile().hasScreenWidth()) {
                device.setDevicePhysicalWidthInPixels(request.getVamMobile().getScreenWidth());
            }
            if (request.getVamMobile().hasScreenHeight()) {
                device.setDevicePhysicalHeightInPixels(request.getVamMobile().getScreenHeight());
            }
            if (request.getVamMobile().hasNetwork()) {
                device.setConnectionType(request.getVamMobile().getNetwork());
            }
            if (request.getVamMobile().hasOperateId()) {
                switch (request.getVamMobile().getOperateId()) {
                    case 0:
                        device.setCarrier("UNKNOWN");
                    case 1:
                        device.setCarrier("Mobile");
                    case 2:
                        device.setCarrier("Unicom");
                    case 3:
                        device.setCarrier("Telecom");
                }
            }
        }

        if (request.hasVamMobileVideo()) {
            if (request.getVamMobileVideo().hasBrand()) {
                device.setDeviceManufacturer(request.getVamMobileVideo().getBrand());
            }
            if (request.getVamMobileVideo().hasModel()) {
                device.setDeviceModel(request.getVamMobileVideo().getModel());
            }
            if (request.getVamMobileVideo().hasOs()) {
                if (request.getVamMobileVideo().getOs() == 1) {
                    device.setDeviceOperatingSystem("android");
                } else if (request.getVamMobileVideo().getOs() == 2) {
                    device.setDeviceOperatingSystem("ios");
                } else {
                    device.setDeviceOperatingSystem("other");
                }
            }
            if (request.getVamMobileVideo().hasOsVersion()) {
                device.setDeviceOperatingSystemVersion(request.getVamMobileVideo().getOsVersion());
            }
            if (request.getVamMobileVideo().hasImei()) {
                device.setMD5HashedDeviceId(request.getVamMobileVideo().getImei());
            }
            if (request.getVamMobileVideo().hasMac()) {
                device.setHashedMD5MacAddressOfDevice(request.getVamMobileVideo().getMac());
            }
            if (request.getVamMobileVideo().hasAid()) {
                device.setMD5HashedDevicePlatformId(request.getVamMobileVideo().getAid());
            }
            if (request.getVamMobileVideo().hasIDFA()) {
                device.setIfa(request.getVamMobileVideo().getIDFA());
            }
            if (request.getVamMobileVideo().hasScreenWidth()) {
                device.setDevicePhysicalWidthInPixels(request.getVamMobileVideo().getScreenWidth());
            }
            if (request.getVamMobileVideo().hasScreenHeight()) {
                device.setDevicePhysicalHeightInPixels(request.getVamMobileVideo().getScreenHeight());
            }
            if (request.getVamMobileVideo().hasNetwork()) {
                device.setConnectionType(request.getVamMobileVideo().getNetwork());
            }
            if (request.getVamMobileVideo().hasOperateId()) {
                switch (request.getVamMobileVideo().getOperateId()) {
                    case 0:
                        device.setCarrier("UNKNOWN");
                    case 1:
                        device.setCarrier("Mobile");
                    case 2:
                        device.setCarrier("Unicom");
                    case 3:
                        device.setCarrier("Telecom");
                }
            }
        }

        openrtbRequest.setBidRequestDevice(device);
    }

    public static void user(VamRequest request) {
        BidRequestUserDTO user = new BidRequestUserDTO();
        if (request.hasCookie()) {
            user.setConsumerCustomData(request.getCookie());
        }

        if (request.hasVamMobile()) {
            if (request.getVamMobile().hasGender()) {
                if (request.getVamMobile().getGender() == 1) {
                    user.setGender("M");
                } else if (request.getVamMobile().getGender() == 2) {
                    user.setGender("F");
                } else {
                    user.setGender("O");
                }
            }
            if (request.getVamMobile().hasBd()) {
                user.setYearOfBirth(request.getVamMobile().getBd());
            }
        }

        if (request.hasVamMobileVideo()) {
            if (request.getVamMobileVideo().hasGender()) {
                if (request.getVamMobileVideo().getGender() == 1) {
                    user.setGender("M");
                } else if (request.getVamMobileVideo().getGender() == 2) {
                    user.setGender("F");
                } else {
                    user.setGender("O");
                }
            }
            if (request.getVamMobileVideo().hasBd()) {
                user.setYearOfBirth(request.getVamMobileVideo().getBd());
            }
        }

        if (request != null) {
            user.setBidRequestGeo(userGeo(request));
        }

        openrtbRequest.setBidRequestUser(user);
    }

    public static void impressionList(VamRequest request) {
        BidRequestImpressionDTO[] bidRequestImpressionArray = new BidRequestImpressionDTO[1];
        BidRequestImpressionDTO bidRequestImpression = new BidRequestImpressionDTO();

        if (request.hasVamVideo()) {
            if (request.getVamVideo().hasAdspaceId()) {
                bidRequestImpression.setAdTagOrPlacementId(request.getVamVideo().getAdspaceId() + "");
            }
            if (request.getVamVideo().hasBidfloor()) {
                bidRequestImpression.setBidFloorPrice((double) request.getVamVideo().getBidfloor());
            }
        }

        if (request.hasVamMobile()) {
            if (request.getVamMobile().hasAdspaceId()) {
                bidRequestImpression.setAdTagOrPlacementId(request.getVamMobile().getAdspaceId() + "");
            }

            if (request.getVamMobile().hasBidfloor()) {
                bidRequestImpression.setBidFloorPrice((double) request.getVamMobile().getBidfloor());
            }
            if (request.getVamMobile().hasFullScreen()) {
                if (request.getVamMobile().getFullScreen()) {
                    bidRequestImpression.setIsAdInterstitial(1);
                } else {
                    bidRequestImpression.setIsAdInterstitial(0);
                }
            }
        }

        if (request.hasVamMobileVideo()) {
            if (request.getVamMobileVideo().hasAdspaceId()) {
                bidRequestImpression.setAdTagOrPlacementId(request.getVamMobileVideo().getAdspaceId() + "");
            }
            if (request.getVamMobileVideo().hasBidfloor()) {
                bidRequestImpression.setBidFloorPrice((double) request.getVamMobileVideo().getBidfloor());
            }
        }

        if (request.getDisplayCount() > 0) {
            if (request.getDisplay(0).hasAdspaceId()) {
                bidRequestImpression.setAdTagOrPlacementId(request.getDisplay(0).getAdspaceId() + "");
            }
            if (request.getDisplay(0).hasBidfloor()) {
                bidRequestImpression.setBidFloorPrice((double) request.getDisplay(0).getBidfloor());
            }
        }

        if (request != null) {
            bidRequestImpression.setBidRequestImpressionVideoObject(impressionVideo(request));
        }
        if (request != null) {
            bidRequestImpression.setBidRequestImpressionBannerObject(impressionBanner(request));
        }
        bidRequestImpressionArray[0] = bidRequestImpression;
        openrtbRequest.setBidRequestImpressionArray(bidRequestImpressionArray);
    }

    public static void app(VamRequest request) {
        BidRequestAppDTO app = new BidRequestAppDTO();
        if (request.hasVamMobile()) {
            if (request.getVamMobile().hasPgn()) {
                app.setApplicationBundleName(request.getVamMobile().getPgn());
            }
            if (request.getVamMobile().hasAppName()) {
                app.setApplicationName(request.getVamMobile().getAppName());
            }
        }
        if (request.hasVamMobileVideo()) {
            if (request.getVamMobileVideo().hasPgn()) {
                app.setApplicationBundleName(request.getVamMobileVideo().getPgn());
            }
            if (request.getVamMobileVideo().hasAppName()) {
                app.setApplicationName(request.getVamMobileVideo().getAppName());
            }
            if (request.getVamMobileVideo().hasKeyword()) {
                app.setAppKeywordsCSV(request.getVamMobileVideo().getKeyword());
            }
        }

        openrtbRequest.setBidRequestApp(app);
    }

    public static BidRequestImpressionVideoObjectDTO impressionVideo(VamRequest request) {
        BidRequestImpressionVideoObjectDTO video = new BidRequestImpressionVideoObjectDTO();

        if (request.hasVamMobile()) {
            if (request.getVamVideo().hasLinear()) {
                if (request.getVamVideo().getLinear().getNumber() == 0) {
                    video.setIsVideoLinear(1);
                } else {
                    video.setIsVideoLinear(2);
                }
            }
            if (request.getVamVideo().hasVamProtocol()) {
                Integer[] number = {request.getVamVideo().getVamProtocol().getNumber() + 1};
                video.setVideoBidResponseProtocol(number);
            }
            if (request.getVamVideo().hasWidth()) {
                video.setWidthVideoPlayerInPixels(request.getVamVideo().getWidth());
            }
            if (request.getVamVideo().hasHeight()) {
                video.setHeightVideoPlayerInPixels(request.getVamVideo().getHeight());
            }
            if (request.getVamVideo().hasMaxDuration()) {
                video.setMaxDurationOfVideo(request.getVamVideo().getMaxDuration());
            }
            if (request.getVamVideo().hasMinDuration()) {
                video.setMinimumDurationOfVideo(request.getVamVideo().getMinDuration());
            }
            if (request.getVamVideo().hasVideoAdform()) {
                video.setStartDelayInSeconds(request.getVamVideo().getVideoAdform());
            }
        }

        if (request.hasVamMobileVideo()) {
            if (request.getVamMobileVideo().hasLinear()) {
                if (request.getVamMobileVideo().getLinear().getNumber() == 0) {
                    video.setIsVideoLinear(1);
                } else {
                    video.setIsVideoLinear(2);
                }
            }
            if (request.getVamMobileVideo().hasVamProtocol()) {
                Integer[] number = {request.getVamMobileVideo().getVamProtocol().getNumber() + 1};
                video.setVideoBidResponseProtocol(number);
            }
            if (request.getVamMobileVideo().hasWidth()) {
                video.setWidthVideoPlayerInPixels(request.getVamMobileVideo().getWidth());
            }
            if (request.getVamMobileVideo().hasHeight()) {
                video.setHeightVideoPlayerInPixels(request.getVamMobileVideo().getHeight());
            }
            if (request.getVamMobileVideo().hasMaxDuration()) {
                video.setMaxDurationOfVideo(request.getVamMobileVideo().getMaxDuration());
            }
            if (request.getVamMobileVideo().hasMinDuration()) {
                video.setMinimumDurationOfVideo(request.getVamMobileVideo().getMinDuration());
            }
            if (request.getVamMobileVideo().hasVideoAdform()) {
                video.setStartDelayInSeconds(request.getVamMobileVideo().getVideoAdform());
            }
            if (request.getVamVideo().getMimesList() != null && request.getVamVideo().getMimesList().size() > 0) {
                video.setMimeTypesSupported((String[]) request.getVamVideo().getMimesList().toArray(new String[request.getVamVideo().getMimesCount()]));
            }
            if (request.getVamMobileVideo().getMimesList() != null && request.getVamMobileVideo().getMimesList().size() > 0) {
                video.setMimeTypesSupported((String[]) request.getVamMobileVideo().getMimesList().toArray(new String[request.getVamMobileVideo().getMimesCount()]));
            }
            if (request.getVamVideo().getExcludedCatList() != null && request.getVamVideo().getExcludedCatList().size() > 0) {
                video.setBlockedCreativeAttributes((Integer[]) request.getVamVideo().getExcludedCatList().toArray(new Integer[request.getVamVideo().getExcludedCatCount()]));
            }
            if (request.getVamMobileVideo().getExcludedCatList() != null && request.getVamMobileVideo().getExcludedCatList().size() > 0) {
                video.setBlockedCreativeAttributes((Integer[]) request.getVamMobileVideo().getExcludedCatList().toArray(new Integer[request.getVamMobileVideo().getExcludedCatCount()]));
            }
        }

        return video;
    }

    public static BidRequestImpressionBannerObjectDTO impressionBanner(VamRequest request) {
        BidRequestImpressionBannerObjectDTO banner = new BidRequestImpressionBannerObjectDTO();
        if (request.getDisplayCount() > 0) {
            if (request.getDisplay(0).hasHeight()) {
                banner.setBannerHeightInPixels(request.getDisplay(0).getHeight());
            }
            if (request.getDisplay(0).hasWidth()) {
                banner.setBannerWidthInPixels(request.getDisplay(0).getWidth());
            }
        }

        if (request.hasVamMobile()) {
            if (request.getVamMobile().hasHeight()) {
                banner.setBannerHeightInPixels(request.getVamMobile().getHeight());
            }
            if (request.getVamMobile().hasWidth()) {
                banner.setBannerWidthInPixels(request.getVamMobile().getWidth());
            }
        }

        return banner;
    }

    public static BidRequestGeoDTO userGeo(VamRequest request) {
        BidRequestGeoDTO geo = new BidRequestGeoDTO();
        if (request.hasVamMobile()) {
            if (request.getVamMobile().getCorner().hasLatitude()) {
                geo.setGeoLatitude(request.getVamMobile().getCorner().getLatitude());
            }
            if (request.getVamMobile().getCorner().hasLongitude()) {
                geo.setGeoLongitude(request.getVamMobile().getCorner().getLongitude());
            }
        }
        if (request.hasVamMobileVideo()) {
            if (request.getVamMobileVideo().getCorner().hasLatitude()) {
                geo.setGeoLatitude(request.getVamMobileVideo().getCorner().getLatitude());
            }
            if (request.getVamMobileVideo().getCorner().hasLongitude()) {
                geo.setGeoLongitude(request.getVamMobileVideo().getCorner().getLongitude());
            }
        }

        return geo;
    }

}
