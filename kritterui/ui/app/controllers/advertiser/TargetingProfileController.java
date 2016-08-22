package controllers.advertiser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import models.advertiser.TargetingDisplay;
import models.advertiser.TargetingDisplayFull;
import models.advertiser.TargetingListDisplay;
import models.entities.TargetingProfileEntity;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.Play;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import services.MetadataAPI;
import services.TPMetadataAPI;
import views.html.advt.targeting.targetingform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.targeting_profile.FileUploadResponse;
import com.kritter.api.entity.targeting_profile.TargetingProfileList;
import com.kritter.api.entity.targeting_profile.TargetingProfileListEntity;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.AdAPIEnum;
import com.kritter.constants.Geo_Targeting_type;
import com.kritter.constants.TargetingProfileAPIEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;



public class TargetingProfileController extends Controller{

	private static Form<TargetingProfileEntity> tpFormTemplate =  Form.form(TargetingProfileEntity.class);
	private static String rhs = Play.application().configuration().getString("show_tp_rhs");
	private static String show_midp_ui = Play.application().configuration().getString("show_midp_ui");
	private static String allow_wifi = Play.application().configuration().getString("allow_wifi");
	private static String retargeting_flow_enabled = Play.application().configuration().getString("retargeting_flow_enabled");
	private static String state_city = Play.application().configuration().getString("state_city");
	private static String mma_required = Play.application().configuration().getString("mma_required");
	private static String adposition_required = Play.application().configuration().getString("adposition_required");
	private static String channel_required = Play.application().configuration().getString("channel_required");
	private static String lat_lon_file = Play.application().configuration().getString("lat_lon_file");
	private static String deviceid_targeting = Play.application().configuration().getString("deviceid_targeting");
	private static String file_prefix_path = Play.application().configuration().getString("file_prefix_path");
	
	private static Targeting_profile getTargetingProfile(String guid, String accountGuid){
		Connection con = null;
		Targeting_profile tp = null; 
		try{
		    con = DB.getConnection();
			TargetingProfileListEntity tple = new TargetingProfileListEntity();
			tple.setTpEnum(TargetingProfileAPIEnum.get_targeting_profile);
			tple.setGuid(guid); 
			tple.setAccount_guid(accountGuid);
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

	@SecuredAction
	public static Result add(String  accountGuid, Option<String> destination){ 
		Targeting_profile tp = new Targeting_profile();
		tp.setAccount_guid(accountGuid); 
		TargetingProfileEntity tpe = new TargetingProfileEntity();
		BeanUtils.copyProperties(tp, tpe);
		if(destination.nonEmpty())
			tpe.setDestination(destination.get());
		return ok(targetingform.render( tpFormTemplate.fill(tpe) , new TargetingDisplay(tp), rhs,show_midp_ui, accountGuid, allow_wifi,retargeting_flow_enabled, state_city,mma_required,adposition_required,channel_required,lat_lon_file,deviceid_targeting));
	}

	@SecuredAction
	public static Result edit(String tpGuid, String accountGuid){ 
		Targeting_profile tp =   getTargetingProfile(tpGuid, accountGuid);  
		TargetingProfileEntity tpe = new TargetingProfileEntity();
		BeanUtils.copyProperties(tp, tpe);
		if(tp!= null)
			return ok(targetingform.render(tpFormTemplate.fill(tpe), new TargetingDisplay(tp),rhs,show_midp_ui, tpe.getAccount_guid(), allow_wifi,retargeting_flow_enabled, state_city,mma_required,adposition_required,channel_required,lat_lon_file,deviceid_targeting));
		else
			return badRequest();
	}

	 

	@SecuredAction
	public static Result view(String tpGuid, String accountGuid){ 
		Targeting_profile tp =   getTargetingProfile(tpGuid, accountGuid); 
		TargetingProfileEntity tpe = new TargetingProfileEntity();
		BeanUtils.copyProperties(tp, tpe);
		if(tp!= null)
			return ok(views.html.advt.targeting.targetingHome.render(new TargetingDisplayFull(tp),retargeting_flow_enabled,mma_required,adposition_required,channel_required,lat_lon_file,deviceid_targeting));
		else
			return badRequest();
	}

	@SecuredAction
	public static Result save(){ 
		Form<TargetingProfileEntity> tpForm  = tpFormTemplate.bindFromRequest();
		Targeting_profile tp = null;
		if(!tpForm.hasErrors()){
			
			Connection con = null;
			try {
				
				con = DB.getConnection();
				TargetingProfileEntity tpe = tpForm.get(); 
				tp = tpe.getEntity();
				tp.setModified_by(1);
				Geo_Targeting_type geo_targeting_type = tp.getGeo_targeting_type();
				switch(geo_targeting_type){
				case COUNTRY_CARRIER:
				    tp.setZipcode_file_id_set("");
				    tp.setCustom_ip_file_id_set("");
				    break;
                case IP:
                    tp.setZipcode_file_id_set("");
                    tp.setCarrier_json("[]");
                    tp.setCountry_json("[]");
                    tp.setState_json("[]");
                    tp.setCity_json("[]");
                    break;
                case ZIPCODE:
                    tp.setCustom_ip_file_id_set("");
                    tp.setCarrier_json("[]");
                    tp.setCountry_json("[]");
                    tp.setState_json("[]");
                    tp.setCity_json("[]");
                    break;
				default:
				    break;
				}
				
				Message msg = null;
				if(tpe.getGuid() != ""){
					tp.setFile_prefix_path(file_prefix_path);
					msg = ApiDef.update_targeting_profile(con, tp);
					String approve_ad_again_on_tp_update = Play.application().configuration().getString("approve_ad_again_on_tp_update");
					if(approve_ad_again_on_tp_update != null && "true".equals(approve_ad_again_on_tp_update)){
					    AdListEntity adListEntity = new AdListEntity();
					    adListEntity.setAdenum(AdAPIEnum.approve_ad_again_on_tp_update);
					    adListEntity.setId_list(tp.getGuid());
					    ApiDef.change_status_ad(con, adListEntity);
					}
				}else{
					tp.setFile_prefix_path(file_prefix_path);
					msg = ApiDef.insert_targeting_profile(con, tp);
				}
				 
				if(msg.getError_code()==0){
					String destination = tpForm.field("destination").value();
					if(!"".equals(destination)){
						destination = destination+"&tpId="+ tp.getGuid();
						return redirect(destination);
					}else
						return redirect(routes.TargetingProfileController.list(tp.getAccount_guid()));
				}
					
				else
					return badRequest(targetingform.render(tpForm, new TargetingDisplay(tp),rhs, show_midp_ui, tp.getAccount_guid(), allow_wifi,retargeting_flow_enabled, state_city,mma_required,adposition_required,channel_required,lat_lon_file,deviceid_targeting));
			} catch (Exception e) {
				Logger.error("Error while saving Targeting profile TargetingProfileController",e);
			}
			finally{
				try {
                    if(con != null){
				        con.close();
                    }
				} catch (SQLException e) {
					Logger.error("Error closing DB connection while saving Targeting profile TargetingProfileController",e);
				}
			} 
			
		}
		String guid = tpForm.field("guid").value();
		String accountGuid = tpForm.field("account_guid").value();
		tp =   getTargetingProfile(guid, accountGuid); 
		if(tp ==null){
		    tp = new Targeting_profile();
		    tp.setAccount_guid(accountGuid);
		}
		return badRequest(targetingform.render(tpForm, new TargetingDisplay(tp),rhs,show_midp_ui, tp.getAccount_guid(), allow_wifi,retargeting_flow_enabled, state_city,mma_required,adposition_required,channel_required,lat_lon_file,deviceid_targeting));
	}


	@SecuredAction
	public static Result defaultOptions(Option<String> osList,  Option<String> brandList){
		ObjectNode defaultOptions = new ObjectNode(JsonNodeFactory.instance);
		defaultOptions.put("osOptions",TPMetadataAPI.osList() );		
		defaultOptions.put("browserOptions", TPMetadataAPI.browserList());
		defaultOptions.put("tier1categoryOptions", MetadataAPI.tier_1_categories());
		defaultOptions.put("tier2categoryOptions", MetadataAPI.tier_1_categories());
		defaultOptions.put("siteOptions", TPMetadataAPI.siteList());
		if(osList.nonEmpty())
			defaultOptions.put("brandOptions", TPMetadataAPI.brandList(osList.get())); 
		if(brandList.nonEmpty())
			defaultOptions.put("modelOptions", TPMetadataAPI.modelList(brandList.get()));
		return ok(defaultOptions);
	}
	
	

	public static Result list(String accountGuid){  
		Account account = DataAPI.getAccountByGuid(accountGuid);
		TargetingListDisplay targetingListDisplay = new TargetingListDisplay(account);
		return ok(views.html.advt.targeting.tpListTemplate.render(targetingListDisplay)); 			 
	}

	@SecuredAction
	public static Result tpList(String accountGuid, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Targeting_profile> tpList = null; 
		tpList = DataAPI.getTPs(accountGuid, pageNo, pageSize);
		ObjectNode result = Json.newObject();
		ArrayNode tpnodes = result.putArray("list");
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode tpnode = null;
		for (Targeting_profile tp : tpList) { 
			tpnode= objectMapper.valueToTree(tp);
			tpnode.put("edit_url", routes.TargetingProfileController.edit(tp.getGuid(), accountGuid).url());
			tpnode.put("view_url", routes.TargetingProfileController.view(tp.getGuid(), accountGuid).url());
			tpnodes.addPOJO(tpnode);
		}
		result.put("size", tpList.size()); 
		return ok(result);
	}
    @SecuredAction
    public static Result uploadGeoTargetingData(){
        return uploadGeoTargetingData(false, null,null);
    }
	public static Result uploadGeoTargetingData(boolean isApicall, String targeting_type, String account_guid){ 
		MultipartFormData multipartFormData = request().body().asMultipartFormData(); 
 
				
		String accountGuid = null;
		String targetingType = null; 
		if(isApicall){
		    targetingType = targeting_type;
		    accountGuid=  account_guid;
		}else{
		      accountGuid = multipartFormData.asFormUrlEncoded().get("account_guid")[0];
		      targetingType = multipartFormData.asFormUrlEncoded().get("targeting_type")[0]; 
		}
		 
		
		FilePart geoFile = multipartFormData.getFile("file"); 
		ObjectNode  response = Json.newObject();	
		if (geoFile != null) { 
			String fileName = geoFile.getFilename();
			String extension = FilenameUtils.getExtension(fileName);
			if(!("txt".equals(extension)|| "csv".equals(extension))){
			    if(isApicall){
			        FileUploadResponse fur = new FileUploadResponse();
                    fur.setErrorCode(ErrorEnum.FILE_EXTENSION_NOT_ALLOWED.getId());
                    fur.setMessage(ErrorEnum.FILE_EXTENSION_NOT_ALLOWED.getName());
                    return ok(fur.toJson().toString());
			    }
			    response.put("message", "Incorrect File " +fileName+ " of Type "+extension+" being uploaded. Allowed format is txt or csv");
			    return badRequest(response);
			}
			boolean validateIP = false;
			if("custom_ip_file_id_set".equals(targetingType)){
			    validateIP = true;
			}
			File file = geoFile.getFile();
			String destFilePath = generateFileUrl(accountGuid,  targetingType, fileName);
			File outputFile = new File("public/"+destFilePath).getAbsoluteFile();
			outputFile.getParentFile().mkdirs();
			BufferedReader br=null;
			BufferedWriter bw = null;
			FileReader fr = null;
			FileWriter fw = null;
			try {
			    fr = new FileReader(file);
			    br = new BufferedReader(fr);
				fw =  new FileWriter(outputFile);
				bw = new BufferedWriter(fw);
				String line = "";
				while((line = br.readLine())!=null){
				    if(validateIP){
				        String msg=ValidateIpFile.validate(line);
				        if(msg != null){
			                if(isApicall){
			                    FileUploadResponse fur = new FileUploadResponse();
			                    fur.setErrorCode(ErrorEnum.FILE_IP_FORMAT_INCORRECT.getId());
			                    fur.setMessage(msg);
			                    return ok(fur.toJson().toString());
			                }
				            response.put("message", msg);
			                return badRequest(response);
				        }
				    }
					bw.write(line);
					bw.write("\n");
				}
				if(isApicall){
				    FileUploadResponse fur = new FileUploadResponse();
				    fur.setErrorCode(ErrorEnum.NO_ERROR.getId());
				    fur.setMessage(ErrorEnum.NO_ERROR.getName());
				    fur.setPath(destFilePath);
				    fur.setPreview_label(fileName);
				    fur.setPreview_url(controllers.routes.StaticFileController.download(Scala.Option(destFilePath)).url());
				    return ok(fur.toJson().toString());
				}
				response.put("message", targetingType+" File Uploaded Succesfully");
				response.put("path", ""+destFilePath);
				response.put("preview_label", fileName);
				response.put("preview_url", controllers.routes.StaticFileController.download(Scala.Option(destFilePath)).url()); 
				return ok(response);

			} catch (IOException e) {
			    Logger.error("Error in reading uploaded file property. "+ e.getMessage(),e);
			    if(isApicall){
			        FileUploadResponse fur = new FileUploadResponse();
                    fur.setErrorCode(ErrorEnum.FILE_READ_ERROR.getId());
                    fur.setMessage(ErrorEnum.FILE_READ_ERROR.getName());
                    return ok(fur.toJson().toString());
                }
			}finally{
                    try {
                        if(bw != null){
                            bw.close();
                        }
                        if(fw != null){
                            fw.close();
                        }
                        if(br != null){
                            br.close();
                        }
                        if(fr != null){
                            fr.close();
                        }
                    } catch (IOException e) {
                        Logger.error(e.getMessage(),e);
                    }
			}
		} else {
            if(isApicall){
                FileUploadResponse fur = new FileUploadResponse();
                fur.setErrorCode(ErrorEnum.FILE_UPLOAD_FAILED.getId());
                fur.setMessage(ErrorEnum.FILE_UPLOAD_FAILED.getName());
                return ok(fur.toJson().toString());
            }else{
                flash("error", "File Uploading failed");
            }
		}
        if(isApicall){
            FileUploadResponse fur = new FileUploadResponse();
            fur.setErrorCode(ErrorEnum.FILE_UPLOAD_FAILED.getId());
            fur.setMessage(ErrorEnum.FILE_UPLOAD_FAILED.getName());
            return ok(fur.toJson().toString());
        }else{
            return ok("File uploaded successfully");
        }
	}
	
	private static String generateFileUrl(String accountGuid, String targetingType, String filename){ 
		String ext = "";
		if(filename.indexOf('.')!= -1){
			String splits[] = filename.split("\\.");
			if(splits.length == 2)
				ext = splits[1];
		}
		String uuidname = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
		String basePath = "/targeting/geo/"+targetingType+"/"+accountGuid+"/"+uuidname+"."+ext;	  
		return basePath; 
	}
}
