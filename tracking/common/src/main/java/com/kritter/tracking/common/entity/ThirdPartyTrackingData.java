package com.kritter.tracking.common.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * This class contains most common data required for conversion
 * data management to be sent to third party trackers.
 * Some fields might be useful to only certain tracking partners
 * hence this class would contain the universal set of fields
 * across all tracking partners and whatever fields are useful
 * would be utilized.
 */
public class ThirdPartyTrackingData
{
    @Getter @Setter
    private Double internalBid;
    @Getter @Setter
    private Double advertiserBid;
    @Getter @Setter
    private Integer adId;
    @Getter @Setter
    private Integer siteId;
    @Getter @Setter
    private Integer inventorySource;
    @Getter @Setter
    private String clickRequestId;
    @Getter @Setter
    private Integer carrierId;
    @Getter @Setter
    private Integer countryId;
    @Getter @Setter
    private Integer deviceModelId;
    @Getter @Setter
    private Integer deviceManufacturerId;
    @Getter @Setter
    private Integer deviceOsId;
    @Getter @Setter
    private Integer deviceBrowserId;
    @Getter @Setter
    private Short selectedSiteCategoryId;
    @Getter @Setter
    private Short bidderModelId;
    @Getter @Setter
    private Short supplySourceType;
    @Getter @Setter
    private Integer externalSupplyAttributesInternalId;
    @Getter @Setter
    private String siteGuid;
    @Getter @Setter
    private Short connectionTypeId;
    @Getter @Setter
    private Short deviceTypeId;
}
