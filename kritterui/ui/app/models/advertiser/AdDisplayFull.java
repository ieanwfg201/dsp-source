package models.advertiser;

import java.util.List;
import services.DataAPI;
import services.MetadataAPI;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.BidType;
import com.kritter.constants.CreativeMacroQuote;
import com.kritter.constants.ExtClickType;
import com.kritter.constants.MetadataType;
import com.kritter.constants.Protocol;

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
	public String getProtocol() {
		return Protocol.getEnum(ad.getProtocol()).getName();
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
	public double getExpectedCtr() {return ad.getExpected_ctr();}
	public String Bidtype() {
        return BidType.getEnum(ad.getBidtype()).getName();
    }
	public String getExternalimptracker(){
	    return ad.getExternal_imp_tracker();
	}
	public String getExternalclicktracker(){
	    return ad.getExternal_click_tracker();
	}
	public String getFreqCapDisplay(){
		if(ad.isIs_frequency_capped()){
			StringBuffer sbuff = new StringBuffer("");
			if(ad.isClick_freq_cap()){
				sbuff.append("CLICK -> FreqPeruser: ");
				sbuff.append(ad.getClick_freq_cap_count());
				
				if(ad.getClick_freq_cap_type() ==1){
					sbuff.append(" FreqType: Life ");
				}else{
					sbuff.append(" FreqType: ByHour Duration:");
					sbuff.append(ad.getClick_freq_time_window());
				}
			}
			if(ad.isImp_freq_cap()){
				sbuff.append(" Imp -> FreqPeruser: ");
				sbuff.append(ad.getImp_freq_cap_count());
				
				if(ad.getImp_freq_cap_type() ==1){
					sbuff.append(" FreqType: Life ");
				}else{
					sbuff.append(" FreqType: ByHour Duration:");
					sbuff.append(ad.getImp_freq_time_window());
				}
			}
			return sbuff.toString();
		}
		return "Not Enabled";
	}
    public List<String> getClickMacro() {
        return MetadataAPI.getCreativeMacroByName(ad.getClickMacro());
    }
    public String getClickMacroquote(){
        CreativeMacroQuote c = CreativeMacroQuote.getEnum(ad.getClickMacroQuote());
        if(c != null){
            return c.getName();
        }
        return "";
    }
    public String getExtclickType(){
        ExtClickType c = ExtClickType.getEnum(ad.getExtclickType());
        if(c != null){
            return c.getName();
        }
        return "";
    }
    public List<String> getImpMacro() {
        return MetadataAPI.getCreativeMacroByName(ad.getImpMacro());
    }
    public String getImpMacroquote(){
        CreativeMacroQuote c = CreativeMacroQuote.getEnum(ad.getImpMacroQuote());
        if(c != null){
            return c.getName();
        }
        return "";
    }

}
