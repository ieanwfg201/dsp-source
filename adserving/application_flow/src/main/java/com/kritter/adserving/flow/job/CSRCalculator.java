package com.kritter.adserving.flow.job;

import com.kritter.adserving.prediction.cache.LogisticRegressionCSRDBCache;
import com.kritter.adserving.prediction.interfaces.CSRPredictor;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * This class calculates csr/cpa value for each ad unit which
 * are of type cpa.This is done via usage of csr cache
 * which is provided all the dimensions for each ad unit.
 */
public class CSRCalculator implements Job
{
    private Logger logger;
    private String jobName;
    private String requestObjectKey;
    private String responseObjectKey;
    private String[] dimensionNames;
    private LogisticRegressionCSRDBCache logisticRegressionCSRCache;
    private final int modelId;
    private final double defaultCSR;
    private static final int SATURDAY = 7;
    private static final int SUNDAY = 1;
    private static final int DEFAULT_DIM_VALUE = -1;

    public CSRCalculator(
                         String loggerName,
                         String jobName,
                         String requestObjectKey,
                         String responseObjectKey,
                         String[] dimensionNames,
                         LogisticRegressionCSRDBCache logisticRegressionCSRCache,
                         int modelId,
                         double defaultCSR
                        )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.jobName = jobName;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.dimensionNames = dimensionNames;
        this.logisticRegressionCSRCache = logisticRegressionCSRCache;
        this.modelId = modelId;
        this.defaultCSR = defaultCSR;
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
            logger.error("Request, Response are null inside CSRCalculator ");
            return;
        }

        Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfo();

        if(null == responseAdInfos)
        {
            logger.error("Response Ad Infos are null inside CSRCalculator ");
            return;
        }

        for(ResponseAdInfo responseAdInfo : responseAdInfos)
        {
            if(responseAdInfo.getMarketPlace().getCode() != MarketPlace.CPD.getCode())
                continue;

            int[] dimensionValues = new int[this.dimensionNames.length];
            int counter = 0;

            for(String dimensionName : this.dimensionNames)
            {
                Integer dimensionValue = null;

                try
                {
                    dimensionValue = fetchDimensionValueForDimension(
                                                                     request,
                                                                     response,
                                                                     dimensionName,
                                                                     responseAdInfo
                                                                    );

                }
                catch (IOException ioe)
                {
                    ReqLog.errorWithDebug(logger, request, "IOException inside CSRCalculator ",ioe);
                }

                int value = (null == dimensionValue ? DEFAULT_DIM_VALUE : dimensionValue);

                ReqLog.debugWithDebug(logger, request, "Dimension Name: {} and value: {}", dimensionName, value );
                dimensionValues[counter++] = value;
            }

            CSRPredictor csrModel = this.logisticRegressionCSRCache.query(this.modelId);
            double csrValue = defaultCSR;
            if(csrModel != null)
                csrValue = csrModel.getCSR(this.dimensionNames, dimensionValues);

            ReqLog.debugWithDebug(logger, request, "CSR value for ad unit: {} is : {} ", responseAdInfo.getAdId(), csrValue);
            responseAdInfo.setCpaValue(csrValue);
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
