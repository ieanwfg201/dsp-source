package models.advertiser;

import java.util.List;
import services.DataAPI;
import services.MetadataAPI;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.BidType;
import com.kritter.constants.MetadataType;

public class AdDisplayFull extends AdDisplay{

	public AdDisplayFull(Ad ad){
		super(ad);
	}

	public CreativeDisplayFull getCreativeDisplay(){  
		int creativeId =  ad.getCreative_id() ;
		Creative_container cc =DataAPI.getCreativeContainer(creativeId);
		return new CreativeDisplayFull(cc);
	}

	public String getLanding_url() {
		return ad.getLanding_url();
	}
	public String getAdvDomain() {
        return ad.getAdv_domain();
    }

	public TargetingDisplayFull getTargetingFull() {
	    Targeting_profile tp = DataAPI.getTargetingProfile(ad.getTargeting_guid());	
		return new TargetingDisplayFull(tp);
	}

	public List<String> getHygieneList() { 
		return MetadataAPI.getSelectedHygienes(ad.getHygiene_list());
	}

	public List<String> getTier1Categories(){ 
		return MetadataAPI.getValues(MetadataType.CATEGORY_BY_ID, ad.getCategories_tier_1_list());
	}
    public List<String> getTier2Categories(){ 
        return MetadataAPI.getValues(MetadataType.CATEGORY_BY_ID, ad.getCategories_tier_2_list());
    }
	public List<String> getMMATier1(){ 
		return MetadataAPI.getValues(MetadataType.MMA_INDUSTRY_BY_IDS, ad.getMma_tier_1_list());
	}
    public List<String> getMMATier2(){ 
        return MetadataAPI.getValues(MetadataType.MMA_INDUSTRY_BY_IDS, ad.getMma_tier_2_list());
    }
	
	public String getMarketplace() {
		return ad.getMarketplace_id().name();
	}

	public double getInternalMaxBid() {
		return ad.getInternal_max_bid();
	}

	public double getAdvertiserBid() {
		return ad.getAdvertiser_bid();
	}
	public String getTracking_partner() {
        return ad.getTracking_partner().getName();
    }

	public double getCpaGoal() {
        return ad.getCpa_goal();
    } 
	public String Bidtype() {
        return BidType.getEnum(ad.getBidtype()).getName();
    }
	public String getExternalimptracker(){
	    return ad.getExternal_imp_tracker();
	}
	public String getExternalclicktracker(){
	    return ad.getExternal_click_tracker();
	}
}
