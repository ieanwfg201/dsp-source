package com.kritter.bidrequest.entity.common.openrtbversion2_3.example;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionVideoObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO;
public class ExampleVideoRequest {
	private BidRequestImpressionVideoObjectDTO getVideo(){
		BidRequestImpressionVideoObjectDTO entity = new BidRequestImpressionVideoObjectDTO();
		entity.setWidthVideoPlayerInPixels(300); // in pixels
		entity.setHeightVideoPlayerInPixels(250);// in pixels
		Integer[] videoBidResponseProtocol =new Integer[4]; //	 1-> VAST 1.0,2->VAST 2.0,3->VAST 3.0,4->VAST 1.0 Wrapper,5-> VAST 2.0 Wrapper, 6->VAST 3.0 Wrapper
		videoBidResponseProtocol[0]=2;videoBidResponseProtocol[1]=3;videoBidResponseProtocol[2]=5;videoBidResponseProtocol[3]=6;
		entity.setVideoBidResponseProtocol(videoBidResponseProtocol);
		String[] mimeTypesSupported = new String[1];
		mimeTypesSupported[0] = "video/mp4";
		entity.setMimeTypesSupported(mimeTypesSupported);
		entity.setStartDelayInSeconds(-1);
		entity.setIsVideoLinear(1);
		Integer[] supportedAPIFrameworkList =new Integer[1];
		supportedAPIFrameworkList[0]=2;
		entity.setSupportedAPIFrameworkList(supportedAPIFrameworkList);
		/**
		 * RECOMMENDED
		entity.setPlaybackMethods(playbackMethods);
		entity.setMaxDurationOfVideo(maxDurationOfVideo);
		entity.setMinimumDurationOfVideo(minimumDurationOfVideo);
		entity.setMaximumExtendedVideoAdDuration(maximumExtendedVideoAdDuration);
		entity.setAdPosition(adPosition);
		entity.setContentBoxingAllowed(contentBoxingAllowed);
		entity.setSupportedDeliveryMethods(supportedDeliveryMethods);
		entity.setCompanionBannerObjects(companionBannerObjects);
		entity.setCompanionTypes(companionTypes);
		 */
		/*Good To Have
		entity.setBlockedCreativeAttributes(blockedCreativeAttributes);
		entity.setMinimumBitRateVideoInKbps(minimumBitRateVideoInKbps);
		entity.setMaximumBitRateVideoInKbps(maximumBitRateVideoInKbps);
		entity.setSequenceCreativeInMultiImpressionsForSameBidRequest(sequenceCreativeInMultiImpressionsForSameBidRequest);
		entity.setExtensionObject(extensionObject);
		 */
		return entity;
	}
	private BidRequestImpressionDTO getImpression(){
		BidRequestImpressionDTO entity = new BidRequestImpressionDTO();
		/** Kritter set these
 		entity.setBidRequestImpressionId(bidRequestImpressionId);
		entity.setBidFloorCurrency(bidFloorCurrency);
		*/
		entity.setBidFloorPrice(0.01); //in CPM
		entity.setBidRequestImpressionVideoObject(getVideo());
		/*
		 * Not Required fo bthis type of request
		entity.setBidRequestPMPDTO(bidRequestPMPDTO);
		entity.setBidRequestImpressionBannerObject(bidRequestImpressionBannerObject);
		entity.setBidRequestImpressionNativeObjectDTO(bidRequestImpressionNativeObjectDTO);
		*/
		/*Good to have
		entity.setAdTagOrPlacementId(adTagOrPlacementId);
		entity.setDisplayManagerForAdRendering(displayManagerForAdRendering);
		entity.setDisplayManagerVersion(displayManagerVersion);
		entity.setIframeBusterNames(iframeBusterNames);
		entity.setIsAdInterstitial(isAdInterstitial);
		entity.setSecure(secure);
		entity.setExtensionObject(extensionObject);
		*/
		return entity;
	}
	private BidRequestImpressionDTO[] getImpressions(){
		/*Only one element to be populated */ 
		BidRequestImpressionDTO[] entities = new BidRequestImpressionDTO[1];
		entities[0] = getImpression();
		return entities;
	}
	private BidRequestUserDTO getUser(){
		BidRequestUserDTO entity = new BidRequestUserDTO();
		entity.setUniqueConsumerIdOnExchange("UNIQUE ID");
		/** Will be populated by Kritter after user sync
		entity.setUniqueConsumerIdMappedForBuyer(uniqueConsumerIdMappedForBuyer);
		*/
		/* Good To have
		entity.setBidRequestGeo(bidRequestGeo);
		entity.setBidRequestUserData(bidRequestUserData);
		entity.setConsumerCustomData(consumerCustomData);
		entity.setExtensionObject(extensionObject);
		entity.setGender(gender);
		entity.setYearOfBirth(yearOfBirth);
		*/
		return entity;
	}
	private BidRequestSiteDTO getSite(){
		BidRequestSiteDTO entity = new BidRequestSiteDTO();
		entity.setSiteIdOnExchange("UNIQUESITEIDOFTHESITE");
		entity.setSiteName("SITENAME");
		/** RECOMMENDED 
		entity.setSiteDomain(siteDomain);
		entity.setRefererURL(refererURL);
		entity.setSitePageURL(sitePageURL);
		*/
		/** GOOD TO HAVE
		entity.setBidRequestContent(bidRequestContent);
		entity.setBidRequestPublisher(bidRequestPublisher);
		entity.setContentCategoriesForSite(contentCategoriesForSite);
		entity.setContentCategoriesSubsectionSite(contentCategoriesSubsectionSite);
		entity.setExtensionObject(extensionObject);
		entity.setIsMobileOptimizedSignal(isMobileOptimizedSignal);
		entity.setPageContentCategories(pageContentCategories);
		entity.setPrivacyPolicy(privacyPolicy);
		entity.setSearchStringCausingNavigation(searchStringCausingNavigation);
		entity.setSiteKeywordsCSV(siteKeywordsCSV);
		*/
		return entity;
	}
	private BidRequestDeviceDTO getDevice(){
		BidRequestDeviceDTO entity = new BidRequestDeviceDTO();
		/*one of ipv4 or v6 should be set*/
		entity.setIpV4AddressClosestToDevice("49.207.50.44");
		//entity.setIpV6Address());
		entity.setDeviceUserAgent("Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Safari/537.36");
		entity.setDoesDeviceSupportJavascript(1);
		
		/** ********RECOMMENDED********** to populate below 
		entity.setHashedMD5MacAddressOfDevice(hashedMD5MacAddressOfDevice);
		entity.setHashedSHA1MacAddressOfDevice(hashedSHA1MacAddressOfDevice);
		entity.setIfa(ifa);
		entity.setMD5HashedDeviceId(MD5HashedDeviceId);
		entity.setMD5HashedDevicePlatformId(MD5HashedDevicePlatformId);
		entity.setSHA1HashedDeviceId(SHA1HashedDeviceId);
		entity.setSHA1HashedDevicePlatformId(SHA1HashedDevicePlatformId);
		*/

		/* if below populated Kritter just passes itson
		entity.setBrowserLanguage(browserLanguage);
		entity.setCarrier(carrier);
		entity.setConnectionType(connectionType);
		entity.setDeviceHardwareVersion(deviceHardwareVersion);
		entity.setDeviceManufacturer(deviceManufacturer);
		entity.setDeviceModel(deviceModel);
		entity.setDeviceOperatingSystem(deviceOperatingSystem);
		entity.setDeviceOperatingSystemVersion(deviceOperatingSystemVersion);
		entity.setDevicePhysicalHeightInPixels(devicePhysicalHeightInPixels);
		entity.setDevicePhysicalWidthInPixels(devicePhysicalWidthInPixels);
		entity.setDeviceType(deviceType);
		entity.setDoNotTrackDevice(doNotTrackDevice);
		entity.setExtensionObject(extensionObject);
		entity.setFlashVersionDetected(flashVersionDetected);
		entity.setGeoObject(geoObject);
		entity.setLimitAdTracking(limitAdTracking);
		entity.setPhysicalToIndependentPixelsRatio(physicalToIndependentPixelsRatio);
		entity.setScreenSizeInPixelsPerLinearInch(screenSizeInPixelsPerLinearInch);
		*/
		return entity;
	}
	public BidRequestParentNodeDTO createVideoRequest(){
		BidRequestParentNodeDTO parent = new BidRequestParentNodeDTO();
		parent.setBidRequestDevice(getDevice());
		parent.setBidRequestImpressionArray(getImpressions());
		// One of Site or APP should be populated 
		//parent.setBidRequestApp(bidRequestApp);
		parent.setBidRequestSite(getSite());
		parent.setBidRequestUser(getUser());
		/** Can Be set and Kritter will Honor
		parent.setBlockedAdvertiserDomainsForBidRequest(blockedAdvertiserDomainsForBidRequest);
		parent.setBlockedAdvertiserCategoriesForBidRequest(blockedAdvertiserCategoriesForBidRequest);
		parent.setBidRequestRegsDTO(bidRequestRegsDTO);
		*/
		/*in miliseconds if not Kritter sets 100*/
		parent.setMaxTimeoutForBidSubmission(100);

		/**	  
 		FOLLOWING NOT REQUIRED and KRITTER sets a DEFAULT VALUES
  		
  		parent.setAllowedCurrencies(allowedCurrencies); (USD only)
		parent.setAuctionType(auctionType);
		parent.setBidRequestId(bidRequestId); Kritter creates this ID

		 */
		/*	  
 		FOLLOWING NOT REQUIRED for this type of request
  				parent.setBidderSeatIds(bidderSeatIds);
		 */
		/*	  
 		FOLLOWING NOT SUPPORTED and nothing is done by KRITTER to these fields

  		parent.setExtensionObject(extensionObject);
  		parent.setIsBidRequestTestAndNotBillable(isBidRequestTestAndNotBillable); always billable
  		parent.setAllImpressions(allImpressions);

		 */
		return parent;
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
	}
    public JsonNode toJson(BidRequestParentNodeDTO b){
        JsonNode jsonNode = objectMapper.valueToTree(b);
        return jsonNode;
    }

	
	public static void main(String args[]){
		ExampleVideoRequest e = new ExampleVideoRequest();
		BidRequestParentNodeDTO v = e.createVideoRequest();
		System.out.println(e.toJson(v));
		
	}
}
