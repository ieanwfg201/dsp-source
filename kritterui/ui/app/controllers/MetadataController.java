package controllers;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.MetadataAPI;
import services.TPMetadataAPI;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;


public class MetadataController extends Controller{

	private static String brandbyos = Play.application().configuration().getString("brandbyos");
		 
		@SecuredAction
		public static Result carriers(String countryList){
			ArrayNode carrierOptions = new ArrayNode(JsonNodeFactory.instance); 
			carrierOptions = TPMetadataAPI.carrierList(countryList); 
			return ok(carrierOptions);
		}
		@SecuredAction
		public static Result state(String countryList){
			ArrayNode stateOptions = new ArrayNode(JsonNodeFactory.instance); 
			stateOptions = TPMetadataAPI.stateList(countryList); 
			return ok(stateOptions);
		}
		@SecuredAction
		public static Result city(String stateList){
			ArrayNode cityOptions = new ArrayNode(JsonNodeFactory.instance); 
			cityOptions = TPMetadataAPI.cityList(stateList); 
			return ok(cityOptions);
		}

		@SecuredAction
		public static Result brands(String osList){
			if("true".equals(brandbyos)){
				ArrayNode brandOptions = TPMetadataAPI.brandList(osList); 
				return ok(brandOptions);
			}else{
				ArrayNode brandOptions = TPMetadataAPI.brandList(); 
				return ok(brandOptions);
			}
		}
		
		@SecuredAction
		public static Result devices(String brands){
			ArrayNode deviceOptions = TPMetadataAPI.modelList(brands); 
			return ok(deviceOptions);
		}
		
		@SecuredAction
		public static Result browsers(){
			ArrayNode browserOptions = TPMetadataAPI.browserList(); 
			return ok(browserOptions);
		}
        @SecuredAction
        public static Result nofillReason(){
            ArrayNode nofillReasonOptions = TPMetadataAPI.nofillReasonList(); 
            return ok(nofillReasonOptions);
        }
        
        @SecuredAction
        public static Result postImpEvent(){
            ArrayNode nofillReasonOptions = TPMetadataAPI.postImpEventList(); 
            return ok(nofillReasonOptions);
        }
		
		@SecuredAction
		public static Result tier_1_categories(){
			ArrayNode tier_1_categoryOptions = MetadataAPI.tier_1_categories(); 
			return ok(tier_1_categoryOptions);
		}
		
        @SecuredAction
        public static Result tier_2_categories(){
            ArrayNode tier_2_categoryOptions = MetadataAPI.tier_2_categories(); 
            return ok(tier_2_categoryOptions);
        }
		
        @SecuredAction
        public static Result device_type(){
            ArrayNode device_typeOptions = MetadataAPI.device_type(); 
            return ok(device_typeOptions);
        }

        @SecuredAction
		public static Result countries(){
			ArrayNode countryOptions = TPMetadataAPI.countryList(); 
			return ok(countryOptions);
		}
		
		@SecuredAction
		public static Result advertisers(){
			ArrayNode advertiserOptions = MetadataAPI.activeAdvertiserArray(); 
			return ok(advertiserOptions);
		}
		
        @SecuredAction
        public static Result advids(){
            ArrayNode advidOptions = MetadataAPI.activeAdvIdsArray(); 
            return ok(advidOptions);
        }
        
		@SecuredAction
        public static Result advertiserforfiltering(){
            ArrayNode advertiserOptions = MetadataAPI.advertiserforfiltering(); 
            return ok(advertiserOptions);
        }

		@SecuredAction
        public static Result directadvertiserforfiltering(){
            ArrayNode advertiserOptions = MetadataAPI.directadvertiserforfiltering(); 
            return ok(advertiserOptions);
        }
		
		@SecuredAction
		public static Result publishers(){
			ArrayNode publisherOptions = MetadataAPI.activePublisherArray(); 
			return ok(publisherOptions);
		}
        @SecuredAction
        public static Result directpublishers(){
            ArrayNode publisherOptions = MetadataAPI.activeDirectPublisherArray(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result exchanges(){
            ArrayNode publisherOptions = MetadataAPI.activeExchangeArray(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result guidExchanges(){
            ArrayNode publisherOptions = MetadataAPI.activeGuidExchangeArray(); 
            return ok(publisherOptions);
        }

        @SecuredAction
        public static Result supply_source_type(){
            ArrayNode supply_source_typeOptions = MetadataAPI.supply_source_typeArray(); 
            return ok(supply_source_typeOptions);
        }

        @SecuredAction
        public static Result connection_type(){
            ArrayNode supply_source_typeOptions = MetadataAPI.reporting_connection_type_Array(); 
            return ok(supply_source_typeOptions);
        }

		@SecuredAction
		public static Result operatingSystems(){
			ArrayNode operatorOptions = TPMetadataAPI.osList(); 
			return ok(operatorOptions);
		}
		
		public static Result sitesByPublishers(String pubList){
			ArrayNode siteOptions = MetadataAPI.sitesByPublishers(pubList); 
			return ok(siteOptions);
		}
		public static Result targeting_sitesByPublishers(String pubList){
            ArrayNode siteOptions = MetadataAPI.targeting_sitesByPublishers(pubList); 
            return ok(siteOptions);
        }
        public static Result targeting_directsitesByPublishers(String pubList){
            ArrayNode siteOptions = MetadataAPI.targeting_directsitesByPublishers(pubList); 
            return ok(siteOptions);
        }
        public static Result ext_siteByPublishers(String pubList){
            ArrayNode siteOptions = MetadataAPI.ext_siteByPublishers(pubList); 
            return ok(siteOptions);
        }
        
        public static Result targeting_ext_siteByPublishers(String pubList){
            ArrayNode siteOptions = MetadataAPI.targeting_ext_siteByPublishers(pubList); 
            return ok(siteOptions);
        }
        @SecuredAction
        public static Result tier1mmacategory(){
            ArrayNode publisherOptions = MetadataAPI.tier1mmacategory(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result tier2MMAcategoryBytier1(String tier1List){
            ArrayNode publisherOptions = MetadataAPI.tier2mmacategory(tier1List); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result tier1mmaindustry(){
            ArrayNode publisherOptions = MetadataAPI.tier1mmaindustry(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result tier2MMAindustryBytier1(String tier1List){
            ArrayNode publisherOptions = MetadataAPI.tier2mmaindustry(tier1List); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result tier1channel(){
            ArrayNode publisherOptions = MetadataAPI.tier1channel(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result tier2channelBytier1(String tier1List){
            ArrayNode publisherOptions = MetadataAPI.tier2channelBytier1(tier1List); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result adposition_list(){
            ArrayNode publisherOptions = MetadataAPI.adposition_list(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result videomimes(){
            ArrayNode publisherOptions = MetadataAPI.videomimes(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result videoprotocols(){
            ArrayNode publisherOptions = MetadataAPI.videoprotocols(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result videoplaybackmethod(){
            ArrayNode publisherOptions = MetadataAPI.videoplaybackmethod(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result videodelivery(){
            ArrayNode publisherOptions = MetadataAPI.videodelivery(); 
            return ok(publisherOptions);
        }
        @SecuredAction
        public static Result videoapi(){
            ArrayNode publisherOptions = MetadataAPI.videoapi(); 
            return ok(publisherOptions);
        }

		public static Result campaignforfilteringByAdvertiser(String advtList){
			String ids[] = advtList.split(",");
			String tmp = "";
			for (String id : ids) {
				tmp +=  id +",";
			}
			tmp = tmp.substring(0, tmp.length()-1);
			ArrayNode campaignOptions = MetadataAPI.campaignsByAccountIds(tmp); 
			return ok(campaignOptions);
		}
        public static Result retargeting_segment_by_adv(String adv){
            ArrayNode segmentOptions = MetadataAPI.retargetingSegmentsByAccount(adv); 
            return ok(segmentOptions);
        }
        public static Result campaignsByAdvertiser(String advtList){
            String guids[] = advtList.split(",");
            String tmp = "";
            for (String guid : guids) {
                tmp +=  "'"+ guid +"',";
            }
            tmp = tmp.substring(0, tmp.length()-1);
            ArrayNode campaignOptions = MetadataAPI.campaignsByAccount(tmp); 
            return ok(campaignOptions);
        }
        public static Result campaignsByAdvid(String advtList){
            String ids[] = advtList.split(",");
            String tmp = "";
            for (String id : ids) {
                tmp +=  id +",";
            }
            tmp = tmp.substring(0, tmp.length()-1);
            ArrayNode campaignOptions = MetadataAPI.campaignsByAdvIds(tmp); 
            return ok(campaignOptions);
        }

		public static Result adsByCampaign(String campaignList){
			ArrayNode adOptions = MetadataAPI.adsByCampaign(campaignList); 
			return ok(adOptions);
		}
		
		public static Result hourList(){
			ArrayNode adOptions = MetadataAPI.hourListOptions(); 
			return ok(adOptions);
		}
		@SecuredAction
        public static Result advertiserJsonArrayOptions(){
            ArrayNode advertiserJsonArrayOptions = MetadataAPI.get_active_advertiser_list_as_metadata();
            return ok(advertiserJsonArrayOptions);
        }
		
		@SecuredAction
        public static Result hygienelist(){
			 ArrayNode hygieneList = MetadataAPI.hygieneList();
			 return ok(hygieneList);
        }
		
		@SecuredAction
        public static Result creativeAttributes(){
			 ArrayNode creativeAttributes = MetadataAPI.creativeAttributes(-1);
	         return ok(creativeAttributes); 
        }
		
		@SecuredAction
        public static Result slotOptions(){
			 ArrayNode creativeSlots = MetadataAPI.creativeSlotsArray();
	         return ok(creativeSlots); 
        }
        @SecuredAction
        public static Result iconSlotOptions(){
             ArrayNode creativeSlots = MetadataAPI.iconSlotsArray();
             return ok(creativeSlots); 
        }
        @SecuredAction
        public static Result screenshotSlotOptions(){
            ArrayNode creativeSlots = MetadataAPI.screenshotSlotsArray();
            return ok(creativeSlots); 
       }

       @SecuredAction
       public static Result third_party_demand_channel_dspid_list(String advertiserGuid){
           ArrayNode dspidlist = MetadataAPI.thirdPartyConnectionDSPIdArray(advertiserGuid);
           return ok(dspidlist);
       }

       @SecuredAction
       public static Result third_party_demand_channel_advid_list(String advertiserGuid){
           ArrayNode advidlist = MetadataAPI.thirdPartyConnectionAdvIdArray(advertiserGuid);
           return ok(advidlist);
       }
}
