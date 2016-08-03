package models.publisher;

import java.util.List;

import models.uiutils.IncExcList;
import services.MetadataAPI;

import com.kritter.api.entity.site.Site;
import com.kritter.constants.APP_STORE_ID;
import com.kritter.constants.MetadataType;
import com.kritter.constants.NativeIconImageSize;
import com.kritter.constants.NativeLayoutId;
import com.kritter.constants.NativeScreenShotImageSize;

import lombok.Getter;
import lombok.Setter;

public class SiteDisplayFull extends SiteDisplay{

    public SiteDisplayFull(Site site){
        super(site);
    }

    public String getAppId() {
        return site.getApp_id();
    }
    public String getGuid() {
        return site.getGuid();
    }


    public String getAppStore() {
        if(site.getApp_store_id()!= -1)
            return APP_STORE_ID.getEnum(site.getApp_store_id()).name();
        else
            return "";
    }

    public String getHygieneList() { 
        return MetadataAPI.getSingleSelectedHygiene(site.getHygiene_list());
    }

    public List<String> getCategories_tier_1_list(){ 
        return MetadataAPI.getValues(MetadataType.CATEGORY_BY_ID, site.getCategories_tier_1_list());
    }
    public List<String> getCategories_tier_2_list(){ 
        return MetadataAPI.getValues(MetadataType.CATEGORY_BY_ID, site.getCategories_tier_2_list());
    }

    public List<String> getOptInHygieneList() {
        return MetadataAPI.getSelectedHygienes(site.getOpt_in_hygiene_list());
    }

    public IncExcList getTier1IncExcCategories(){
        String cIds =site.getCategory_list_inc_exc_tier_1();
        boolean isIncList =  !site.isIs_category_list_excluded();
        List<String> categoryList = MetadataAPI.getValues(MetadataType.CATEGORY_BY_ID, cIds);
        return new IncExcList(isIncList, categoryList);
    }
    public IncExcList getTier2IncExcCategories(){
        String cIds =site.getCategory_list_inc_exc_tier_2();
        boolean isIncList =  !site.isIs_category_list_excluded();
        List<String> categoryList = MetadataAPI.getValues(MetadataType.CATEGORY_BY_ID, cIds);
        return new IncExcList(isIncList, categoryList);
    }

    public List<String> getCreativeAttrs() {
        String crids=  site.getCreative_attr_inc_exc();
        List<String> creativeAttrList = MetadataAPI.getValues(MetadataType.CREATIVE_ATTRIBUTES_BY_ID, crids);
        return creativeAttrList;
    }
    public IncExcList getIncExcAdvertisers() {
        String ids=  site.getAdvertiser_json_array();
        List<String> incexcList = MetadataAPI.getValues(MetadataType.ACCOUNT_AS_META_BY_ID, ids);
        boolean isIncList =  !site.isIs_advertiser_excluded();
        return new IncExcList(isIncList, incexcList);
    }
    public IncExcList getIncExcCampaign() {
        String ids=  site.getCampaign_json_array();
        String str = ids.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
        String strSplit[] = str.split(",");
        boolean isFirst=true;
        StringBuffer sbuuf = new StringBuffer("");
        for(String element:strSplit){
            String elementSplit[] = element.split("\\|");
            if(elementSplit.length>1){
                if(isFirst){
                    isFirst=false;
                }else{
                    sbuuf.append(",");
                }
                sbuuf.append(elementSplit[1]);
            }
        }
        
        List<String> incexcList = MetadataAPI.getValues(MetadataType.CAMPAIGN_AS_META_BY_ID, "["+sbuuf.toString()+"]");
        boolean isIncList =  !site.isIs_advertiser_excluded();
        return new IncExcList(isIncList, incexcList);
    }
    public String getSitePlatform() {
        if(site.getSite_platform_id() != null)
            return site.getSite_platform_id().name();
        else
            return "";
    }

    public String getUrlExclusion() {
        String excList = site.getUrl_exclusion();
        if(excList == null)
            return "";

        String[] tmp = excList.split(",");
        String excListStr = "";
        for (String string : tmp) {
            excListStr += string+"\n";
        }
        return excListStr;
    }
    public String getBilling_rules_json(){
        return site.getBilling_rules_json();
    }
    public String getNativeLayout(){
        if(site.getNative_layout() != null){
            NativeLayoutId.getEnum(site.getNative_layout()).getName();
        }
        return "";
    }
    
    public String getNativeTitleKeyName(){
        return site.getNative_title_keyname();
    }

    public String getNativeTitleChars(){
        if( site.getNative_title_maxchars() != null){
            return site.getNative_title_maxchars()+"";
        }
        return "";
    }

    public String getNativeScreenShotKeyName(){
        return site.getNative_screenshot_keyname();
    }
    public String getNativeScreenShotImagesize(){
        if(site.getNative_screenshot_imagesize() != null){
            NativeScreenShotImageSize.getEnum(site.getNative_screenshot_imagesize()).getName();
        }
        return "";
    }

    public String getNativeLandingUrlKeyName(){
        return site.getNative_landingurl_keyname();
    }

    public String getNativeCallToActionKeyName(){
        return site.getNative_call_to_action_keyname();
    }

    public String getNativeIconKeyName(){
        return site.getNative_icon_keyname();
    }
    
    public String getNativeIconImagesize(){
        if(site.getNative_icon_imagesize() != null){
            NativeIconImageSize.getEnum(site.getNative_icon_imagesize()).getName();
        }
        return "";
    }

    public String getNativeDescriptionKeyName(){
        return site.getNative_description_keyname();
    }
    public String getNativeDescriptionChars(){
        if( site.getNative_description_maxchars() != null){
            return site.getNative_description_maxchars()+"";
        }
        return "";
    }
    public String getNativeRatingCountKeyName(){
        return site.getNative_rating_count_keyname();
    }


    //    
    //    public boolean isAllow_house_ads() {
    //        return allow_house_ads;
    //    }
    //    public void setAllow_house_ads(boolean allow_house_ads) {
    //        this.allow_house_ads = allow_house_ads;
    //    }
    //
    //    public String getBilling_rules_json() {
    //        return billing_rules_json;
    //    }
    //    public void setBilling_rules_json(String billing_rules_json) {
    //        this.billing_rules_json = billing_rules_json;
    //    }

}
