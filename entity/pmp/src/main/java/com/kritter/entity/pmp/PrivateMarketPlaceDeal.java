package com.kritter.entity.pmp;

import lombok.Getter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

/**
 * This class keeps a private marketplace deal data.
 */
public class PrivateMarketPlaceDeal
{
    @Getter
    /*nomenclature: SSP-MMYY-Numeric_Value,e.g: nativead-0716-9871344567*/
    private String dealId;

    @Getter
    /*nomenclature: ATD/Buyer-dsp-publisher-placement-MMDDYY,e.g: VIVAKI-MEDIAMATH-nativehome-sports_home-071116*/
    private String dealName;

    @Getter
    /*guid from ad table, if deal wants to run on specific targeting.cannot be null*/
    private String adGuid;

    @Getter
    /*This has list of site ids for which deal is applicable,e.g: ["1","2"] or can be null*/
    private Integer[] siteIdList;

    @Getter
    /*iab categories code array for list of iab categories to block.*/
    private String[] blockedIABCategories;

    @Getter
    /*string array, with each value as an advertiser guid.*/
    private String[] thirdPartyConnectionList;

    @Getter
    /* Array with each value as id from third_party_conn_dsp_mapping table.*/
    private Integer[] dspIdList;

    @Getter
    /* Array with each value as id from dsp_adv_agency_mapping table.*/
    private Integer[] advertiserIdList;

    @Getter
    /* Since the white listed domains can be specific to each third party connection, as each third pary connection
     * would have their own set of advertisers.
     * This map has key as third party connection id and value as array of advertiser domain available on that
     * third party connection.
     */
    private Map<String,String[]> whitelistedAdvertiserDomainsMap;

    @Getter
    /*possible values 1,2,3. Refer to descriptions in open rtb documents for pmp auction type.*/
    private Short auctionType;

    @Getter
    private Integer requestCap;

    @Getter
    private Timestamp startDate;

    @Getter
    private Timestamp endDate;

    @Getter
    private Double dealCPM;

    private static final String EMPTY_STRING = "";

    public PrivateMarketPlaceDeal(String dealId,
                                  String dealName,
                                  String adGuid,
                                  Integer[] siteIdList,
                                  String[] blockedIABCategories,
                                  String[] thirdPartyConnectionList,
                                  Integer[] dspIdList,
                                  Integer[] advertiserIdList,
                                  Map<String,String[]> whitelistedAdvertiserDomainsMap,
                                  Short auctionType,
                                  Integer requestCap,
                                  Timestamp startDate,
                                  Timestamp endDate,
                                  Double dealCPM)
    {
        this.dealId = dealId;
        this.dealName = dealName;
        this.adGuid = adGuid;
        this.siteIdList = siteIdList;
        this.blockedIABCategories = blockedIABCategories;
        this.thirdPartyConnectionList = thirdPartyConnectionList;
        this.dspIdList = dspIdList;
        this.advertiserIdList = advertiserIdList;
        this.whitelistedAdvertiserDomainsMap = whitelistedAdvertiserDomainsMap;
        this.auctionType = auctionType;
        this.requestCap = requestCap;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dealCPM = dealCPM;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (this.dealId.hashCode()) + (this.dealName.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        PrivateMarketPlaceDeal externalObject = (PrivateMarketPlaceDeal) obj;

        if (
            this.dealId.equalsIgnoreCase(externalObject.dealId) &&
            this.dealName.equalsIgnoreCase(externalObject.dealName)
           )
            return true;

        return false;
    }

    public static Map<String,String[]> populateWhitelistedAdvertiserDomains(String whitelistedDomainsTextValue)
                                                                                                    throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        if(null != whitelistedDomainsTextValue && !EMPTY_STRING.equals(whitelistedDomainsTextValue))
            return objectMapper.readValue(whitelistedDomainsTextValue,new TypeReference<Map<String,String[]>>(){});

        return null;
    }
}