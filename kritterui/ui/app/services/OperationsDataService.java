package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;
import scala.Option;

import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdList;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.ext_site.Ext_site;
import com.kritter.api.entity.ext_site.Ext_site_input;
import com.kritter.api.entity.ext_site.Ext_site_list;
import com.kritter.api.entity.insertion_order.IOListEntity;
import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.api.entity.insertion_order.Insertion_Order_List;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.constants.AdAPIEnum;
import com.kritter.constants.Ext_siteEnum;
import com.kritter.constants.IOStatus;
import com.kritter.constants.PageConstants;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.api.def.ApiDef;

public class OperationsDataService {
	
	 
	public static List<Site> siteApprovalQueueData(Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		Connection con = null;
		List<Site> sites = null; 
		try{
		    con = DB.getConnection();
			SiteListEntity siteListEntity = new SiteListEntity();
			siteListEntity.setPage_no(PageConstants.start_index);
			if(status.nonEmpty())
				siteListEntity.setStatus_id(StatusIdEnum.valueOf(status.get()).getCode());
			else
				siteListEntity.setStatus_id(StatusIdEnum.Pending.getCode()); 
			siteListEntity.setPage_size(PageConstants.page_size);  

			if(pageNo.nonEmpty())
				siteListEntity.setPage_no(pageNo.get()-1);
			if(pageSize.nonEmpty())
				siteListEntity.setPage_size(pageSize.get());
			SiteList siteList = ApiDef.list_site(con,siteListEntity);
			if(siteList.getMsg().getError_code()==0){
				if( siteList.getSite_list().size()>0){
					sites=  siteList.getSite_list();
				}
			}else{
				sites = new ArrayList<Site>();
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching site list", e);
		}
		finally{
			try {
			    if(sites == null){
			        sites = new ArrayList<Site>();
			    }
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of sites in PublisherController",e);
			}
		}
		return sites; 
	}
    public static List<Ext_site> extsiteApprovalQueueData(Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
        Connection con = null;
        List<Ext_site> ext_sites = null; 
        try{
            con = DB.getConnection();
            Ext_site_input ext_site_input = new Ext_site_input();
            ext_site_input.setPage_no(PageConstants.start_index);
            ext_site_input.setExt_siteenum(Ext_siteEnum.get_unapproved_ext_site); 
            if(status.nonEmpty()){
                String str = status.get();
                if("Pending".equalsIgnoreCase(str)){
                    ext_site_input.setExt_siteenum(Ext_siteEnum.get_unapproved_ext_site);
                }else if("Active".equalsIgnoreCase(str)){
                    ext_site_input.setExt_siteenum(Ext_siteEnum.get_approved_ext_site);
                }else if("Rejected".equalsIgnoreCase(str)){
                    ext_site_input.setExt_siteenum(Ext_siteEnum.get_rejected_ext_site);
                }
                
            }
                
            ext_site_input.setPage_size(PageConstants.page_size);  

            if(pageNo.nonEmpty())
                ext_site_input.setPage_no(pageNo.get()-1);
            if(pageSize.nonEmpty())
                ext_site_input.setPage_size(pageSize.get());
            Ext_site_list ext_site_List = ApiDef.various_get_ext_site(con, ext_site_input);
            if(ext_site_List.getMsg().getError_code()==0){
                if( ext_site_List.getExt_site_list().size()>0){
                    ext_sites =  ext_site_List.getExt_site_list();
                }
            }else{
                ext_sites = new ArrayList<Ext_site>();
            }
        }catch(Exception e){
            play.Logger.error(e.getMessage()+".Error fetching ext site list", e);
        }
        finally{
            try {
                if(ext_sites == null){
                    ext_sites = new ArrayList<Ext_site>();
                }
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing DB connection while fetching list of ext sites",e);
            }
        }
        return ext_sites; 
    }
	
	public static List<Ad> adApprovalQueueData(Option<String> status, Option<Integer> pageNoIn,   Option<Integer> pageSize){
		List<Ad> adList = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			AdListEntity adListEntity = new AdListEntity();
			adListEntity.setAdenum(AdAPIEnum.list_ad_by_status); 
			if(status.nonEmpty())
				adListEntity.setStatudIdEnum(StatusIdEnum.valueOf(status.get()));
			else
				adListEntity.setStatudIdEnum(StatusIdEnum.Pending);  
			adListEntity.setPage_no(PageConstants.start_index);
			adListEntity.setPage_size(PageConstants.page_size);
			 
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
			    if(adList == null){
			        adList = new ArrayList<Ad>();
			    }
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return adList;
	}
	
	public static List<Insertion_Order> ioApprovalQueueData(Option<String> status, Option<Integer> pageNoIn,   Option<Integer> pageSize){		
		List<Insertion_Order> ioList = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			IOListEntity ioListEntity = new IOListEntity();
			ioListEntity.setPage_no(PageConstants.start_index);
			if(status.nonEmpty())
				ioListEntity.setStatus(IOStatus.valueOf(status.get()));
			else
				ioListEntity.setStatus(IOStatus.NEW);
			ioListEntity.setPage_size(PageConstants.page_size);
			
			Insertion_Order_List ioListStatus = ApiDef.list_io_by_status(con, ioListEntity);
					
			if(ioListStatus.getMsg().getError_code()==0){ 
					ioList = ioListStatus.getInsertion_order_list(); 
			}
		}catch(Exception e){
			Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
			    if(ioList == null){
			        ioList = new ArrayList<Insertion_Order>();
			    }   
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return ioList;
	} 
	
	public static List<Insertion_Order> ioByAccount( String guid,  String  status, Option<Integer> pageNoIn,   Option<Integer> pageSize){		
		List<Insertion_Order> ioList = new ArrayList<Insertion_Order>();
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			IOListEntity ioListEntity = new IOListEntity();
			ioListEntity.setPage_no(PageConstants.start_index); 
			ioListEntity.setStatus(IOStatus.valueOf(status));
			ioListEntity.setPage_size(PageConstants.page_size);
			ioListEntity.setAccount_guid(guid);
			
			Insertion_Order_List ioListStatus = ApiDef.list_io_by_account_guid_by_status(con, ioListEntity);
					
			if(ioListStatus.getMsg().getError_code()==0){ 
					ioList = ioListStatus.getInsertion_order_list(); 
			}
		}catch(Exception e){
			Logger.error(e.getMessage()+".Error fetching IO list",e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching IO's",e);
			}
		}
		return ioList;
	} 
	
	 
}
