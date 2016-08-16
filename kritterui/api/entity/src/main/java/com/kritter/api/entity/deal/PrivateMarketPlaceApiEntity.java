package com.kritter.api.entity.deal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    /*id list from ad table, if deal wants to run on specific targeting.cannot be null.*/
    private String adIdList;

    @Getter @Setter
    //This has list of pub ids for which deal is applicable,e.g: ["1","2"] or can be null
    private String pubIdList="[]";

    @Getter @Setter
    /*This has list of site ids for which deal is applicable,e.g: ["1","2"] or can be null*/
    private String siteIdList;

    @Getter @Setter
    /*iab categories code array for list of iab categories to block.*/
    private String blockedIABCategories;

    @Getter @Setter
    /*string array, with each value as an advertiser guid.*/
    private String thirdPartyConnectionList;

    @Getter @Setter
    /* Array with each value as id from third_party_conn_dsp_mapping table.*/
    private String dspIdList;

    @Getter @Setter
    /* Array with each value as id from dsp_adv_agency_mapping table.*/
    private String advertiserIdList;

    @Getter @Setter
    /* Since the white listed domains can be specific to each third party connection, as each third pary connection
     * would have their own set of advertisers.
     * This map has key as third party connection id and value as array of advertiser domain available on that
     * third party connection.
     */
    private String whitelistedAdvertiserDomainsMap;

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
}