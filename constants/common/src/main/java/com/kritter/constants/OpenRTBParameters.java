package com.kritter.constants;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class maintains parameter names that exist as part of device object or elsewhere
 * in the bid request, used by CreativeAndFloorMatchingRTBExchange implementations to
 * populate URLFields to be sent as part of postimpression URLs, what all fields must be
 * set and used for transfer is dervied by using their individual configurations inside
 * CreativeAndFloorMatchingRTBExchange implementations.
 * These parameters are not just limited to any particular object, any parameter can be
 * added here which is generic in nature to fetch.
 *
 * If some parameter is present elsewhere not in accordance of openrtb, then the
 * corresponding enricher must populate the parameter as per open rtb specification.
 * Example: smaato sends many device id related parameters in ext.udi object of top
 * level bid request, so smaato enricher must read these parameters and populate them
 * in the open rtb defined object.
 *
 * Device identifiers didsha1 and didmd5 are not sent by any ad-exchange and are
 * recommended to be deprecated. dpidsha1,dpidmd5 are being used which are anndroid
 * ids in case of android platform, macsha1 and macmd5 are also received.
 *
 * Lengths: dpid max length = 50, ifa = 50
 *
 *
 */
public class OpenRTBParameters
{
    private Map<OpenRTBVersion,List<PARAMETER>> openRTBVersionPARAMETERMap;

    public enum PARAMETER
    {
        IFA(1,"ifa","ID sanctioned for advertiser use in the clear (i.e., not hashed)."),
        DEVICE_PLATFORM_ID_SHA1(2,"dpidsha1","Platform device ID (e.g., Android ID); hashed via SHA1."),
        DEVICE_PLATFORM_ID_MD5(3,"dpidmd5","Platform device ID (e.g., Android ID); hashed via MD5."),
        MAC_ADDRESS_SHA1(6,"macsha1","MAC address of the device; hashed via SHA1."),
        MAC_ADDRESS_MD5(7,"macmd5","MAC address of the device; hashed via MD5.");

        @Getter
        private int code;
        @Getter
        private String openRtbBidRequestParameterName;
        @Getter
        private String description;

        PARAMETER(int code,String openRtbBidRequestParameterName,String description)
        {
            this.code = code;
            this.openRtbBidRequestParameterName = openRtbBidRequestParameterName;
            this.description = description;
        }
    }

    public OpenRTBParameters()
    {
        this.openRTBVersionPARAMETERMap = new HashMap<OpenRTBVersion, List<PARAMETER>>();
        populateVersionSpecificParameterNames();
    }

    public void populateVersionSpecificParameterNames()
    {
        List<PARAMETER> twoDotZeroParameterList = new ArrayList<PARAMETER>();
        twoDotZeroParameterList.add(PARAMETER.DEVICE_PLATFORM_ID_SHA1);
        twoDotZeroParameterList.add(PARAMETER.DEVICE_PLATFORM_ID_MD5);

        //same as two dot zero.
        List<PARAMETER> twoDotOneParameterList = twoDotZeroParameterList;

        List<PARAMETER> twoDotTwoParameterList = new ArrayList<PARAMETER>();
        twoDotTwoParameterList.add(PARAMETER.DEVICE_PLATFORM_ID_SHA1);
        twoDotTwoParameterList.add(PARAMETER.DEVICE_PLATFORM_ID_MD5);
        twoDotTwoParameterList.add(PARAMETER.MAC_ADDRESS_SHA1);
        twoDotTwoParameterList.add(PARAMETER.MAC_ADDRESS_MD5);

        List<PARAMETER> twoDotThreeParameterList = new ArrayList<PARAMETER>();
        twoDotThreeParameterList.add(PARAMETER.DEVICE_PLATFORM_ID_SHA1);
        twoDotThreeParameterList.add(PARAMETER.DEVICE_PLATFORM_ID_MD5);
        twoDotThreeParameterList.add(PARAMETER.MAC_ADDRESS_SHA1);
        twoDotThreeParameterList.add(PARAMETER.MAC_ADDRESS_MD5);
        twoDotThreeParameterList.add(PARAMETER.IFA);

        openRTBVersionPARAMETERMap.put(OpenRTBVersion.VERSION_2_0,twoDotZeroParameterList);
        openRTBVersionPARAMETERMap.put(OpenRTBVersion.VERSION_2_1,twoDotOneParameterList);
        openRTBVersionPARAMETERMap.put(OpenRTBVersion.VERSION_2_2,twoDotTwoParameterList);
        openRTBVersionPARAMETERMap.put(OpenRTBVersion.VERSION_2_3,twoDotThreeParameterList);
    }

    public List<PARAMETER> fetchOpenRTBVersionSpecificBidRequestParameters(OpenRTBVersion openRTBVersion)
    {
        return openRTBVersionPARAMETERMap.get(openRTBVersion);
    }
}
