package controllers;

import java.util.List;

import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;
import models.EntityList;
import models.EntityListFilter;
import models.accounts.displays.AdvertiserDisplay;
import models.accounts.displays.PublisherDisplay;
import models.advertiser.AdDisplay;
import models.advertiser.CampaignDisplay;
import models.advertiser.CreativeDisplay;
import models.advertiser.RetargetingSegmentDisplay;
import models.advertiser.TargetingDisplay;
import models.entities.adpositionget.AdpositionGetDisplay;
import models.entities.adxbasedexchanges.AdxBasedExchangesMetadataDisplay;
import models.entities.audience.AudienceMetadataDisplay;
import models.entities.isp_mapping.Isp_mappingDisplay;
import models.entities.materialadvinfoupload.MaterialAdvInfoUploadDisplay;
import models.entities.materialbannerupload.MaterialBannerUploadDisplay;
import models.entities.materialvideoupload.MaterialVideoUploadDisplay;
import models.entities.qualification.QualificationDisplay;
import models.iddefinition.IddefinitionDisplay;
import models.pmp.display.PMPDisplay;
import models.publisher.Ext_siteDisplay;
import models.publisher.SiteDisplay;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.EntityListDataService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.ext_site.Ext_site;
import com.kritter.api.entity.iddefinition.Iddefinition;
import com.kritter.api.entity.isp_mapping.Isp_mapping;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.Account_Type;
import com.kritter.entity.account.Qualification;
import com.kritter.entity.adxbasedexchanges_metadata.AdPositionGet;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadAdvInfo;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadBanner;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadVideo;
import com.kritter.entity.audience_metadata.AudienceMetadata;
import com.kritter.entity.retargeting_segment.RetargetingSegment;

public class EntityListController extends Controller{
	
	@SuppressWarnings("unchecked")
	public static Result entityList(){
		JsonNode queryParams = request().body().asJson();
		ObjectNode result = Json.newObject();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			EntityListFilter entityListFilter = objectMapper.treeToValue(queryParams, EntityListFilter.class);
			if(entityListFilter.getEntityType()!=null){
				EntityList<?> entityList =  null;
				ArrayNode itemList = result.putArray("list");
				switch (entityListFilter.getEntityType()) {
				case account:
					entityList = EntityListDataService.listData(entityListFilter, new Account()); 
					populateAccounts(itemList, (List<Account>)entityList.getEntityList()); 
					break;
					
				case campaign:
					entityList = EntityListDataService.listData(entityListFilter, new Campaign()); 
					populateCampaigns(itemList,(List<Campaign>) entityList.getEntityList()); 
					break;
					
				case ad:
					entityList = EntityListDataService.listData(entityListFilter, new Ad()); 
					populateAds(itemList,(List<Ad>) entityList.getEntityList()); 
					break;
					
				case creative:
					entityList = EntityListDataService.listData(entityListFilter, new Creative_container()); 
					populateCreatives(itemList,(List<Creative_container>) entityList.getEntityList()); 
					break;
					
				case targeting_profile:
					entityList = EntityListDataService.listData(entityListFilter, new Targeting_profile()); 
					populateTargetingList(itemList,(List<Targeting_profile>) entityList.getEntityList()); 
					break;
				case site:
					entityList = EntityListDataService.listData(entityListFilter, new Site()); 
					populateSites(itemList,(List<Site>) entityList.getEntityList()); 
					break;
                case extsite:
                    entityList = EntityListDataService.listData(entityListFilter, new Ext_site()); 
                    populateExtSites(itemList,(List<Ext_site>) entityList.getEntityList()); 
                    break;
                case ispMapping:
                    entityList = EntityListDataService.listData(entityListFilter, new Isp_mapping()); 
                    populateIsp_mapping(itemList,(List<Isp_mapping>) entityList.getEntityList()); 
                    break;
                case iddefinition:
                    entityList = EntityListDataService.listData(entityListFilter, new Iddefinition()); 
                    populateIddefinition(itemList,(List<Iddefinition>) entityList.getEntityList()); 
                    break;
                case retargeting_segment:
                    entityList = EntityListDataService.listData(entityListFilter, new RetargetingSegment()); 
                    populateRetargetingSegment(itemList,(List<RetargetingSegment>) entityList.getEntityList()); 
                    break;
				case pmp_deal:
					entityList = EntityListDataService.listData(entityListFilter, new PrivateMarketPlaceApiEntity());
					populatePMPDeals(itemList,(List<PrivateMarketPlaceApiEntity>) entityList.getEntityList());
					break;
				case adxbasedexchangesmetadata:
					entityList = EntityListDataService.listData(entityListFilter, new AdxBasedExchangesMetadata());
					populateAdxBasedExchangesMetadata(itemList,(List<AdxBasedExchangesMetadata>) entityList.getEntityList());
					break;
				case adpositionget:
					entityList = EntityListDataService.listData(entityListFilter, new AdPositionGet());
					populateAdPositionGet(itemList,(List<AdPositionGet>) entityList.getEntityList());
					break;
				case materialbannerupload:
					entityList = EntityListDataService.listData(entityListFilter, new MaterialUploadBanner());
					populateMaterialBannerUpload(itemList,(List<MaterialUploadBanner>) entityList.getEntityList());
					break;
				case materialvideoupload:
					entityList = EntityListDataService.listData(entityListFilter, new MaterialUploadVideo());
					populateMaterialVideoUpload(itemList,(List<MaterialUploadVideo>) entityList.getEntityList());
					break;
				case materialadvinfoupload:
					entityList = EntityListDataService.listData(entityListFilter, new MaterialUploadAdvInfo());
					populateMaterialAdvInfoUpload(itemList,(List<MaterialUploadAdvInfo>) entityList.getEntityList());
					break;
				case qualification:
					entityList = EntityListDataService.listData(entityListFilter, new Qualification());
					populateQualification(itemList,(List<Qualification>) entityList.getEntityList());
					break;
				case audiencemetadata:
					entityList = EntityListDataService.listData(entityListFilter, new AudienceMetadata());
					populateAudienceMetadata(itemList,(List<AudienceMetadata>) entityList.getEntityList());
					break;
				default:
					break;
				}
				result.put("size", entityList.getCount()); 
				return ok(result);  
			}
			
		} catch (Exception e) {
			Logger.debug("Error while fetchinng account list", e);
		} 
		return badRequest();
	}
	
	private static void populateAccounts(ArrayNode entityArray, List<Account> accountList){
		for (Account account : accountList) {
			if(account.getType_id() == Account_Type.directadvertiser){
				entityArray.addPOJO(objectMapper.valueToTree(new AdvertiserDisplay(account)));
			}else{
				entityArray.addPOJO(objectMapper.valueToTree(new PublisherDisplay(account)));
			} 
		}	
	}
	
	static ObjectMapper objectMapper = new ObjectMapper();
	
	private static void populateCampaigns(ArrayNode entityArray, List<Campaign> campaignList){
		for (Campaign campaign : campaignList) { 
			entityArray.addPOJO(objectMapper.valueToTree(new CampaignDisplay(campaign)));
		}
	}
	
	private static void populateAds(ArrayNode entityArray, List<Ad> adList){
		for (Ad ad : adList) { 
			entityArray.addPOJO(objectMapper.valueToTree(new AdDisplay(ad)));
		}
	}
	
	private static void populateCreatives(ArrayNode entityArray, List<Creative_container> creativeList){
		for (Creative_container creative : creativeList) { 
			entityArray.addPOJO(objectMapper.valueToTree(new CreativeDisplay(creative)));
		}
	}
	
	private static void populateTargetingList(ArrayNode entityArray, List<Targeting_profile> targetingProfileList){
		for (Targeting_profile targeting_profile : targetingProfileList) { 
			entityArray.addPOJO(objectMapper.valueToTree(new TargetingDisplay(targeting_profile)));
		} 
	}
	
	private static void populateSites(ArrayNode entityArray, List<Site> siteList){
		for (Site site : siteList) { 
			entityArray.addPOJO(objectMapper.valueToTree(new SiteDisplay(site)));
		}
	}
    private static void populateExtSites(ArrayNode entityArray, List<Ext_site> extsiteList){
        for (Ext_site ext_site : extsiteList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new Ext_siteDisplay(ext_site)));
        }
    }
    private static void populateIsp_mapping(ArrayNode entityArray, List<Isp_mapping> isp_mappingList){
        for (Isp_mapping isp_mapping : isp_mappingList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new Isp_mappingDisplay(isp_mapping)));
        }
    }
    private static void populateIddefinition(ArrayNode entityArray, List<Iddefinition> iiddefinitionList){
        for (Iddefinition iddefinition : iiddefinitionList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new IddefinitionDisplay(iddefinition)));
        }
    }
    private static void populateRetargetingSegment(ArrayNode entityArray, List<RetargetingSegment> retargetingSegmentList){
        for (RetargetingSegment retargetingSegment : retargetingSegmentList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new RetargetingSegmentDisplay(retargetingSegment)));
        }
    }

	private static void populatePMPDeals(ArrayNode entityArray, List<PrivateMarketPlaceApiEntity> pmpDealList){
		for (PrivateMarketPlaceApiEntity entity : pmpDealList) {
			entityArray.addPOJO(objectMapper.valueToTree(new PMPDisplay(entity)));
		}
	}
    private static void populateAdxBasedExchangesMetadata(ArrayNode entityArray, List<AdxBasedExchangesMetadata> adxBasedList){
        for (AdxBasedExchangesMetadata adxBased : adxBasedList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new AdxBasedExchangesMetadataDisplay(adxBased)));
        }
    }
    private static void populateAdPositionGet(ArrayNode entityArray, List<AdPositionGet> entityList){
        for (AdPositionGet entity : entityList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new AdpositionGetDisplay(entity)));
        }
    }
    private static void populateMaterialBannerUpload(ArrayNode entityArray, List<MaterialUploadBanner> entityList){
        for (MaterialUploadBanner entity : entityList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new MaterialBannerUploadDisplay(entity)));
        }
    }
    private static void populateMaterialVideoUpload(ArrayNode entityArray, List<MaterialUploadVideo> entityList){
        for (MaterialUploadVideo entity : entityList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new MaterialVideoUploadDisplay(entity)));
        }
    }
    private static void populateMaterialAdvInfoUpload(ArrayNode entityArray, List<MaterialUploadAdvInfo> entityList){
        for (MaterialUploadAdvInfo entity : entityList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new MaterialAdvInfoUploadDisplay(entity)));
        }
    }
    private static void populateQualification(ArrayNode entityArray, List<Qualification> entityList){
        for (Qualification entity : entityList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new QualificationDisplay(entity)));
        }
    }
    private static void populateAudienceMetadata(ArrayNode entityArray, List<AudienceMetadata> entityList){
        for (AudienceMetadata entity : entityList) { 
            entityArray.addPOJO(objectMapper.valueToTree(new AudienceMetadataDisplay(entity)));
        }
    }
}
