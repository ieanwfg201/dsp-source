package com.kritter.adserving.flow.job;

import com.kritter.adserving.prediction.cache.LogisticRegressionCTRDBCache;
import com.kritter.adserving.prediction.interfaces.CTRPredictor;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.constants.MarketPlace;
import com.kritter.constants.SupplyDemandDimension;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Set;

/**
 * This class calculates ctr value for each ad unit which
 * are of type cpc.This is done via usage of ctr cache
 * which is provided all the dimensions for each ad unit.
 */
public class CTRCalculator implements Job
{
    private Logger logger;
    private String jobName;
    private String requestObjectKey;
    private String responseObjectKey;
    private String[] dimensionNames;
    private LogisticRegressionCTRDBCache logisticRegressionCTRCache;
    private final int modelId;
    private final double defaultCTR;
    private final double ctrCeiling;
    private static final int SATURDAY = 7;
    private static final int SUNDAY = 1;
    private static final int DEFAULT_DIM_VALUE = -1;

    public CTRCalculator(
                         String loggerName,
                         String jobName,
                         String requestObjectKey,
                         String responseObjectKey,
                         String[] dimensionNames,
                         LogisticRegressionCTRDBCache logisticRegressionCTRCache,
                         int modelId,
                         double defaultCTR,
                         double ctrCeiling
                        )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.jobName = jobName;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.dimensionNames = dimensionNames;
        this.logisticRegressionCTRCache = logisticRegressionCTRCache;
        this.modelId = modelId;
        this.defaultCTR = defaultCTR;
        this.ctrCeiling = ctrCeiling;
    }

    @Override
    public String getName()
    {
        return jobName;
    }

    @Override
    public void execute(Context context)
    {
        Request request = (Request)context.getValue(requestObjectKey);
        Response response = (Response)context.getValue(responseObjectKey);

        if(null == request || null == response)
        {
            logger.error("Request, Response are null inside CTRCalculator ");
            return;
        }

        Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfo();

        if(null == responseAdInfos)
        {
            logger.error("Response Ad Infos are null inside CTRCalculator ");
            return;
        }

        for(ResponseAdInfo responseAdInfo : responseAdInfos)
        {
            //if ad is not of type CPC or CPD then skip it.
//            int marketPlaceCode =  responseAdInfo.getMarketPlace().getCode();
//            if(!(marketPlaceCode == MarketPlace.CPC.getCode() || marketPlaceCode == MarketPlace.CPD.getCode())) {
//                continue;
//            }

            int[] dimensionValues = new int[this.dimensionNames.length];
            int counter = 0;

            for(String dimensionName : this.dimensionNames)
            {
                Integer dimensionValue = null;

                try {
                    dimensionValue = fetchDimensionValueForDimension(request, response, dimensionName, responseAdInfo);

                } catch (IOException ioe) {
                    ReqLog.errorWithDebugNew(logger, request, "IOException inside CTRCalculator ", ioe);
                }

                int value = (null == dimensionValue ? DEFAULT_DIM_VALUE : dimensionValue);

                ReqLog.debugWithDebugNew(logger, request, "Dimension Name: {} and value: {}", dimensionName, value);
                dimensionValues[counter++] = value;
            }

            // double ctrValue = this.logisticRegressionCTRCache.getCTR(this.dimensionNames,dimensionValues);
            // double ctrValue = this.ctrPredictor.getCTR(this.dimensionNames,dimensionValues);
            CTRPredictor ctrModel = (CTRPredictor) this.logisticRegressionCTRCache.query(this.modelId);
            double ctrValue = defaultCTR;
            if(ctrModel != null)
                ctrValue = ctrModel.getCTR(this.dimensionNames, dimensionValues);

            if(ctrValue < 0)
                ctrValue = defaultCTR;

            ReqLog.debugWithDebugNew(logger, request, "CTR value for ad unit: {} is : {}", responseAdInfo.getAdId(), ctrValue);

            if(ctrValue > ctrCeiling) {
                ReqLog.debugWithDebugNew(logger, request, "CTR value for ad unit: {} predicted to be more than CTR ceiling. Setting CTR to: {}",
                        responseAdInfo.getAdId(), this.ctrCeiling);
                ctrValue = this.ctrCeiling;
            }

            responseAdInfo.setCtrValue(ctrValue);
        }
    }

    /**
     * This function fetches dimension value for a given dimension,
     * if not found then null is returned which means no value.
     * @param dimensionName
     * @return
     */
    private Integer fetchDimensionValueForDimension(
                                                    Request request,
                                                    Response response,
                                                    String dimensionName,
                                                    ResponseAdInfo responseAdInfo
                                                   ) throws IOException
    {
        HandsetMasterData handsetMasterData = request.getHandsetMasterData();

        if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_INVENTORY_SRC_ID.getCode()))
        {
            return Integer.valueOf(request.getInventorySource());
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_SITE_ID.getCode()))
        {
            return request.getSite().getSiteIncId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_DEVICE_ID.getCode()))
        {
            if(null == handsetMasterData || null == handsetMasterData.getInternalId())
                return null;

            return handsetMasterData.getInternalId().intValue();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_MANUFACTURER_ID.getCode()))
        {
            if(null == handsetMasterData)
                return null;
            return handsetMasterData.getManufacturerId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_MODEL_ID.getCode()))
        {
            if(null == handsetMasterData)
                return null;
            return handsetMasterData.getModelId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_OS_ID.getCode()))
        {
            if(null == handsetMasterData)
                return null;
            return handsetMasterData.getDeviceOperatingSystemId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_COUNTRY_ID.getCode()))
        {
            return request.getCountryUserInterfaceId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_CARRIER_ID.getCode()))
        {
            return request.getCarrierUserInterfaceId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_SELECTED_SITE_CATEGORY_ID.getCode()))
        {
            return Integer.valueOf(response.getSelectedSiteCategoryId());
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_TIME_WINDOW_ID.getCode()))
        {
            return ApplicationGeneralUtils.getTimeWindowId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_IS_WEEKEND.getCode()))
        {
            int dayOfWeek = ApplicationGeneralUtils.getDayOfWeek();

            return (SATURDAY == dayOfWeek || SUNDAY == dayOfWeek) ? 1 : 0;
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_AD_ID.getCode()))
        {
            return responseAdInfo.getAdId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_CAMPAIGN_ID.getCode()))
        {
            return responseAdInfo.getCampaignId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_CREATIVE_ID.getCode()))
        {
            return responseAdInfo.getCreative().getId();
        }
        else if(dimensionName.equalsIgnoreCase(SupplyDemandDimension.DIM_CREATIVE_TYPE.getCode()))
        {
            return responseAdInfo.getCreative().getCreativeFormat().getCode();
        }

        return null;
    }
}
