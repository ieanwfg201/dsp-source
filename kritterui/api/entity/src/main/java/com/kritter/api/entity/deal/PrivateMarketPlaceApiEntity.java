package com.kritter.api.entity.deal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class captures data for pmp deals from user interface.
 */
@ToString
public class PrivateMarketPlaceApiEntity
{
    @Getter @Setter
    /*nomenclature: SSP-MMYY-Numeric_Value,e.g: nativead-0716-9871344567*/
    private String dealId;

    @Getter @Setter
    /*nomenclature: ATD/Buyer-dsp-publisher-placement-MMDDYY,e.g: VIVAKI-MEDIAMATH-nativehome-sports_home-071116*/
    private String dealName;

    @Getter @Setter
    /*id list from campaign table, if deal wants to run on specific targeting.cannot be null.*/
    private String campaignIdList="[]";

    @Getter @Setter
    /*id list from ad table, if deal wants to run on specific targeting.cannot be null.*/
    private String adIdList="[]";

    @Getter @Setter
    //This has list of pub ids for which deal is applicable,e.g: ["1","2"] or can be null
    private String pubIdList="[]";

    @Getter @Setter
    /*This has list of site ids for which deal is applicable,e.g: ["1","2"] or can be null*/
    private String siteIdList="[]";

    @Getter @Setter
    /*iab categories code array for list of iab categories to block.*/
    private String blockedIABCategories="[]";

    @Getter @Setter
    /*advertiser guid.*/
    private String thirdPartyConnectionGuid="";

    @Getter @Setter
    /* Array with each value as id from third_party_conn_dsp_mapping table.*/
    private String dspIdList="[]";

    @Getter @Setter
    /* Array with each value as id from dsp_adv_agency_mapping table.*/
    private String advertiserIdList="[]";

    @Setter
    /* Array of whitelisted domains
     */
    private String whitelistedAdvertiserDomains;

    public String getWhitelistedAdvertiserDomains()
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            if (null != whitelistedAdvertiserDomains)
            {
                String[] values = objectMapper.readValue(whitelistedAdvertiserDomains, String[].class);
                StringBuffer sb = new StringBuffer();
                for (String value : values)
                {
                    sb.append(value);
                    sb.append(",");
                }

                sb = sb.deleteCharAt(sb.length() - 1);
                return sb.toString();
            }
        }
        catch (Exception e)
        {
        }

        return "";
    }

    @Getter @Setter
    /*possible values 1,2,3. Refer to descriptions in open rtb documents for pmp auction type.*/
    private String auctionType;

    @Getter @Setter
    private String requestCap;

    @Getter @Setter
    private long startDate;

    @Getter @Setter
    private long endDate;

    @Getter @Setter
    private String dealCPM;

    @Getter @Setter
    private int isEdit = 0;

    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
}