package com.kritter.formatterutil;

import com.kritter.constants.DeviceType;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.constants.ConnectionType;
import com.kritter.device.entity.HandsetMasterData;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.slf4j.Logger;

import java.security.NoSuchAlgorithmException;

/**
 * This class is utility class for creative formatting.
 * Click uri is prepared here , if new version is to be
 * implemented for the click uri/postimpression uri ,it
 * is to be done here along with feeding url version.
 *
 */
public class CreativeFormatterUtils
{
    private static final String URI_FIELD_DELIMITER = "/";
    public static final String CLICK_URL_MACRO       = "$CLICK_URL";
    public static final String CREATIVE_IMAGE_URL    = "$CREATIVE_IMAGE";
    public static final String CREATIVE_TEXT         = "$CREATIVE_TEXT";
    public static final String CREATIVE_CSC_BEACON   = "$CREATIVE_CSC_BEACON";
    public static final String CREATIVE_ALT_TEXT     = "$CREATIVE_ALT_TEXT";
    public static final String CREATIVE_TYPE         = "$CREATIVE_TYPE";
    public static final String CREATIVE_IMAGE_WIDTH  = "$CREATIVE_IMAGE_WIDTH";
    public static final String CREATIVE_IMAGE_HEIGHT = "$CREATIVE_IMAGE_HEIGHT";
    public static final String RICHMEDIA_PAYLOAD     = "$RICHMEDIA_PAYLOAD";
    public static final String WIN_NOTIFICATION_URL  = "$WIN_NOTIFICATION_URL";


    public static String prepareClickUri(Logger logger,Request request,
                                         ResponseAdInfo responseAdInfo,
                                         Short bidderModelId,
                                         int urlVersion,
                                         int inventorySource,
                                         Short selectedSiteCategoryId,
                                         String secretKeyForHash)
    {
        String impressionId = ApplicationGeneralUtils
                                                .generateImpressionId(request.getRequestId(),
                                                        responseAdInfo.getAdId());

        if(null == bidderModelId)
        {
            bidderModelId = ApplicationGeneralUtils.DEFAULT_BIDDER_MODEL_ID;
        }

        if(null == selectedSiteCategoryId)
        {
            selectedSiteCategoryId = ApplicationGeneralUtils.DEFAULT_SELECTED_SITE_CATEGORY_ID;
        }

        //set impression id in response ad information.
        responseAdInfo.setImpressionId(impressionId);

        //the device id has to be present and handset has to be not null
        //otherwise flow would not come here, this check of non null
        //handset is done inside request enrichers.
        HandsetMasterData handsetMasterData = request.getHandsetMasterData();

        if(null == handsetMasterData)
            return null;

        //For fraud check logic in postimpression,check for deviceId first,
        //if not equal then check for manufacturer and model to be equal.
        //Reason being just in case new handset dataloading happens first
        //in postimpression we will have a new deviceid detected for the
        //same user agent however the manufacturer and model id would be
        //the same, hence the postimpression can anytime in terms of new
        //handset data loading, also in case of if adserving handset data
        //loading takes place first then its not an issue as in that case
        //also device id could be different(if postimpression has not
        //loaded its handset data yet however the individual manufacturer
        //and model ids would be same if handset detected at all in
        //postimpression server.)
        //In online system ,as we have right now,depending upon the
        //frequency at which data loads takes place, we wait for two times
        //that frequency after putting files in postimpression server(if
        //postimpression server is different from adserving.)Otherwise
        //in case of same server the synch issue couldbe very less or not
        //at all.
        Long deviceId = handsetMasterData.getInternalId();
        int manufacturerId = handsetMasterData.getManufacturerId();
        int modelId = handsetMasterData.getModelId();
        int osId = handsetMasterData.getDeviceOperatingSystemId();
        int browserId = handsetMasterData.getDeviceBrowserId();

        Integer siteId = request.getSite().getSiteIncId();
        short supplySourceWapOrApp = -1;

        try
        {
            supplySourceWapOrApp = request.getSite().getSitePlatform().shortValue();
        }
        catch (Exception e)
        {
            logger.error("Error in getting site platform code",e);
        }

        Integer countryId =        (null == request.getCountryUserInterfaceId()) ?
                                   ApplicationGeneralUtils.DEFAULT_COUNTRY_ID :
                                   request.getCountryUserInterfaceId();

        Integer countryCarrierId = (null == request.getCarrierUserInterfaceId()) ?
                                   ApplicationGeneralUtils.DEFAULT_COUNTRY_CARRIER_ID :
                                   request.getCarrierUserInterfaceId();

        String encodedBidInfo = ApplicationGeneralUtils.formEncodedBidTypesWithValues(
                                            responseAdInfo.getHardBid(),responseAdInfo.getAdvertiserBid());

        Integer externalSupplyAttributesInternalId =
                (null == request.getExternalSupplyAttributesInternalId())?
                ApplicationGeneralUtils.DEFAULT_INTERNAL_ID_FOR_EXTERNAL_SUPPLY_ATTRIBUTES :
                request.getExternalSupplyAttributesInternalId();

        Short connectionTypeId = (null == request.getConnectionType()) ? ConnectionType.UNKNOWN.getId() :
                request.getConnectionType().getId();

        Short deviceType = (null == request.getHandsetMasterData() ||
                            null == request.getHandsetMasterData().getDeviceType())
                ? DeviceType.UNKNOWN.getCode() : request.getHandsetMasterData().getDeviceType().getCode();

        StringBuffer dataToHash = new StringBuffer(urlVersion);
        dataToHash.append(inventorySource);
        dataToHash.append(impressionId);
        dataToHash.append(deviceId);
        dataToHash.append(manufacturerId);
        dataToHash.append(modelId);
        dataToHash.append(osId);
        dataToHash.append(browserId);
        dataToHash.append(responseAdInfo.getSlotId());
        dataToHash.append(siteId);
        dataToHash.append(countryId);
        dataToHash.append(countryCarrierId);
        dataToHash.append(encodedBidInfo);
        dataToHash.append(bidderModelId);
        dataToHash.append(selectedSiteCategoryId);
        dataToHash.append(supplySourceWapOrApp);
        dataToHash.append(externalSupplyAttributesInternalId);
        dataToHash.append(connectionTypeId);
        dataToHash.append(deviceType);

        String hashValue = null;

        try
        {
            hashValue = ApplicationGeneralUtils.calculateHashForData(dataToHash.toString(),secretKeyForHash);
        }
        catch(NoSuchAlgorithmException e)
        {
            logger.error("Could not generate hash in click url creation ", e);
            return null;
        }

        StringBuffer sb = new StringBuffer(URI_FIELD_DELIMITER);
        sb.append(urlVersion);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(inventorySource);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(impressionId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(deviceId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(manufacturerId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(modelId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(osId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(browserId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(responseAdInfo.getSlotId());
        sb.append(URI_FIELD_DELIMITER);
        sb.append(siteId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(countryId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(countryCarrierId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(encodedBidInfo);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(bidderModelId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(selectedSiteCategoryId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(supplySourceWapOrApp);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(externalSupplyAttributesInternalId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(connectionTypeId);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(deviceType);
        sb.append(URI_FIELD_DELIMITER);
        sb.append(hashValue);

        return sb.toString();
    }
}
