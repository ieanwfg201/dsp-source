package com.kritter.utils.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class contains conversion data which is sent in as part of click redirect url
 * to advertiser's landing page, which can be an app download or just a web page,
 * this data when retrieved by the tracking partner is sent back to kritter server
 * through postback url for conversion attribution.
 */
@ToString
public class ConversionUrlData
{
    @Getter @Setter
    Double internalBid;
    @Getter @Setter
    Double advertiserBid;
    @Getter @Setter
    Integer adId;
    @Getter @Setter
    Integer siteId;
    @Getter @Setter
    Integer inventorySource;
    @Getter @Setter
    String clickRequestId;
    @Getter @Setter
    Integer carrierId;
    @Getter @Setter
    Integer countryId;
    @Getter @Setter
    Integer deviceModelId;
    @Getter @Setter
    Integer deviceManufacturerId;
    @Getter @Setter
    Integer deviceOsId;
    @Getter @Setter
    Integer deviceBrowserId;
    @Getter @Setter
    Short selectedSiteCategoryId;
    @Getter @Setter
    Short bidderModelId;
    @Getter @Setter
    Short supplySourceType;
    @Getter @Setter
    Integer externalSupplyAttributesInternalId;
    @Getter @Setter
    Short connectionTypeId;
    @Getter @Setter
    Short deviceTypeId;
}
