package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kritter.api.entity.deal.PMPList;
import com.kritter.api.entity.deal.PMPListEntity;
import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;
import models.EntityList;
import models.EntityListFilter;
import models.Constants.PageType;
import play.Logger;
import play.db.DB;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountList;
import com.kritter.api.entity.account.ListEntity;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentInputEntity;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentList;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdList;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.adxbasedexchangesmetadata.AdxBasedExchangesMetadatList;
import com.kritter.api.entity.adxbasedexchangesmetadata.AdxBasedExchangesMetadataListEntity;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.ext_site.Ext_site;
import com.kritter.api.entity.ext_site.Ext_site_input;
import com.kritter.api.entity.ext_site.Ext_site_list;
import com.kritter.api.entity.iddefinition.Iddefinition;
import com.kritter.api.entity.iddefinition.IddefinitionInput;
import com.kritter.api.entity.iddefinition.IddefinitionList;
import com.kritter.api.entity.isp_mapping.Isp_mapping;
import com.kritter.api.entity.isp_mapping.Isp_mappingList;
import com.kritter.api.entity.isp_mapping.Isp_mappingListEntity;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.api.entity.targeting_profile.TargetingProfileList;
import com.kritter.api.entity.targeting_profile.TargetingProfileListEntity;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.Account_Type;
import com.kritter.constants.AdAPIEnum;
import com.kritter.constants.AdxBasedExchangesMetadataQueryEnum;
import com.kritter.constants.CampaignQueryEnum;
import com.kritter.constants.CreativeContainerAPIEnum;
import com.kritter.constants.Ext_siteEnum;
import com.kritter.constants.IddefinitionEnum;
import com.kritter.constants.IddefinitionType;
import com.kritter.constants.Isp_mappingEnum;
import com.kritter.constants.PageConstants;
import com.kritter.constants.RetargetingSegmentEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.TargetingProfileAPIEnum;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;
import com.kritter.entity.retargeting_segment.RetargetingSegment;
import com.kritter.kritterui.api.def.ApiDef;

public class EntityListDataService {
	
	 
	@SuppressWarnings("unchecked")
	public static <T> EntityList<T> listData(EntityListFilter listDataFilter, T t){
		Connection con = null; 
		try{
		    con = DB.getConnection(); 
		    EntityList<?> entityList  = null;
		    switch (listDataFilter.getEntityType()) {
				case account:
					
					ListEntity listEntity = new ListEntity();
					listEntity.setPage_no(listDataFilter.getPageNumber());
					listEntity.setPage_size(listDataFilter.getPageSize());
					listEntity.setAccount_type(Account_Type.valueOf( listDataFilter.getAccountType()));
					listEntity.setStatus( listDataFilter.getStatus()); 
//					StatusIdEnum.Rejected.getCode();
					AccountList accountList = ApiDef.listAccountByStatus(con, listEntity);
					List<Account> accounts =  accountList.getAccount_list();
					entityList = new EntityList<Account>(accounts, accounts.size()); 
					break;
				case targeting_profile:
					TargetingProfileListEntity tple = new TargetingProfileListEntity();
					tple.setTpEnum(TargetingProfileAPIEnum.list_active_targeting_profile_by_account); 
					tple.setAccount_guid(listDataFilter.getAccountGuid());
//					tple.setPage_no(listDataFilter.getPageNumber());
//					tple.setPage_size(listDataFilter.getPageSize());
					TargetingProfileList targetingProfileList = ApiDef.various_get_targeting_profile(con,tple);

					if(targetingProfileList.getMsg().getError_code()==0){
						if( targetingProfileList.getTplist().size()>0){ 
							entityList = new EntityList<Targeting_profile>(targetingProfileList.getTplist(), targetingProfileList.getTplist().size()); 
						}
					}else{
						entityList = new EntityList<Targeting_profile>(new ArrayList<Targeting_profile>(), 0);
					}
					break;
				case campaign:
					CampaignListEntity cle = new CampaignListEntity();
					if(listDataFilter.getStatus() != null){ 
					    if(StatusIdEnum.Expired == listDataFilter.getStatus()){
					        cle.setCampaignQueryEnum(CampaignQueryEnum.list_all_expired_campaign_of_account);
					    }else{
					        cle.setCampaignQueryEnum(CampaignQueryEnum.list_non_expired_campaign_by_status);
					    }
						cle.setStatusIdEnum(listDataFilter.getStatus());
					}else{
						cle.setCampaignQueryEnum(CampaignQueryEnum.list_all_non_expired_campaign_of_account);
					}  
					cle.setAccount_guid(listDataFilter.getAccountGuid()); 
					cle.setPage_no(listDataFilter.getPageNumber());
					cle.setPage_size(listDataFilter.getPageSize());
					CampaignList cl = ApiDef.list_campaign(con,cle);
					entityList = new EntityList<Campaign>(cl.getCampaign_list(), cl.getCampaign_list().size()); 
					
					break;
					
				case site:

					SiteListEntity siteListEntity = new SiteListEntity();
					siteListEntity.setPage_no(PageConstants.start_index); 
					if(listDataFilter.getStatus() != null)
						siteListEntity.setStatus_id(listDataFilter.getStatus().getCode());
					else
						siteListEntity.setStatus_id(StatusIdEnum.Pending.getCode());  
		
					
					
					siteListEntity.setPage_no(listDataFilter.getPageNumber());
					siteListEntity.setPage_size(listDataFilter.getPageSize());
					SiteList siteList = null;
					if(listDataFilter.getAccountGuid() != null) { 
						siteListEntity.setPub_guid(listDataFilter.getAccountGuid());
						siteList = ApiDef.list_site_by_account_guid(con,siteListEntity);
					}else{
						siteList = ApiDef.list_site(con,siteListEntity);
					}
						
					if(siteList.getMsg().getError_code()==0){ 
						entityList = new EntityList<Site>(siteList.getSite_list(), siteList.getSite_list().size()); 
					}else{
						entityList = new EntityList<Site>(new ArrayList<Site>(), 0);
					}
					break;
                case extsite:
                    Ext_site_input ext_site_input = new Ext_site_input();
                    ext_site_input.setPage_no(PageConstants.start_index); 
                    ext_site_input.setExt_siteenum(Ext_siteEnum.get_unapproved_ext_site); 
                    ext_site_input.setId_list(listDataFilter.getExchangeId()+"");
                    String osId = listDataFilter.getOsId().trim();
                    ext_site_input.setOsid_list(osId);
                    boolean osfilter = true;
                    if(osId == null || "".equals(osId.trim()) || "ALL".equalsIgnoreCase(osId.trim())){
                        osfilter=false;
                    }
                        
                    if(listDataFilter.getStatus() != null){
                        String str = listDataFilter.getStatus().toString();
                        if("Pending".equalsIgnoreCase(str)){
                            if(osfilter){
                                ext_site_input.setExt_siteenum(Ext_siteEnum.get_unapproved_ext_site_by_pub_os);
                            }else{
                                ext_site_input.setExt_siteenum(Ext_siteEnum.get_unapproved_ext_site_by_pub);
                            }
                        }else if("Active".equalsIgnoreCase(str)){
                            if(osfilter){
                                ext_site_input.setExt_siteenum(Ext_siteEnum.get_approved_ext_site_by_pub_os);
                            }else{
                                ext_site_input.setExt_siteenum(Ext_siteEnum.get_approved_ext_site_by_pub);
                            }
                        }else if("Rejected".equalsIgnoreCase(str)){
                            if(osfilter){
                                ext_site_input.setExt_siteenum(Ext_siteEnum.get_rejected_ext_site_by_pub_os);
                            }else{
                                ext_site_input.setExt_siteenum(Ext_siteEnum.get_rejected_ext_site_by_pub);
                            }
                        }
                        
                    }
                    ext_site_input.setOsid_list(listDataFilter.getOsId());
                    ext_site_input.setPage_no(listDataFilter.getPageNumber());
                    ext_site_input.setPage_size(listDataFilter.getPageSize());
                    Ext_site_list ext_siteList = null;
                    ext_siteList = ApiDef.various_get_ext_site(con, ext_site_input);
                    if(ext_siteList.getMsg().getError_code()==0){ 
                        entityList = new EntityList<Ext_site>(ext_siteList.getExt_site_list(), ext_siteList.getExt_site_list().size()); 
                    }else{
                        entityList = new EntityList<Ext_site>(new ArrayList<Ext_site>(), 0);
                    }
                    break;
				case creative:

					CreativeContainerListEntity creativeContainerListEntity = new CreativeContainerListEntity();
					creativeContainerListEntity.setAccount_guid(listDataFilter.getAccountGuid());
					if(listDataFilter.getStatus() != null){
					    creativeContainerListEntity.setCcenum(CreativeContainerAPIEnum.list_creative_container_by_status);
					    creativeContainerListEntity.setStatus_id(StatusIdEnum.getEnum(listDataFilter.getStatus().getCode()));
					}else{
					    creativeContainerListEntity.setCcenum(CreativeContainerAPIEnum.list_creative_container_by_account);
                        
					}
        			creativeContainerListEntity.setPage_no(listDataFilter.getPageNumber());
					creativeContainerListEntity.setPage_size(listDataFilter.getPageSize());
					 
					CreativeContainerList creativeContainerList = ApiDef.various_get_creative_container(con,creativeContainerListEntity);
					if(creativeContainerList.getMsg().getError_code()==0){
						if( creativeContainerList.getCclist().size()>0){ 
							entityList = new EntityList<Creative_container>(creativeContainerList.getCclist(), creativeContainerList.getCclist().size()); 
						}
					}else{
						entityList = new EntityList<Creative_container>(new ArrayList<Creative_container>(), 0);
					}
					break;
					
				case ad:

					AdListEntity adListEntity = new AdListEntity();
					if(listDataFilter.getCampaignId() != 0 ){
						adListEntity.setCampaign_id(listDataFilter.getCampaignId());
						adListEntity.setAdenum(AdAPIEnum.list_ad_by_campaign_with_tp_name); 
					}else if(listDataFilter.getStatus() != null){ 
						adListEntity.setStatudIdEnum(listDataFilter.getStatus());
						if(PageType.operations == listDataFilter.getPageType()){
						    adListEntity.setAdenum(AdAPIEnum.list_ad_by_status_of_non_expired_campaign);
						}else{
						    adListEntity.setAdenum(AdAPIEnum.list_ad_by_status);
						}
					}
						
					adListEntity.setPage_no(listDataFilter.getPageNumber());
					adListEntity.setPage_size(listDataFilter.getPageSize()); 
					AdList adList = ApiDef.various_get_ad(con,adListEntity); 
					  
					if(adList.getMsg().getError_code()==0){
						if( adList.getAdlist().size()>0){ 
							entityList = new EntityList<Ad>(adList.getAdlist(), adList.getAdlist().size()); 
						}
					}else{
						entityList = new EntityList<Ad>(new ArrayList<Ad>(), 0);
					}
					break;
                case ispMapping:
                    Isp_mappingListEntity isp_mappingListEntity = new Isp_mappingListEntity();
                    if(listDataFilter.getStatus() != null){
                        isp_mappingListEntity.setIsp_mappingEnum(Isp_mappingEnum.get_mappings_by_country);
                        StatusIdEnum statusEnum = listDataFilter.getStatus();
                        if(statusEnum == StatusIdEnum.Active){
                            isp_mappingListEntity.setIsp_mappingEnum(Isp_mappingEnum.get_mapped_isp_by_country);
                        }else  if(statusEnum == StatusIdEnum.Rejected){
                            isp_mappingListEntity.setIsp_mappingEnum(Isp_mappingEnum.get_rejected_isp_mapping_by_country);
                        }
                        
                    }
                    isp_mappingListEntity.setId_list(""+listDataFilter.getCountryId());
                        
                    Isp_mappingList isp_mappingList = ApiDef.various_get_isp_mapping(con, isp_mappingListEntity); 
                      
                    if(isp_mappingList.getMsg().getError_code()==0){
                        if( isp_mappingList.getIsp_mappinglist().size()>0){ 
                            entityList = new EntityList<Isp_mapping>(isp_mappingList.getIsp_mappinglist(), isp_mappingList.getIsp_mappinglist().size()); 
                        }
                    }else{
                        entityList = new EntityList<Isp_mapping>(new ArrayList<Isp_mapping>(), 0);
                    }
                    break;	
                case iddefinition:
                    IddefinitionInput idi = new IddefinitionInput();
                    idi.setIddefinitionEnum(IddefinitionEnum.getEnum(listDataFilter.getGet_type()));
                    idi.setIddefinitionType(IddefinitionType.getEnum(listDataFilter.getId_guid()));
                    idi.setIds(listDataFilter.getIds());
                    IddefinitionList iddefinitionList = null;
                    iddefinitionList = ApiDef.get_iddefinition_list(con, idi);
                    if(iddefinitionList.getMsg().getError_code()==0){ 
                        entityList = new EntityList<Iddefinition>(iddefinitionList.getIddefinition_list(), iddefinitionList.getIddefinition_list().size()); 
                    }else{
                        entityList = new EntityList<Iddefinition>(new ArrayList<Iddefinition>(), 0);
                    }
                    break;
                case retargeting_segment:
                    RetargetingSegmentInputEntity retargetingSegmentInputEntity = new RetargetingSegmentInputEntity();
                    retargetingSegmentInputEntity.setRetargetingSegmentEnum(RetargetingSegmentEnum.get_retargeting_segments_by_accounts);
                    retargetingSegmentInputEntity.setId_list(listDataFilter.getAccountGuid());
                    RetargetingSegmentList retargetingSegmentList = null;
                    retargetingSegmentList = ApiDef.various_get_retargeting_segments(con, retargetingSegmentInputEntity);
                    if(retargetingSegmentList.getMsg().getError_code()==0){ 
                        entityList = new EntityList<RetargetingSegment>(retargetingSegmentList.getRetargeting_segment_list(), 
                                retargetingSegmentList.getRetargeting_segment_list().size()); 
                    }else{
                        entityList = new EntityList<RetargetingSegment>(new ArrayList<RetargetingSegment>(), 0);
                    }
                    break;

				case pmp_deal:
					PMPListEntity pmpListEntity = new PMPListEntity();
					pmpListEntity.setPage_no(PageConstants.start_index);
					pmpListEntity.setPage_size(listDataFilter.getPageSize());
					PMPList pmpList = ApiDef.get_PMP_deals_list(con,pmpListEntity);

					if(pmpList.getMsg().getError_code()==0)
					{
						entityList = new EntityList<PrivateMarketPlaceApiEntity>(pmpList.getPMP_list(), pmpList.getPMP_list().size());
					}
					else
					{
						entityList = new EntityList<PrivateMarketPlaceApiEntity>(new ArrayList<PrivateMarketPlaceApiEntity>(), 0);
					}
					break;
                case adxbasedexchangesmetadata:
                    AdxBasedExchangesMetadataListEntity adxBasedEntity = new AdxBasedExchangesMetadataListEntity();
                    adxBasedEntity.setQueryEnum(AdxBasedExchangesMetadataQueryEnum.get_all);
                    AdxBasedExchangesMetadatList adxBasedEntityList = null;
                    adxBasedEntityList = ApiDef.various_get_adbasedexchanges_metadata(con, adxBasedEntity);
                    if(adxBasedEntityList.getMsg().getError_code()==0){ 
                        entityList = new EntityList<AdxBasedExchangesMetadata>(adxBasedEntityList.getEntity_list(), 
                        		adxBasedEntityList.getEntity_list().size()); 
                    }else{
                        entityList = new EntityList<AdxBasedExchangesMetadata>(new ArrayList<AdxBasedExchangesMetadata>(), 0);
                    }
                    break;

				default:
					break;
			}
		    return (EntityList<T>)entityList; 
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching ENITY list", e);
		}
		finally{
			try { 
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of sites in PublisherController",e);
			}
		}
		return null; 
	}
	 
}
