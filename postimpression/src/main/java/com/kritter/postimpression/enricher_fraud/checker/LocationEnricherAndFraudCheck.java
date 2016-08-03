package com.kritter.postimpression.enricher_fraud.checker;

import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.geo.common.entity.*;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.CountryUserInterfaceIdCache;
import com.kritter.geo.common.entity.reader.IConnectionTypeDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.geo.common.entity.reader.ISPUserInterfaceIdCache;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import com.kritter.constants.ConnectionType;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * This class checks whether the location id received in post impression event
 * matches with the location id that was sent in url at the time of impression.
 * As of now the check is only on country id.
 */

public class LocationEnricherAndFraudCheck implements OnlineEnricherAndFraudCheck
{
	private String signature;
    private Logger logger;
	private String postImpressionRequestObjectKey;
	private String remoteAddressHeaderName;
    private String xffHeaderName;
    private String userAgentHeaderName;
    private List<String> privateAddressPrefixList;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private CountryUserInterfaceIdCache countryUserInterfaceIdCache;
    private ISPUserInterfaceIdCache ispUserInterfaceIdCache;
    private IConnectionTypeDetectionCache connectionTypeDetectionCache;

	public LocationEnricherAndFraudCheck(
                                         String signature,
                                         String loggerName,
                                         String postImpressionRequestObjectKey,
                                         String remoteAddressHeaderName,
                                         String xffHeaderName,
                                         String userAgentHeaderName,
                                         List<String> privateAddressPrefixList,
                                         CountryDetectionCache countryDetectionCache,
                                         ISPDetectionCache ispDetectionCache,
                                         CountryUserInterfaceIdCache countryUserInterfaceIdCache,
                                         ISPUserInterfaceIdCache ispUserInterfaceIdCache
                                        ) throws Exception
    {
		this.signature = signature;
        this.logger = LoggerFactory.getLogger(loggerName);
		this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.remoteAddressHeaderName = remoteAddressHeaderName;
        this.xffHeaderName = xffHeaderName;
        this.userAgentHeaderName = userAgentHeaderName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.countryUserInterfaceIdCache = countryUserInterfaceIdCache;
        this.ispUserInterfaceIdCache = ispUserInterfaceIdCache;
        this.connectionTypeDetectionCache = null;

	}

	public LocationEnricherAndFraudCheck(
                                         String signature,
                                         String loggerName,
                                         String postImpressionRequestObjectKey,
                                         String remoteAddressHeaderName,
                                         String xffHeaderName,
                                         String userAgentHeaderName,
                                         List<String> privateAddressPrefixList,
                                         CountryDetectionCache countryDetectionCache,
                                         ISPDetectionCache ispDetectionCache,
                                         CountryUserInterfaceIdCache countryUserInterfaceIdCache,
                                         ISPUserInterfaceIdCache ispUserInterfaceIdCache,
                                         IConnectionTypeDetectionCache connectionTypeDetectionCache
                                        ) throws Exception
    {
		this.signature = signature;
        this.logger = LoggerFactory.getLogger(loggerName);
		this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.remoteAddressHeaderName = remoteAddressHeaderName;
        this.xffHeaderName = xffHeaderName;
        this.userAgentHeaderName = userAgentHeaderName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.countryUserInterfaceIdCache = countryUserInterfaceIdCache;
        this.ispUserInterfaceIdCache = ispUserInterfaceIdCache;
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;

	}

	@Override
	public String getIdentifier()
    {
		return this.signature;
	}

	@Override
	public ONLINE_FRAUD_REASON fetchFraudReason(Context context,boolean applyFraudCheck)
    {
        if(!applyFraudCheck)
            return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;

        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        Request request = (Request)context.getValue(this.postImpressionRequestObjectKey);

        //if event is from rtb-exchange,ignore this check as mnc,mcc codes are not available here.
        if(request.getInventorySource().shortValue() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode())
            return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;

        String remoteAddress = httpServletRequest.getHeader(this.remoteAddressHeaderName);
        String xffHeader = httpServletRequest.getHeader(this.xffHeaderName);
        String userAgent = httpServletRequest.getHeader(this.userAgentHeaderName);

        logger.debug("Inside LocationEnricherAndFraudCheck , Remote address: {}, xffHeader: {} and user agent: {}", remoteAddress, xffHeader, userAgent);

        String ip = ApplicationGeneralUtils.findCorrectIpAddressForDetection(remoteAddress,
                xffHeader,
                privateAddressPrefixList);

        logger.debug("The ip found for detection is : {}", ip);

        Integer countryId = null;
        Integer countryCarrierId = null;
        Integer uiCountryId = null;
        Integer uiCountryCarrierId = null;

        try
        {
            countryId = findCountryId(ip);
            if(null != countryId)
            {
                Set<Integer> uiCountryIdSetForDataSourceId =
                        this.countryUserInterfaceIdCache.query(new CountryUserInterfaceIdSecondaryIndex(countryId));

                if(null != uiCountryIdSetForDataSourceId)
                {
                    for(Integer uiCountryIdEntry : uiCountryIdSetForDataSourceId)
                    {
                        uiCountryId = uiCountryIdEntry;
                        break;
                    }
                }
            }

            if(null == uiCountryId)
                uiCountryId = ApplicationGeneralUtils.DEFAULT_COUNTRY_ID;

            logger.debug("Country id found as : {}", countryId);
            logger.debug("UICountry id found as : {}", uiCountryId);
        }
        catch (Exception e)
        {
            logger.error("Exception in finding country id setting as default -1. ",e);
            countryId = ApplicationGeneralUtils.DEFAULT_COUNTRY_ID;
            uiCountryId = ApplicationGeneralUtils.DEFAULT_COUNTRY_ID;
        }

        try
        {
            countryCarrierId = findCarrierId(ip);
            if(null != countryCarrierId)
            {
                Set<Integer> uiIspIdSetForDataSourceId =
                        this.ispUserInterfaceIdCache.query(new IspUserInterfaceIdSecondaryIndex(countryCarrierId));

                if(null != uiIspIdSetForDataSourceId)
                {
                    for(Integer uiIspIdEntry : uiIspIdSetForDataSourceId)
                    {
                        uiCountryCarrierId = uiIspIdEntry;
                        break;
                    }
                }
            }

            if(null == uiCountryCarrierId)
                uiCountryCarrierId = ApplicationGeneralUtils.DEFAULT_COUNTRY_CARRIER_ID;

            logger.debug("CountryCarrier id found as {}", countryCarrierId);
            logger.debug("UICountryCarrier id found as {}", uiCountryCarrierId);
        }
        catch (Exception e)
        {
            logger.error("Exception in finding carrier id setting as default -1. ",e);
            countryCarrierId = ApplicationGeneralUtils.DEFAULT_COUNTRY_CARRIER_ID;
            uiCountryCarrierId = ApplicationGeneralUtils.DEFAULT_COUNTRY_CARRIER_ID;
        }

        request.setCountryId(countryId);
        request.setCountryCarrierId(countryCarrierId);

        /**
         * Not required to set connection type here, as its set using id from url.
        ConnectionType connectionType = ConnectionType.UNKNOWN;
        if(connectionTypeDetectionCache != null) {
            connectionType = connectionTypeDetectionCache.getConnectionType(ip);
            logger.debug("Connection type for ip {} = {}", ip, connectionType);
        }
        request.setConnectionType(connectionType);
        **/

        if(null == request.getUiCountryId() || null == request.getUiCountryCarrierId())
        {
            return ONLINE_FRAUD_REASON.LOCATION_ID_MISSING_FROM_REQUEST;
        }

        //check if country and carrier id matches or not.
        if(null != uiCountryCarrierId && null != uiCountryId)
        {
            if(
                (
                 (uiCountryCarrierId.intValue() == request.getUiCountryCarrierId().intValue()) ||
                 request.getUiCountryCarrierId().equals(ApplicationGeneralUtils.DEFAULT_COUNTRY_CARRIER_ID)
                ) &&
                (
                 (uiCountryId.intValue() == request.getUiCountryId().intValue()) ||
                 request.getUiCountryId().equals(ApplicationGeneralUtils.DEFAULT_COUNTRY_ID)
                )
              )
                return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
            else
            {
                //set detected ui country and carrier ids.
                request.setUiCountryId(uiCountryId);
                request.setUiCountryCarrierId(uiCountryCarrierId);
                return ONLINE_FRAUD_REASON.LOCATION_MISMATCH;
            }
        }

		return ONLINE_FRAUD_REASON.LOCATION_MISMATCH;
	}

    private Integer findCountryId(String ip) throws Exception
    {
        Country country = countryDetectionCache.findCountryForIpAddress(ip);

        if(null != country)
            return country.getCountryInternalId();

        return null;
    }

    private Integer findCarrierId(String ip) throws Exception
    {

        InternetServiceProvider internetServiceProvider = this.ispDetectionCache.fetchISPForIpAddress(ip);

        if(null != internetServiceProvider)
            return internetServiceProvider.getOperatorInternalId();

        return null;
    }
}
