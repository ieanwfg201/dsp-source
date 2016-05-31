package com.kritter.postimpression.enricher_fraud.checker;

import java.security.NoSuchAlgorithmException;

import com.kritter.core.workflow.Context;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to check whether the received url is tampered with
 * or not.
 * 
 */
public class URLParamEnricherAndFraudCheck implements OnlineEnricherAndFraudCheck
{
	private String signature;
    private Logger logger;
    private String postImpressionRequestObjectKey;
	private String secretKey;

	public URLParamEnricherAndFraudCheck(
                                         String signature,
                                         String loggerName,
                                         String postImpressionRequestObjectKey,
                                         String secretKey
                                        )
    {
        this.signature = signature;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
		this.secretKey = secretKey;
	}

	@Override
	public String getIdentifier()
    {
		return this.signature;
	}

	@Override
	public ONLINE_FRAUD_REASON fetchFraudReason(Context context,boolean applyFraudCheck)
    {
        Request request = (Request)context.getValue(this.postImpressionRequestObjectKey);

        logger.debug(
                "Inside URLParamEnricherAndFraudCheck , data is : url hash:{} , impressionid:{}, deviceid:{},manufacturerid:{},modelid:{},osId:{},browserId:{},slotId:{},siteid:{},ui-countryid:{},ui-countrycarrierid:{},biddermodelid:{},urlversion:{},inventorysource:{},selectedsitecategoryid:{},supplySourceType: {},externalSupplyAttrInternalId: {},connectionType: {},deviceType:{}",
                     request.getUrlHash(),
                     request.getImpressionId(),
                     request.getDeviceId(),
                     request.getDeviceManufacturerId(),
                     request.getDeviceModelId(),
                     request.getDeviceOsId(),
                     request.getBrowserId(),
                     request.getSlotId(),
                     request.getSiteId(),
                     request.getUiCountryId(),
                     request.getUiCountryCarrierId(),
                     request.getBidderModelId(),
                     request.getUrlVersion(),
                     request.getInventorySource(),
                     request.getSelectedSiteCategoryId(),
                     request.getSupplySourceTypeCode(),
                     request.getExternalSupplyAttributesInternalId(),
                     request.getConnectionType(),
                     request.getDeviceTypeId()
                    );

        if(null==request.getUrlHash()                            ||
           null==request.getImpressionId()                       ||
           null==request.getDeviceId()                           ||
           null==request.getDeviceManufacturerId()               ||
           null==request.getDeviceModelId()                      ||
           null==request.getDeviceOsId()                         ||
           null==request.getBrowserId()                          ||
           null==request.getSlotId()                             ||
           null==request.getSiteId()                             ||
           null==request.getUiCountryId()                        ||
           null==request.getUiCountryCarrierId()                 ||
           null==request.getBidderModelId()                      ||
           null==request.getUrlVersion()                         ||
           null==request.getInventorySource()                    ||
           null==request.getSelectedSiteCategoryId()             ||
           null==request.getSupplySourceTypeCode()               ||
           null==request.getExternalSupplyAttributesInternalId() ||
           null==request.getConnectionType()                    ||
           null==request.getDeviceTypeId())
        {
            return ONLINE_FRAUD_REASON.URL_DATA_NOT_COMPLETE;
        }

        StringBuffer dataToHash = new StringBuffer(request.getUrlVersion());
        dataToHash.append(request.getInventorySource());
        dataToHash.append(request.getImpressionId());
        dataToHash.append(request.getDeviceId());
        dataToHash.append(request.getDeviceManufacturerId());
        dataToHash.append(request.getDeviceModelId());
        dataToHash.append(request.getDeviceOsId());
        dataToHash.append(request.getBrowserId());
        dataToHash.append(request.getSlotId());
        dataToHash.append(request.getSiteId());
        dataToHash.append(request.getUiCountryId());
        dataToHash.append(request.getUiCountryCarrierId());
        dataToHash.append(ApplicationGeneralUtils.
                formEncodedBidTypesWithValues(request.getInternalMaxBid(),request.getAdvertiserBid()));
        dataToHash.append(request.getBidderModelId());
        dataToHash.append(request.getSelectedSiteCategoryId());
        dataToHash.append(request.getSupplySourceTypeCode());
        dataToHash.append(request.getExternalSupplyAttributesInternalId());
        dataToHash.append(request.getConnectionType().getId());
        dataToHash.append(request.getDeviceTypeId());

		try
        {
            String hashValue = ApplicationGeneralUtils.calculateHashForData(dataToHash.toString(),this.secretKey);

			if (!hashValue.equals(request.getUrlHash()))
			    return ONLINE_FRAUD_REASON.URL_TAMPERED;

		}
        catch (NoSuchAlgorithmException nsae)
        {
            logger.error("NoSuchAlgorithmException inside URLParamEnricherAndFraudCheck",nsae);
			return ONLINE_FRAUD_REASON.INTERNAL_SERVER_ERROR;
		}

		return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
	}
}
