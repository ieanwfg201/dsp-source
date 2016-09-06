package models.entities;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Form entity for deal.
 */
public class DealFormEntity extends DealEntity
{
    public DealFormEntity()
    {
        super();
    }

    public String getDealId()
    {
        return super.getDealId();
    }

    public void setDealId(String dealId)
    {
        super.setDealId(dealId);
    }

    public String getDealName()
    {
        return super.getDealName();
    }

    public void setDealName(String dealName)
    {
        super.setDealName(dealName);
    }

    public String getCampaignIdList()
    {
        return super.getCampaignIdList();
    }

    public void setCampaignIdList(String campaignIdList)
    {
        super.setCampaignIdList(campaignIdList);
    }

    public String getAdIdList()
    {
        return super.getAdIdList();
    }

    public void setAdIdList(String adIdList)
    {
        super.setAdIdList(adIdList);
    }

    public String getPubIdList()
    {
        return super.getPubIdList();
    }

    public void setPubIdList(String pubIdList)
    {
        super.setPubIdList(pubIdList);
    }


    public String getSiteIdList()
    {
        return super.getSiteIdList();
    }

    public void setSiteIdList(String siteIdList)
    {
        super.setSiteIdList(siteIdList);
    }

    public String getBlockedIABCategories()
    {
        return super.getBlockedIABCategories();
    }

    public void setBlockedIABCategories(String blockedIABCategories)
    {
        super.setBlockedIABCategories(blockedIABCategories);
    }

    public String getThirdPartyConnectionGuid()
    {
        return super.getThirdPartyConnectionGuid();
    }

    public void setThirdPartyConnectionGuid(String thirdPartyConnectionGuid)
    {
        super.setThirdPartyConnectionGuid(thirdPartyConnectionGuid);
    }

    public String getDspIdList()
    {
        return super.getDspIdList();
    }

    public void setDspIdList(String dspIdList)
    {
        super.setDspIdList(dspIdList);
    }

    public String getWhitelistedAdvertiserDomains()
    {
        if("[\"\"]".equalsIgnoreCase(super.getWhitelistedAdvertiserDomains()))
            return "";
        return super.getWhitelistedAdvertiserDomains();
    }

    public void setWhitelistedAdvertiserDomains(String whitelistedAdvertiserDomains)
    {
        super.setWhitelistedAdvertiserDomains(whitelistedAdvertiserDomains);
    }

    public String getAdvertiserIdList()
    {
        return super.getAdvertiserIdList();
    }

    public void setAdvertiserIdList(String advertiserIdList)
    {
        super.setAdvertiserIdList(advertiserIdList);
    }

    public String getAuctionType()
    {
        return super.getAuctionType();
    }

    public void setAuctionType(String auctionType)
    {
        super.setAuctionType(auctionType);
    }

    public String getRequestCap()
    {
        return super.getRequestCap();
    }

    public void setRequestCap(String requestCap)
    {
        super.setRequestCap(requestCap);
    }

    public long getStartDate()
    {
        return super.getStartDate();
    }

    public void setStartDate(long startDate)
    {
        super.setStartDate(startDate);
    }

    public long getEndDate()
    {
        return super.getEndDate();
    }

    public void setEndDate(long endDate)
    {
        super.setEndDate(endDate);
    }

    public String getDealCPM()
    {
        return super.getDealCPM();
    }

    public void setDealCPM(String dealCPM)
    {
        super.setDealCPM(dealCPM);
    }

    public short getStatus() {return super.getStatus();}

    public void setStatus(short status) {super.setStatus(status);}

    public String getStatusValue(){return super.getStatusValue();}

    public void setStatusValue(String status){super.setStatusValue(status);}

    public int isEdit = 0;
}
