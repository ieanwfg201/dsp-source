package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.kritter.api.entity.deal.PMPMessagePair;
import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;
import com.kritter.api.entity.site.Site;
import models.formelements.SelectOption;
import play.Logger;
import play.db.DB;
import play.libs.Scala;
import scala.Option;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountList;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.account.ListEntity;
import com.kritter.api.entity.account_budget.Account_Budget_Msg;
import com.kritter.api.entity.account_budget.Account_budget;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdList;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.api.entity.campaign_budget.CampaignBudgetList;
import com.kritter.api.entity.campaign_budget.CampaignBudgetListEntity;
import com.kritter.api.entity.campaign_budget.Campaign_budget;
import com.kritter.api.entity.creative_banner.CreativeBannerList;
import com.kritter.api.entity.creative_banner.CreativeBannerListEntity;
import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.native_icon.NativeIconList;
import com.kritter.api.entity.native_icon.NativeIconListEntity;
import com.kritter.api.entity.native_screenshot.NativeScreenshotList;
import com.kritter.api.entity.native_screenshot.NativeScreenshotListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.targeting_profile.TargetingProfileList;
import com.kritter.api.entity.targeting_profile.TargetingProfileListEntity;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.api.entity.video_info.VideoInfoList;
import com.kritter.api.entity.video_info.VideoInfoListEntity;
import com.kritter.constants.Account_Type;
import com.kritter.constants.AdAPIEnum;
import com.kritter.constants.CampaignQueryEnum;
import com.kritter.constants.CreativeBannerAPIEnum;
import com.kritter.constants.CreativeContainerAPIEnum;
import com.kritter.constants.NativeIconAPIEnum;
import com.kritter.constants.NativeScreenshotAPIEnum;
import com.kritter.constants.PageConstants;
import com.kritter.constants.TargetingProfileAPIEnum;
import com.kritter.constants.VideoInfoAPIEnum;
import com.kritter.entity.native_props.demand.NativeIcon;
import com.kritter.entity.native_props.demand.NativeScreenshot;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.kritterui.api.def.ApiDef;

public class DataAPI {
	
	public static Campaign_budget getCampaignBudget(int campaignId){
		Connection con = null;
		Campaign_budget campaignBudget = new Campaign_budget();
		try{
			con = DB.getConnection();
			CampaignBudgetListEntity campaignBudgetlistEntity = new CampaignBudgetListEntity();

			campaignBudgetlistEntity.setCampaign_id(campaignId);
			CampaignBudgetList cl = ApiDef.get_campaign_budget(con, campaignBudgetlistEntity);
			if(cl.getMsg().getError_code()==0){
				if( cl.getCampaig_budget_list().size()>0){
					campaignBudget = cl.getCampaig_budget_list().get(0);
				}
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching campaign budget for campaign with id = " +campaignId, e);
		}
		finally{
			try {
				if(con != null)
					con.close();
			} catch (SQLException e) {
				Logger.error("Error closing DB connection in getCampaign in CampaignCOntroller",e);
			}
		}
		return campaignBudget;
	}
	
	public static List<SelectOption> getTargetingOptions(String accountGuid){
		List<SelectOption> targetingOptions = new ArrayList<SelectOption>();;
	 
		List<Targeting_profile> tps  = getTPs(accountGuid, Scala.Option(1), Scala.Option(Integer.MAX_VALUE)); 
		for (Targeting_profile tp : tps) {
			targetingOptions.add(new SelectOption(tp.getName(), tp.getGuid()));
		}
		 
		return targetingOptions;
	}
	
	public static List<Ad> getAds(int campaign_id, Option<Integer> pageNoIn,   Option<Integer> pageSize){
		List<Ad> adList = null;
		Connection con = null; 
		try{
			con = DB.getConnection(); 
			AdListEntity adListEntity = new AdListEntity();
			adListEntity.setCampaign_id(campaign_id);
			adListEntity.setAdenum(AdAPIEnum.list_ad_by_campaign_with_tp_name); 
			 
			AdList adl = ApiDef.various_get_ad(con,adListEntity);
			if(adl.getMsg().getError_code()==0){
				if( adl.getAdlist().size()>0){
					adList = adl.getAdlist();
				}
			}
		}catch(Exception e){
			Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection ",e);
			}
		}
		return adList;
	}
	
	
	public static List<Account> getAccounts(Account_Type accountType,  Option<Integer> pageNo,   Option<Integer> pageSize){
		List<Account> accounts = null;
		Connection con = null; 
		try{
			con = DB.getConnection(); 
			ListEntity listEntity = new ListEntity(); 
			listEntity.setAccount_type(accountType);
			if(pageNo.nonEmpty())
				listEntity.setPage_no(pageNo.get());
			else
				listEntity.setPage_no(PageConstants.start_index);
			if(pageSize.nonEmpty())
				listEntity.setPage_size(pageSize.get());
			else
				listEntity.setPage_size(PageConstants.page_size);
			 
			AccountList accountList = ApiDef.listAccount(con, listEntity);
			
			if(accountList.getMsg().getError_code()==0){
				if( accountList.getAccount_list().size()>0){
					accounts = accountList.getAccount_list();
				}
			}
		}catch(Exception e){
			Logger.error(e.getMessage()+".Error fetching Account List",e);
		}
		finally{
			try {
				if(con != null)
					con.close();
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of accounts", e);
			}
		}
		return accounts;
		
	}
	
	public static Account_budget getAccountBudget(String accountGuid ){
		Account_budget  budget = null;
		Connection con = null;
		try{ 
		    con = DB.getConnection(); 
			budget = new Account_budget();
			budget.setAccount_guid(accountGuid); 
			 
			Account_Budget_Msg abMsg = ApiDef.get_Account_Budget(con, budget);
			if(abMsg.getMsg().getError_code()==0){
				budget = abMsg.getAccount_budget();
			}else
				budget = null;
			 
		}catch(Exception e){
			Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return budget;
	}
	
	public static Ad getAd(int  id ){
		Ad ad = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			AdListEntity adListEntity = new AdListEntity();
			adListEntity.setId(id);
			adListEntity.setAdenum(AdAPIEnum.get_ad); 
			 
			AdList adl = ApiDef.various_get_ad(con,adListEntity);
			if(adl.getMsg().getError_code()==0){
				if( adl.getAdlist().size()>0){
					ad = adl.getAdlist().get(0);
				}
			}
		}catch(Exception e){
			Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return ad;
	}
	

	public static Creative_container getCreativeContainer(int  id ){
		Creative_container creativeContainer  = null; 
		Connection con = null; 
		
		try{
			CreativeContainerListEntity ccle = new CreativeContainerListEntity();
			ccle.setId(id);
			ccle.setCcenum(CreativeContainerAPIEnum.get_creative_container);
			con = DB.getConnection();
			CreativeContainerList ccList = ApiDef.various_get_creative_container(con, ccle); 
			if(ccList.getMsg().getError_code() !=-1 && ccList.getCclist().size()==1){
				creativeContainer  = ccList.getCclist().get(0);   
			}else{
				creativeContainer  = null;
				Logger.error("Unable to find Creative Container for given id");
			}
		}catch(Exception e){
			Logger.error(e.getMessage()+".Error fetching Creative Container",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of creatives",e);
			}
		}
		return creativeContainer;
	}
	public static Account getAccountByGuid(String accountGuid){
		Account account = new Account();
		if(accountGuid !=null){
			account.setGuid(accountGuid);

			Connection dbConnection = null;
			try{
			    dbConnection = DB.getConnection(); 
				AccountMsgPair actMsgPair = ApiDef.get_Account_By_Guid(dbConnection, account);
				Message msg = actMsgPair.getMsg();
				if(msg.getError_code()==0){
					account = actMsgPair.getAccount(); 
				}else
					account = null;
			}catch (Exception e) {
				Logger.debug("Error while requesting user object");
			}
			finally{
				try {
					if(dbConnection != null)
						dbConnection.close();

				} catch (SQLException e) {
					Logger.debug("Error while closing DB Connection.", e);
				}
			}
		}
		return account;
	}

	public static PrivateMarketPlaceApiEntity getPMPDealByGuid(String pmpGuid){
		PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity = new PrivateMarketPlaceApiEntity();
		if(pmpGuid !=null){
			privateMarketPlaceApiEntity.setDealId(pmpGuid);

			Connection dbConnection = null;
			try{
				dbConnection = DB.getConnection();
				PMPMessagePair actMsgPair = ApiDef.get_PMP_deal_By_Guid(dbConnection, privateMarketPlaceApiEntity);
				Message msg = actMsgPair.getMsg();
				if(msg.getError_code()==0){
					privateMarketPlaceApiEntity = actMsgPair.getPrivateMarketPlaceApiEntity();
				}else
					privateMarketPlaceApiEntity = null;
			}catch (Exception e) {
				Logger.debug("Error while requesting PMP object");
			}
			finally{
				try {
					if(dbConnection != null)
						dbConnection.close();

				} catch (SQLException e) {
					Logger.debug("Error while closing DB Connection.", e);
				}
			}
		}
		return privateMarketPlaceApiEntity;
	}

	public static List<PrivateMarketPlaceApiEntity> getPMPDeals()
	{
		Connection dbConnection = null;
		List<PrivateMarketPlaceApiEntity> privateMarketPlaceApiEntityList = new ArrayList<PrivateMarketPlaceApiEntity>();

		try
		{
			dbConnection = DB.getConnection();
			List<PMPMessagePair> actMsgPair = ApiDef.get_PMP_deals(dbConnection);

			if(null != actMsgPair)
			{

				for (PMPMessagePair pmpMessagePair : actMsgPair)
				{
					Message msg = pmpMessagePair.getMsg();

					if (msg.getError_code() == 0)
						privateMarketPlaceApiEntityList.add(pmpMessagePair.getPrivateMarketPlaceApiEntity());
				}
			}
		}
		catch (Exception e)
		{
			Logger.debug("Error while requesting PMP objects");
		}
		finally
		{
			try
			{
				if(dbConnection != null)
					dbConnection.close();
			}
			catch (SQLException e)
			{
				Logger.debug("Error while closing DB Connection.", e);
			}
		}

		return privateMarketPlaceApiEntityList;
	}

	public static List<Targeting_profile> getTPs(String accountGuid, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Targeting_profile> tpList = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			TargetingProfileListEntity tple = new TargetingProfileListEntity();
			tple.setTpEnum(TargetingProfileAPIEnum.list_active_targeting_profile_by_account); 
			tple.setAccount_guid(accountGuid);
			 
			TargetingProfileList tpl = ApiDef.various_get_targeting_profile(con,tple);
			if(tpl.getMsg().getError_code()==0){
				if( tpl.getTplist().size()>0){
					tpList = tpl.getTplist();
				}
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
			    if(tpList == null){
			        tpList = new ArrayList<Targeting_profile>();
			    }
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return tpList;
	}
	   public static Targeting_profile getTargetingProfile(String guid){
	        Connection con = null;
	        Targeting_profile tp = null; 
	        try{
	            con = DB.getConnection();
	            TargetingProfileListEntity tple = new TargetingProfileListEntity();
	            tple.setTpEnum(TargetingProfileAPIEnum.get_targeting_profile);
	            tple.setGuid(guid); 
	            TargetingProfileList tpl = ApiDef.various_get_targeting_profile(con,tple);
	            if(tpl.getMsg().getError_code()==0){
	                if( tpl.getTplist().size()>0){
	                    tp = tpl.getTplist().get(0);
	                }
	            }
	        }catch(Exception e){
	            play.Logger.error(e.getMessage()+".Error fetching campaign with id="+guid,e);
	        }
	        finally{
	            try {
	                if(con != null){
	                    con.close();
	                }
	            } catch (SQLException e) {
	                Logger.error("Error closing DB connection in getCampaign in CampaignCOntroller",e);
	            }
	        } 
	        return tp;
	    }

	
	public static List<Creative_container> getCreativeContainerList(String accountGuid,CreativeContainerAPIEnum listDef,
			 Option<Integer> pageNo, Option<Integer> pageSize){
		List<Creative_container> creativeContainerList = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			CreativeContainerListEntity cle = new CreativeContainerListEntity();
			cle.setAccount_guid(accountGuid);
			cle.setCcenum(listDef);
			 
			CreativeContainerList cl = ApiDef.various_get_creative_container(con,cle);
			if(cl.getMsg().getError_code()==0){
				if( cl.getCclist().size()>0){
					creativeContainerList=  cl.getCclist();
				}
			}else{
				creativeContainerList = new ArrayList<Creative_container>();
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of Creatives",e);
			}
		}
		return creativeContainerList;
	}
	 
	public static List<SelectOption> getCreativeOptions(String accountGuid){
		List<SelectOption> creativeOptions = new ArrayList<SelectOption>();
		List<Creative_container> cl = getCreativeContainerList(
		            accountGuid,
					CreativeContainerAPIEnum.list_creative_container_by_account,
					Scala.Option(0) ,
					Scala.Option(Integer.MAX_VALUE));
			
		for (Creative_container cc : cl) { 
			creativeOptions.add(new SelectOption(cc.getLabel(),cc.getId()+":"+ cc.getGuid()));
		} 
		return creativeOptions;
	}

	public static List<Creative_banner> getCreativeBannerList(String ids){
		List<Creative_banner> creativeBannerList = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			CreativeBannerListEntity cblistEntity = new CreativeBannerListEntity(); 
			cblistEntity.setGuid_list(ids);
			cblistEntity.setCbenum(CreativeBannerAPIEnum.get_creative_banner_by_ids);
			 
			CreativeBannerList cblist = ApiDef.various_get_creative_banner(con, cblistEntity);
			if(cblist.getMsg().getError_code()==0){
				if( cblist.getCblist().size()>0){
					creativeBannerList=  cblist.getCblist();
				}
			}else{
				creativeBannerList = new ArrayList<Creative_banner>();
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching creative banners",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of Creative Banner",e);
			}
		}
		return creativeBannerList;
	}
	public static List<VideoInfo> getDirectVideoList(String ids){
		List<VideoInfo> videoInfoList = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			VideoInfoListEntity videoInfolistEntity = new VideoInfoListEntity(); 
			videoInfolistEntity.setId_list(ids);
			videoInfolistEntity.setVideoenum(VideoInfoAPIEnum.get_video_info_by_ids);
			VideoInfoList cblist = ApiDef.various_get_video_info(con, videoInfolistEntity);
			if(cblist.getMsg().getError_code()==0){
				if( cblist.getCblist().size()>0){
					videoInfoList=  cblist.getCblist();
				}
			}else{
				videoInfoList = new ArrayList<VideoInfo>();
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching video info",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of video info",e);
			}
		}
		return videoInfoList;
	}
    public static List<NativeIcon> getNativeIconList(String ids){
        List<NativeIcon> nativeIconList = null;
        if(ids == null){
            return new ArrayList<NativeIcon>();
        }
        String idTrim = ids.trim().replaceAll("\\[", "").replaceAll("]", "");
        Connection con = null;
        try{
            con = DB.getConnection(); 
            NativeIconListEntity cblistEntity = new NativeIconListEntity(); 
            cblistEntity.setId_list(idTrim);
            cblistEntity.setNativeenum(NativeIconAPIEnum.get_native_icon_by_ids);
             
            NativeIconList cblist = ApiDef.various_get_native_icon(con, cblistEntity);
            if(cblist.getMsg().getError_code()==0){
                if( cblist.getCblist().size()>0){
                    nativeIconList =  cblist.getCblist();
                }
            }else{
                nativeIconList = new ArrayList<NativeIcon>();
            }
        }catch(Exception e){
            play.Logger.error(e.getMessage()+".Error fetching native icons",e);
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing DB connection while fetching list of Native Icon",e);
            }
        }
        return nativeIconList;
    }
    public static List<NativeScreenshot> getNativeScreenshotList(String ids){
        List<NativeScreenshot> nativeScreenshotList = null;
        if(ids == null){
            return new ArrayList<NativeScreenshot>();
        }
        String idTrim = ids.trim().replaceAll("\\[", "").replaceAll("]", "");
        Connection con = null;
        try{
            con = DB.getConnection(); 
            NativeScreenshotListEntity cblistEntity = new NativeScreenshotListEntity(); 
            cblistEntity.setId_list(idTrim);
            cblistEntity.setNativeenum(NativeScreenshotAPIEnum.get_native_screenshot_by_ids);
             
            NativeScreenshotList cblist = ApiDef.various_get_native_screenshot(con, cblistEntity);
            if(cblist.getMsg().getError_code()==0){
                if( cblist.getCblist().size()>0){
                    nativeScreenshotList =  cblist.getCblist();
                }
            }else{
                nativeScreenshotList = new ArrayList<NativeScreenshot>();
            }
        }catch(Exception e){
            play.Logger.error(e.getMessage()+".Error fetching native screenshot",e);
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing DB connection while fetching list of Native Screenshot",e);
            }
        }
        return nativeScreenshotList;
    }
    
    public static List<VideoInfo> getVideoInfoList(String ids){
        List<VideoInfo> nativeIconList = null;
        if(ids == null){
            return new ArrayList<VideoInfo>();
        }
        String idTrim = ids.trim().replaceAll("\\[", "").replaceAll("]", "");
        Connection con = null;
        try{
            con = DB.getConnection(); 
            VideoInfoListEntity cblistEntity = new VideoInfoListEntity(); 
            cblistEntity.setId_list(idTrim);
            cblistEntity.setVideoenum(VideoInfoAPIEnum.get_video_info_by_ids);
            VideoInfoList cblist = ApiDef.various_get_video_info(con, cblistEntity);
            if(cblist.getMsg().getError_code()==0){
                if( cblist.getCblist().size()>0){
                    nativeIconList =  cblist.getCblist();
                }
            }else{
                nativeIconList = new ArrayList<VideoInfo>();
            }
        }catch(Exception e){
            play.Logger.error(e.getMessage()+".Error fetching video infos",e);
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing DB connection while fetching list of video info",e);
            }
        }
        return nativeIconList;
    }


	public static List<Ad> getAdsByCampaign(String campaigns){
		List<Ad> adList = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			AdListEntity adListEntity = new AdListEntity();
//			adListEntity.setCampaign_id(campaign_id);
			adListEntity.setAdenum(AdAPIEnum.list_ad_by_campaign); 
			 
			AdList adl = ApiDef.various_get_ad(con,adListEntity);
			if(adl.getMsg().getError_code()==0){
				if( adl.getAdlist().size()>0){
					adList = adl.getAdlist();
				}
			}
		}catch(Exception e){
			Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return adList;
	}
	
	public static List<Campaign> getCampaignsByAdvertisers(String advertisers){
		List<Campaign> campaigns = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			CampaignListEntity cle = new CampaignListEntity(); 
			cle.setCampaignQueryEnum(CampaignQueryEnum.list_all_non_expired_campaign_of_account);
			CampaignList cl = ApiDef.list_campaign(con,cle);
			if(cl.getMsg().getError_code()==0){
				if( cl.getCampaign_list().size()>0){
					campaigns=  cl.getCampaign_list();
				}
			}else{
				campaigns = new ArrayList<Campaign>();
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return campaigns;
	}
	public static List<SelectOption> getCampaignsIdGuidByAdvertisers(String advertisers){
        List<SelectOption> campaignOptions = new ArrayList<SelectOption>();;
     
        
        List<Campaign> campaigns = null;
        Connection con = null;
        try{
            con = DB.getConnection(); 
            CampaignListEntity cle = new CampaignListEntity(); 
            cle.setCampaignQueryEnum(CampaignQueryEnum.list_all_non_expired_campaign_of_account);
            cle.setAccount_guid(advertisers);
            CampaignList cl = ApiDef.list_campaign(con,cle);
            if(cl.getMsg().getError_code()==0){
                if( cl.getCampaign_list().size()>0){
                    campaigns=  cl.getCampaign_list();
                }
            }else{
                campaigns = new ArrayList<Campaign>();
            }
            for (Campaign cp : campaigns) {
                campaignOptions.add(new SelectOption(cp.getName(), cp.getId()+"|"+cp.getGuid()));
            }

        }catch(Exception e){
            play.Logger.error(e.getMessage()+".Error fetching campaign list",e);
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
            }
        }
        return campaignOptions;
    }

	public static String fetchSiteName(Integer id)
	{
		Connection con = null;

		try {
			con = DB.getConnection();
			if(null != id) {
				Site site = ApiDef.get_site_by_inc_id(con, id.intValue());
				if(null != site)
					return site.getName();
			}
		}
		catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching  site list in DataApi",e);
		}
		finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (SQLException e) {
				Logger.error("Error closing DB connection in fetchSiteNames of DataApi",e);
			}
		}

		return null;
	}
}
