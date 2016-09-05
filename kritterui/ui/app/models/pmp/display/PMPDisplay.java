package models.pmp.display;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.api.def.ApiDef;
import org.codehaus.jackson.map.ObjectMapper;
import models.uiutils.Path;
import scala.Option;
import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;

import controllers.deal.routes;
import services.DataAPI;
import services.MetadataAPI;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class PMPDisplay
{
    protected PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity;

    public PMPDisplay(PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity)
    {
        this.privateMarketPlaceApiEntity = privateMarketPlaceApiEntity;
    }

    public String getViewUrl() {
        return routes.DealController.info(privateMarketPlaceApiEntity.getDealId()).url();
    }

    public String getEditUrl() {
        return routes.DealController.edit(privateMarketPlaceApiEntity.getDealId()).url();
    }

    public String addPMPUrl(){
        return  routes.DealController.add().url();
    }

    public ArrayList<Path> getBreadCrumbPaths() {
        ArrayList<Path> trails= new ArrayList<Path>();
        trails.add(new Path("All Deals",routes.DealController.PMPListView().url()));
        trails.add(new Path(privateMarketPlaceApiEntity.getDealName(), getViewUrl()));

        return trails;
    }


    protected String destinationUrl = null;

    public String getDealName()
    {
        return this.privateMarketPlaceApiEntity.getDealName();
    }

    public String getDealId()
    {
        return this.privateMarketPlaceApiEntity.getDealId();
    }

    public String thirdPartyConnection()
    {
        Account account = DataAPI.getAccountByGuid(this.privateMarketPlaceApiEntity.getThirdPartyConnectionGuid());
        return account.getName();

    }

    public String getDSPList()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Integer[] ids = null;

        try
        {
            ids = objectMapper.readValue(privateMarketPlaceApiEntity.getDspIdList(), Integer[].class);
        }
        catch (Exception e)
        {
        }

        return MetadataAPI.fetchDSPsForExternalConnectionWithGivenIds(this.privateMarketPlaceApiEntity.getThirdPartyConnectionGuid(),ids);
    }

    public String getAdvertiserList()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Integer[] ids = null;

        try
        {
            ids = objectMapper.readValue(privateMarketPlaceApiEntity.getAdvertiserIdList(), Integer[].class);
        }
        catch (Exception e)
        {
        }
        return MetadataAPI.fetchAdvsForExternalConnectionWithGivenIds(this.privateMarketPlaceApiEntity.getThirdPartyConnectionGuid(),ids);

    }

    public String getCampaignIdList()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> campaignNames = new HashSet();
        if(null != privateMarketPlaceApiEntity.getCampaignIdList())
        {
            try
            {
                Integer[] campaignIds = objectMapper.readValue(privateMarketPlaceApiEntity.getCampaignIdList(), Integer[].class);
                if(null != campaignIds)
                {
                    for(Integer id : campaignIds)
                    {
                        Campaign campaign = DataAPI.getCampaign(id.intValue());
                        campaignNames.add(campaign.getName());
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
        return campaignNames.toString();
    }

    public String getAdIdList()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> adNames = new HashSet();
        if(null != privateMarketPlaceApiEntity.getAdIdList())
        {
            try
            {
                Integer[] adIds = objectMapper.readValue(privateMarketPlaceApiEntity.getAdIdList(), Integer[].class);
                if(null != adIds)
                {
                    for(Integer id : adIds)
                    {
                        Ad ad = DataAPI.getAd(id.intValue());
                        adNames.add(ad.getName());
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
        return adNames.toString();
    }

    public String publisherList()
    {

        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> publisherNames = new HashSet<String>();
        if(null != privateMarketPlaceApiEntity.getPubIdList())
        {
            try
            {
                Integer[] pubIds = objectMapper.readValue(privateMarketPlaceApiEntity.getPubIdList(), Integer[].class);
                if(null != pubIds)
                {
                    for(Integer id : pubIds)
                    {
                        Account account = DataAPI.getAccountById(id.intValue());
                        publisherNames.add(account.getName());
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
        return publisherNames.toString();
    }

    public String siteList()
    {

        ObjectMapper objectMapper = new ObjectMapper();
        Set<String> siteNames = new HashSet<String>();
        if(null != privateMarketPlaceApiEntity.getSiteIdList())
        {
            try
            {
                Integer[] siteIds = objectMapper.readValue(privateMarketPlaceApiEntity.getSiteIdList(), Integer[].class);
                if(null != siteIds)
                {
                    for(Integer id : siteIds)
                    {
                        String name = DataAPI.fetchSiteName(id);
                        siteNames.add(name);
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
        return siteNames.toString();
    }

    public String categoryList()
    {
        ObjectMapper objectMapper = new ObjectMapper();

        if(null != privateMarketPlaceApiEntity.getBlockedIABCategories()) {
            try {
                Integer[] ids = objectMapper.readValue(privateMarketPlaceApiEntity.getBlockedIABCategories(), Integer[].class);
                if (null != ids) {
                    return MetadataAPI.fetchIABCategoriesByIds(ids);
                }
            } catch (Exception e)
            {

            }
        }

        return "";
    }

    public String domains()
    {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (null != this.privateMarketPlaceApiEntity.getWhitelistedAdvertiserDomains()) {
                String[] values = objectMapper.readValue(this.privateMarketPlaceApiEntity.getWhitelistedAdvertiserDomains(), String[].class);
                StringBuffer sb = new StringBuffer();
                for (String value : values) {
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

    public String auctionType()
    {
        return this.privateMarketPlaceApiEntity.getAuctionType();
    }

    public String requestCap()
    {
        return this.privateMarketPlaceApiEntity.getRequestCap();
    }

    public String startDate()
    {
        return new Timestamp(this.privateMarketPlaceApiEntity.getStartDate()).toString();
    }

    public String endDate()
    {
        return new Timestamp(this.privateMarketPlaceApiEntity.getEndDate()).toString();
    }

    public String dealCpm()
    {
        return this.privateMarketPlaceApiEntity.getDealCPM();
    }

    public void setDestination(String destination){
        this.destinationUrl = destination;
    }

    public String getStatusValue()
    {
        if(this.privateMarketPlaceApiEntity.getStatus() == (short)StatusIdEnum.Active.getCode())
            return "ACTIVE";
        if(this.privateMarketPlaceApiEntity.getStatus() == (short)StatusIdEnum.Paused.getCode())
            return "PAUSED";

        return "INACTIVE";
    }
}

