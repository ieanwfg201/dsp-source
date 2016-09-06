package models.entities;

import org.springframework.beans.BeanUtils;
import play.data.validation.Constraints.Required;
import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
public class DealEntity
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
    /*advertiser guid.Note: Only one value.*/
    private String thirdPartyConnectionGuid="None";

    @Getter @Setter
    /* Array with each value as id from third_party_conn_dsp_mapping table.*/
    private String dspIdList="[]";

    @Getter @Setter
    /* Array with each value as id from dsp_adv_agency_mapping table.*/
    private String advertiserIdList="[]";

    @Setter @Getter
    /*
     * Whitelisted advertiser domains for the mentioned third party connection.
     */
    private String whitelistedAdvertiserDomains;

    @Getter @Setter
    /*possible values 1,2,3. Refer to descriptions in open rtb documents for pmp auction type.*/
    private String auctionType;

    @Getter @Setter
    private String requestCap;

    @Getter @Setter
    private long startDate = 0;

    @Getter @Setter
    private long endDate = 0;

    @Getter @Setter
    private String dealCPM;

    @Getter @Setter
    private short status;

    @Getter @Setter
    private String statusValue;

    public PrivateMarketPlaceApiEntity getEntity(){
        PrivateMarketPlaceApiEntity pmp = new PrivateMarketPlaceApiEntity();
        BeanUtils.copyProperties(this, pmp);
        return pmp;
    }
}
