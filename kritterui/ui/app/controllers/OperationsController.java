package controllers;

import java.util.List;

import models.advertiser.AdDisplay;
import models.advertiser.IoDisplay;
import models.entities.ext_site.ExtSiteFormEntity;
import models.entities.isp_mapping.IspMappingFormEntity;
import models.publisher.Ext_siteDisplay;
import models.publisher.SiteDisplay;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.MetadataAPI;
import services.OperationsDataService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ext_site.Ext_site;
import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.api.entity.site.Site;


public class OperationsController extends Controller{

	 
	@SecuredAction
	public static Result adApprovalQueue(){
		return ok(views.html.operations.adApprovalQueueView.render(""));
	}
	

    @SecuredAction
    public static Result creativeApprovalQueue(){
        return ok(views.html.operations.creativeApprovalQueueView.render(""));
    }
	
	@SecuredAction
	public static Result siteApprovalQueue(){ 
		return ok(views.html.operations.siteApprovalQueueView.render(""));
	}
    @SecuredAction
    public static Result extsiteApprovalQueue(String status,Option<String> exchange, Option<String> osId){
        Form<ExtSiteFormEntity> extSiteFormEntityData = Form.form(ExtSiteFormEntity.class);
        ExtSiteFormEntity extSiteFormEntity  = new ExtSiteFormEntity();

        if(!exchange.isEmpty()){
            extSiteFormEntity.setExchange(exchange.get());
        }
        if(!osId.isEmpty()){
            extSiteFormEntity.setOsId(osId.get());
        }
        return ok(views.html.metaadmin.approveextsite.render(status,extSiteFormEntityData.fill(extSiteFormEntity)));
    }
    @SecuredAction
    public static Result extsiteRejectedQueue(String status,Option<String> exchange){ 
        Form<ExtSiteFormEntity> extSiteFormEntityData = Form.form(ExtSiteFormEntity.class);
        ExtSiteFormEntity extSiteFormEntity  = new ExtSiteFormEntity();

        if(!exchange.isEmpty()){
            extSiteFormEntity.setExchange(exchange.get());
        }
        return ok(views.html.metaadmin.approveextsite.render(status,extSiteFormEntityData.fill(extSiteFormEntity)));
    }
    @SecuredAction
    public static Result extsiteActiveQueue(String status,Option<String> exchange){ 
        Form<ExtSiteFormEntity> extSiteFormEntityData = Form.form(ExtSiteFormEntity.class);
        ExtSiteFormEntity extSiteFormEntity  = new ExtSiteFormEntity();

        if(!exchange.isEmpty()){
            extSiteFormEntity.setExchange(exchange.get());
        }
        return ok(views.html.metaadmin.approveextsite.render(status,extSiteFormEntityData.fill(extSiteFormEntity)));
    }
	
	@SecuredAction
	public static Result ioApprovalQueue(){
		return ok(views.html.operations.ioApprovalQueueView.render(""));
	}
	
	@SecuredAction
    public static Result rejectedIos(){
        return ok(views.html.operations.rejectedIOView.render(""));
    }
    
	@SecuredAction
    public static Result rejectedSites(){
        return ok(views.html.operations.rejectedSitesView.render(""));
    }
    
    @SecuredAction
    public static Result approvedSites(){
        return ok(views.html.operations.approvedactiveSitesView.render("Approved"));
    }
    @SecuredAction
    public static Result activeSites(){
        return ok(views.html.operations.approvedactiveSitesView.render("Active"));
    }
    @SecuredAction
    public static Result pausedSites(){
        return ok(views.html.operations.approvedactiveSitesView.render("Paused"));
    }

    @SecuredAction
    public static Result rejectedAds(){
        return ok(views.html.operations.rejectedAdsView.render(""));
    }
    @SecuredAction
    public static Result rejectedCreative(){
        return ok(views.html.operations.rejectedCreativeView.render("Rejected"));
    }
	
	@SecuredAction
	public static Result adApprovalQueueData(Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Ad> adList = OperationsDataService.adApprovalQueueData(status,pageNo,pageSize );
		ObjectNode result = Json.newObject();

		ArrayNode adnodes = result.putArray("list");
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode adnode = null;
		AdDisplay adDisplay = null;
		String destinationUrl = routes.OperationsController.adApprovalQueue().url();
		for (Ad ad : adList) { 
			adDisplay = new AdDisplay(ad);
			adDisplay.setDestination(destinationUrl);
			adnode= objectMapper.valueToTree(adDisplay);
			adnodes.addPOJO(adnode);
		}
		result.put("size", adList.size());  
		return ok(result);  
	}
	
	@SecuredAction
	public static Result siteApprovalQueueData(Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Site> siteList = OperationsDataService.siteApprovalQueueData(status, pageNo,pageSize );
		ObjectNode result = Json.newObject();

		ArrayNode cnodes = result.putArray("list");
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode snode = null;
		
		SiteDisplay siteDisplay = null;
		String destinationUrl = routes.OperationsController.siteApprovalQueue().url();
		
		for (Site site : siteList) { 
			siteDisplay = new SiteDisplay(site);
			siteDisplay.setDestination(destinationUrl);
			snode= objectMapper.valueToTree(siteDisplay); 
			cnodes.addPOJO(snode);
		}
		result.put("size", siteList.size());
		result.put("statusMap", MetadataAPI.campaignStatuses());

		return ok(result); 
	}
	
	@SecuredAction
	public static Result ioApprovalQueueData(Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Insertion_Order> ioList = OperationsDataService.ioApprovalQueueData(status, pageNo,pageSize );
		ObjectNode result = Json.newObject();

		ArrayNode cnodes = result.putArray("list");
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode snode = null;
		for (Insertion_Order io : ioList) { 
			snode= objectMapper.valueToTree(new IoDisplay(io)); 
			cnodes.addPOJO(snode);
		}
		result.put("size", ioList.size());
		result.put("statusMap", MetadataAPI.campaignStatuses());

		return ok(result); 
	}
    @SecuredAction
    public static Result extsiteApprovalQueueData(Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
        List<Ext_site> extsiteList = OperationsDataService.extsiteApprovalQueueData(status, pageNo,pageSize );
        ObjectNode result = Json.newObject();

        ArrayNode cnodes = result.putArray("list");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode snode = null;
        
        Ext_siteDisplay extsiteDisplay = null;
        for (Ext_site ext_site : extsiteList) { 
            extsiteDisplay = new Ext_siteDisplay(ext_site);
            snode= objectMapper.valueToTree(extsiteDisplay); 
            cnodes.addPOJO(snode);
        }
        result.put("size", extsiteList.size());
        return ok(result); 
    }
    

	 
}
