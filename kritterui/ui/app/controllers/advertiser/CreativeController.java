package controllers.advertiser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import models.Constants.Actions;
import models.advertiser.BannerDisplay;
import models.advertiser.CreativeDisplay;
import models.advertiser.CreativeDisplayFull;
import models.advertiser.CreativeListDisplay;
import models.advertiser.DirectvideoDisplay;
import models.advertiser.NativeIconDisplay;
import models.advertiser.NativeScreenshotDisplay;
import models.entities.CreativeBannerEntity;
import models.entities.CreativeContainerEntity;
import models.formbinders.CreativeWorkFlowEntity;
import models.formelements.SelectOption;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.Play;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import services.MetadataAPI;
import views.html.advt.creative.creativeform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Files;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.api.entity.creative_banner.ImageUploadResponse;
import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.upload_to_cdn.IUploadToCDN;
import com.kritter.api.upload_to_cdn.everest.UploadToCDN;
import com.kritter.constants.CreativeContainerAPIEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.native_props.demand.NativeIcon;
import com.kritter.entity.native_props.demand.NativeScreenshot;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utild.ucloud_upload.upload.UploadToUCloud;
import com.kritter.utils.amazon_s3_upload.UploadToS3;
import com.kritter.utils.edgecast_upload.UploadToEdgecast;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;
public class CreativeController extends Controller {

	public static Form<CreativeContainerEntity> creativeContainerFormTemplate =  Form.form(CreativeContainerEntity.class);
	private static Form<CreativeBannerEntity> creativeBannerFormTemplate =  Form.form(CreativeBannerEntity.class);
	static Form<CreativeWorkFlowEntity> creativeWFForm = Form.form(CreativeWorkFlowEntity.class);


	private static List<Creative_container> getCreativeContainerList(CreativeContainerAPIEnum listDef, String account_guid,
			Option<Integer> containerId, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Creative_container> creativeContainerList = null;
		Connection con = null; 
		try{

			con = DB.getConnection(); 
			CreativeContainerListEntity cle = new CreativeContainerListEntity();
			cle.setAccount_guid(account_guid);
			cle.setCcenum(listDef);
			if(containerId!= null && containerId.nonEmpty())
				cle.setId(containerId.get());
			if(pageNo.nonEmpty())
				cle.setPage_no(pageNo.get()-1);
			if(pageSize.nonEmpty())
				cle.setPage_size(pageSize.get());
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
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return creativeContainerList;
	}

	@SecuredAction
	public static Result creativeAtrributes(int creativeType){
		ArrayNode creativeAttrOptions  = MetadataAPI.creativeAttributes(creativeType);
		return ok(creativeAttrOptions);
	}

	@SecuredAction
    public static Result creativeMacros(){
        ArrayNode creativeMacrosOptions  = MetadataAPI.creativeMacros();
        return ok(creativeMacrosOptions);
    }
    @SecuredAction
    public static Result videoTracking(){
        ArrayNode creativeMacrosOptions  = MetadataAPI.videoTracking();
        return ok(creativeMacrosOptions);
    }
	
	private static List<Creative_container> getCreativeContainers(String accountGuid, 
			Option<Integer> pageNo, Option<Integer> pageSize){
		List<Creative_container> containers = getCreativeContainerList(CreativeContainerAPIEnum.list_creative_container_by_account,
				accountGuid, null, pageNo,  pageSize);
		if(containers.size()>0){
			return containers;
		}else
			return new ArrayList<Creative_container>();
	}



	@SecuredAction
	public static Result addCreative(String accountGuid, Option<String> destination) {
		CreativeContainerEntity creativeContainer  = new CreativeContainerEntity();  
		creativeContainer.setAccount_guid(accountGuid);
		if(destination.nonEmpty())
			creativeContainer.setDestination(destination.get());
		return ok(views.html.advt.creative.creativeFormPage.render(
				creativeContainerFormTemplate.fill(creativeContainer), 
				new CreativeDisplay(creativeContainer.getEntity())));
	}

	@SecuredAction
	public static Result viewCreative(int creativeId) {
		Creative_container creativeContainer  = DataAPI.getCreativeContainer(creativeId); 
		return ok(views.html.advt.creative.creativeDisplayPage.render(new CreativeDisplayFull(creativeContainer)));
	}

	@SecuredAction
	public static Result editCreative(int creativeId) {
		Creative_container creativeContainer  = DataAPI.getCreativeContainer(creativeId); 

		if(creativeContainer !=null ){ 
			CreativeContainerEntity creativeContainerEntity  = new CreativeContainerEntity();  
			BeanUtils.copyProperties(creativeContainer, creativeContainerEntity);
			return ok(views.html.advt.creative.creativeFormPage.render(creativeContainerFormTemplate.fill(creativeContainerEntity)  , new CreativeDisplay(creativeContainerEntity.getEntity())));
		}else{
			return badRequest("Creative with given Id not found");
		}	
	}

	@SecuredAction
	public static Result addBanner(String accountGuid){
		CreativeBannerEntity creativeBanner = new CreativeBannerEntity();  
		creativeBanner.setAccount_guid(accountGuid);
		return ok(views.html.advt.creative.bannerform.render( creativeBannerFormTemplate.fill(creativeBanner)));
	}

	@SecuredAction
	public static Result editBanner(String bannerGuid){
		CreativeBannerEntity creativeBanner = new CreativeBannerEntity();   
		return ok(views.html.advt.creative.bannerform.render( creativeBannerFormTemplate.fill(creativeBanner)));
	}

	@SecuredAction
	public static Result listView(String accountGuid){  
		Account account = DataAPI.getAccountByGuid(accountGuid);
		CreativeListDisplay cld = new CreativeListDisplay(account);
		return ok(views.html.advt.creative.creativeListTpl.render(cld)); 			 
	}

	@SecuredAction
	public static Result list(String accountGuid, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Creative_container> ccList = null; 
		ccList = getCreativeContainers( accountGuid, pageNo, pageSize);
		ObjectNode result = Json.newObject();

		ArrayNode tpnodes = result.putArray("list");
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode tpnode = null;
		for (Creative_container cc : ccList) { 
			tpnode= objectMapper.valueToTree(new CreativeDisplay(cc)); 
			tpnodes.addPOJO(tpnode);
		}
		result.put("size", 30); 
		MetadataAPI.creativeTypes();

		return ok(result);
	}
	public static Result uploadBanner(String accountGuid){
	    return uploadBanner(accountGuid, false);
	}
	//@SecuredAction
	public static Result uploadBanner(String accountGuid,boolean isApicall){ 
		MultipartFormData multipartFormData = request().body().asMultipartFormData();

		//		String slotid = multipartFormData.asFormUrlEncoded().get("slot_id")[0]; 
		//		String accountGuid = multipartFormData.asFormUrlEncoded().get("account_guid")[0];

		FilePart banner = multipartFormData.getFile("file"); 


		if (banner != null) { 
			String fileName = banner.getFilename();
			File file = banner.getFile();

			String destFileUuid = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
			String destFilePath = getImageUrl(accountGuid, destFileUuid, fileName, false,false);

			File originalOutputFile = new File("public/"+destFilePath).getAbsoluteFile();
			originalOutputFile.getParentFile().mkdirs();
			try {
				if(!originalOutputFile.exists()){
					originalOutputFile.createNewFile();
				}
				Files.copy(file, originalOutputFile);
				BufferedImage bimg = ImageIO.read(file);
				int width          = bimg.getWidth();
				int height         = bimg.getHeight();
				List<SelectOption> slotOptions = validateUploadedImage( width, height);
				if(slotOptions != null && slotOptions.size()>0){
					String basePath =  getImageUrl(accountGuid, destFileUuid, fileName, false,true);
					String internalPath = "public/"+basePath;
					String externalPath = "/images/"+basePath;
					File outputFile = new File(internalPath).getAbsoluteFile();
					outputFile.getParentFile().mkdirs();
					String ext = FilenameUtils.getExtension(fileName);
					Thumbnails.of(file).size(120, 120).outputFormat(ext).imageType(BufferedImage.TYPE_INT_ARGB).toFile(new File(internalPath).getAbsoluteFile());
					if(isApicall){
					    ImageUploadResponse iur = new ImageUploadResponse();
					    iur.setErrorCode(ErrorEnum.NO_ERROR.getId());
					    iur.setMessage(ErrorEnum.NO_ERROR.getName());
					    iur.setSlotOptions(getSlotOptions(slotOptions).toString());
					    iur.setBannerUrl(getImageUrl(accountGuid, destFileUuid, fileName, true,false));
					    iur.setThumbUrl(externalPath);
					    return ok(iur.toJson().toString());
					}else{
					    ObjectNode  response = new ObjectNode(JsonNodeFactory.instance);					 
					    response.put("message", "Image Uploaded Succesfully");
					    response.put("slotOptions", getSlotOptions(slotOptions));
					    response.put("thumbUrl", externalPath);
					    response.put("bannerUrl", getImageUrl(accountGuid, destFileUuid, fileName, true,false));
					    return ok(response);
					}
				}else{
                    if(isApicall){
                        ImageUploadResponse iur = new ImageUploadResponse();
                        iur.setErrorCode(ErrorEnum.IMAGE_SIZE_MISMATCH.getId());
                        iur.setMessage(ErrorEnum.IMAGE_SIZE_MISMATCH.getName());
                        return ok(iur.toJson().toString());
                    }else{
                        ObjectNode  response = new ObjectNode(JsonNodeFactory.instance);
					    response.put("message", "Image size mismatch");
					    return badRequest(response);
                    }
				}

			} catch (Exception e) {
                if(isApicall){
                    ImageUploadResponse iur = new ImageUploadResponse();
                    iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
                    iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
                    return ok(iur.toJson().toString());
                }else{
                    Logger.error("Error in reading uploaded image property. "+ e.getMessage(),e);
                    flash("error", "File Uploading failed");
                }
			}
		} else {
            if(isApicall){
                ImageUploadResponse iur = new ImageUploadResponse();
                iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
                iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
                return ok(iur.toJson().toString());
            }else{
                flash("error", "File Uploading failed");
            }
		}
        if(isApicall){
            ImageUploadResponse iur = new ImageUploadResponse();
            iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
            iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
            return ok(iur.toJson().toString());
        }else{
            return ok("Banner uploaded successfully");
        }
	}

    public static Result uploadIcon(String accountGuid){
        return uploadIcon(accountGuid, false);
    }
    //@SecuredAction
    public static Result uploadIcon(String accountGuid,boolean isApicall){ 
        MultipartFormData multipartFormData = request().body().asMultipartFormData();
        FilePart icon = multipartFormData.getFile("file"); 
        if (icon != null) { 
            String fileName = icon.getFilename();
            File file = icon.getFile();
            String destFileUuid = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
            String destFilePath = getImageUrl(accountGuid, destFileUuid, fileName, false,false);
            File originalOutputFile = new File("public/"+destFilePath).getAbsoluteFile();
            originalOutputFile.getParentFile().mkdirs();
            try {
                if(!originalOutputFile.exists()){
                    originalOutputFile.createNewFile();
                }
                Files.copy(file, originalOutputFile);
                BufferedImage bimg = ImageIO.read(file);
                int width          = bimg.getWidth();
                int height         = bimg.getHeight();
                List<SelectOption> slotOptions = validateUploadedIcon( width, height);
                if(slotOptions != null && slotOptions.size()>0){
                    String basePath =  getImageUrl(accountGuid, destFileUuid, fileName, false,true);
                    String internalPath = "public/"+basePath;
                    String externalPath = "/images/"+basePath;
                    File outputFile = new File(internalPath).getAbsoluteFile();
                    outputFile.getParentFile().mkdirs();
                    String ext = FilenameUtils.getExtension(fileName);
                    Thumbnails.of(file).size(120, 120).outputFormat(ext).imageType(BufferedImage.TYPE_INT_ARGB).toFile(new File(internalPath).getAbsoluteFile());
                    if(isApicall){
                        ImageUploadResponse iur = new ImageUploadResponse();
                        iur.setErrorCode(ErrorEnum.NO_ERROR.getId());
                        iur.setMessage(ErrorEnum.NO_ERROR.getName());
                        iur.setSlotOptions(getSlotOptions(slotOptions).toString());
                        iur.setBannerUrl(getImageUrl(accountGuid, destFileUuid, fileName, true,false));
                        iur.setThumbUrl(externalPath);
                        return ok(iur.toJson().toString());
                    }else{
                        ObjectNode  response = new ObjectNode(JsonNodeFactory.instance);                     
                        response.put("message", "Image Uploaded Succesfully");
                        response.put("slotOptions", getSlotOptions(slotOptions));
                        response.put("thumbUrl", externalPath);
                        response.put("bannerUrl", getImageUrl(accountGuid, destFileUuid, fileName, true,false));
                        return ok(response);
                    }
                }else{
                    if(isApicall){
                        ImageUploadResponse iur = new ImageUploadResponse();
                        iur.setErrorCode(ErrorEnum.IMAGE_SIZE_MISMATCH.getId());
                        iur.setMessage(ErrorEnum.IMAGE_SIZE_MISMATCH.getName());
                        return ok(iur.toJson().toString());
                    }else{
                        ObjectNode  response = new ObjectNode(JsonNodeFactory.instance);
                        response.put("message", "Image size mismatch");
                        return badRequest(response);
                    }
                }
            } catch (Exception e) {
                if(isApicall){
                    ImageUploadResponse iur = new ImageUploadResponse();
                    iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
                    iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
                    return ok(iur.toJson().toString());
                }else{
                    Logger.error("Error in reading uploaded image property. "+ e.getMessage(),e);
                    flash("error", "File Uploading failed");
                }
            }
        } else {
            if(isApicall){
                ImageUploadResponse iur = new ImageUploadResponse();
                iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
                iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
                return ok(iur.toJson().toString());
            }else{
                flash("error", "Icon Uploading failed");
            }
        }
        if(isApicall){
            ImageUploadResponse iur = new ImageUploadResponse();
            iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
            iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
            return ok(iur.toJson().toString());
        }else{
            return ok("Icon uploaded successfully");
        }
    }

    public static Result uploadScreenshot(String accountGuid){
        return uploadScreenshot(accountGuid, false);
    }
    //@SecuredAction
    public static Result uploadScreenshot(String accountGuid,boolean isApicall){ 
        MultipartFormData multipartFormData = request().body().asMultipartFormData();
        FilePart screenshot = multipartFormData.getFile("file"); 
        if (screenshot != null) { 
            String fileName = screenshot.getFilename();
            File file = screenshot.getFile();
            String destFileUuid = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
            String destFilePath = getImageUrl(accountGuid, destFileUuid, fileName, false,false);
            File originalOutputFile = new File("public/"+destFilePath).getAbsoluteFile();
            originalOutputFile.getParentFile().mkdirs();
            try {
                if(!originalOutputFile.exists()){
                    originalOutputFile.createNewFile();
                }
                Files.copy(file, originalOutputFile);
                BufferedImage bimg = ImageIO.read(file);
                int width          = bimg.getWidth();
                int height         = bimg.getHeight();
                List<SelectOption> slotOptions = validateUploadedScreenshot( width, height);
                if(slotOptions != null && slotOptions.size()>0){
                    String basePath =  getImageUrl(accountGuid, destFileUuid, fileName, false,true);
                    String internalPath = "public/"+basePath;
                    String externalPath = "/images/"+basePath;
                    File outputFile = new File(internalPath).getAbsoluteFile();
                    outputFile.getParentFile().mkdirs();
                    String ext = FilenameUtils.getExtension(fileName);
                    Thumbnails.of(file).size(120, 120).outputFormat(ext).imageType(BufferedImage.TYPE_INT_ARGB).toFile(new File(internalPath).getAbsoluteFile());
                    if(isApicall){
                        ImageUploadResponse iur = new ImageUploadResponse();
                        iur.setErrorCode(ErrorEnum.NO_ERROR.getId());
                        iur.setMessage(ErrorEnum.NO_ERROR.getName());
                        iur.setSlotOptions(getSlotOptions(slotOptions).toString());
                        iur.setBannerUrl(getImageUrl(accountGuid, destFileUuid, fileName, true,false));
                        iur.setThumbUrl(externalPath);
                        return ok(iur.toJson().toString());
                    }else{
                        ObjectNode  response = new ObjectNode(JsonNodeFactory.instance);                     
                        response.put("message", "Image Uploaded Succesfully");
                        response.put("slotOptions", getSlotOptions(slotOptions));
                        response.put("thumbUrl", externalPath);
                        response.put("bannerUrl", getImageUrl(accountGuid, destFileUuid, fileName, true,false));
                        return ok(response);
                    }
                }else{
                    if(isApicall){
                        ImageUploadResponse iur = new ImageUploadResponse();
                        iur.setErrorCode(ErrorEnum.IMAGE_SIZE_MISMATCH.getId());
                        iur.setMessage(ErrorEnum.IMAGE_SIZE_MISMATCH.getName());
                        return ok(iur.toJson().toString());
                    }else{
                        ObjectNode  response = new ObjectNode(JsonNodeFactory.instance);
                        response.put("message", "Image size mismatch");
                        return badRequest(response);
                    }
                }
            } catch (Exception e) {
                if(isApicall){
                    ImageUploadResponse iur = new ImageUploadResponse();
                    iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
                    iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
                    return ok(iur.toJson().toString());
                }else{
                    Logger.error("Error in reading uploaded screenshot property. "+ e.getMessage(),e);
                    flash("error", "screenshot Uploading failed");
                }
            }
        } else {
            if(isApicall){
                ImageUploadResponse iur = new ImageUploadResponse();
                iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
                iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
                return ok(iur.toJson().toString());
            }else{
                flash("error", "screenshot Uploading failed");
            }
        }
        if(isApicall){
            ImageUploadResponse iur = new ImageUploadResponse();
            iur.setErrorCode(ErrorEnum.IMAGE_UPLOAD_FAILED.getId());
            iur.setMessage(ErrorEnum.IMAGE_UPLOAD_FAILED.getName());
            return ok(iur.toJson().toString());
        }else{
            return ok("screenshot uploaded successfully");
        }
    }
    public static Result uploadDirectvideo(String accountGuid){
        return uploadDirectvideo(accountGuid, false);
    }
    //@SecuredAction
    public static Result uploadDirectvideo(String accountGuid,boolean isApicall){ 
    	MultipartFormData multipartFormData = request().body().asMultipartFormData();
    	FilePart directvideo = multipartFormData.getFile("file"); 
    	if (directvideo != null) { 
    		String fileName = directvideo.getFilename();
    		File file = directvideo.getFile();
    		String destFileUuid = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
    		String destFilePath = getImageUrl(accountGuid, destFileUuid, fileName, false,false);
    		File originalOutputFile = new File("public/"+destFilePath).getAbsoluteFile();
    		originalOutputFile.getParentFile().mkdirs();
            List<SelectOption> slotOptions = validateDirectVideo();
    		try {
    			if(!originalOutputFile.exists()){
    				originalOutputFile.createNewFile();
    			}
    			Files.copy(file, originalOutputFile);
    			if(isApicall){
    				ImageUploadResponse iur = new ImageUploadResponse();
    				iur.setErrorCode(ErrorEnum.NO_ERROR.getId());
    				iur.setMessage(ErrorEnum.NO_ERROR.getName());
    				iur.setSlotOptions(getSlotOptions(slotOptions).toString());
    				iur.setBannerUrl(getImageUrl(accountGuid, destFileUuid, fileName, true,false));
    				iur.setThumbUrl("");
    				return ok(iur.toJson().toString());
    			}else{
    				ObjectNode  response = new ObjectNode(JsonNodeFactory.instance);                     
    				response.put("message", "Video Uploaded Succesfully");
    				response.put("slotOptions",getSlotOptions(slotOptions));
    				response.put("thumbUrl", "");
    				response.put("bannerUrl", getImageUrl(accountGuid, destFileUuid, fileName, true,false));
    				return ok(response);
    			}
    		} catch (Exception e) {
    			if(isApicall){
    				ImageUploadResponse iur = new ImageUploadResponse();
    				iur.setErrorCode(ErrorEnum.VIDEO_UPLOAD_FAILED.getId());
    				iur.setMessage(ErrorEnum.VIDEO_UPLOAD_FAILED.getName());
    				return ok(iur.toJson().toString());
    			}else{
    				Logger.error("Error in reading uploaded video property. "+ e.getMessage(),e);
    				flash("error", "video Uploading failed");
    			}
    		}
    	} else {
    		if(isApicall){
    			ImageUploadResponse iur = new ImageUploadResponse();
    			iur.setErrorCode(ErrorEnum.VIDEO_UPLOAD_FAILED.getId());
    			iur.setMessage(ErrorEnum.VIDEO_UPLOAD_FAILED.getName());
    			return ok(iur.toJson().toString());
    		}else{
    			flash("error", "video Uploading failed");
    		}
    	}
    	if(isApicall){
    		ImageUploadResponse iur = new ImageUploadResponse();
    		iur.setErrorCode(ErrorEnum.VIDEO_UPLOAD_FAILED.getId());
    		iur.setMessage(ErrorEnum.VIDEO_UPLOAD_FAILED.getName());
    		return ok(iur.toJson().toString());
    	}else{
    		return ok("video uploaded successfully");
    	}
    }

    private static ArrayNode getSlotOptions(List<SelectOption> options){
		ArrayNode optionNode = new ArrayNode(JsonNodeFactory.instance);
		for (SelectOption selectOption : options) {
			optionNode.add(selectOption.toJson());
		}
		return optionNode;
	}

	private static String getImageUrl(String accountGuid, String bannerUuid, String filename, boolean relative, boolean thumbnail){		
		String extension = filename.substring(filename.lastIndexOf(".")+1); 
		String path = "/"+accountGuid+"/"+bannerUuid+"."+extension; 
		if(relative){
			return path;
		}else{
			if(thumbnail)
				path = "banners/thumbnails"+path; 
			else
				path = "banners/original"+path; 
		}

		return path;
	}

	private static List<SelectOption> validateUploadedImage(int width,
			int height) { 
		List<SelectOption> slotIds = MetadataAPI.creativeSlots();
		HashMap<String, List<SelectOption>> slotIdMap = new HashMap<String, List<SelectOption>>();
		List<SelectOption> optionsList = new ArrayList<SelectOption>();
		String dim = "";
		for (SelectOption selectOption : slotIds) {
			String[] tmp = selectOption.getLabel().split("\\-");
			if(tmp.length ==2){
				dim = tmp[0]; 
				if(slotIdMap.containsKey(dim))
					optionsList = slotIdMap.get(dim);
				else{
					optionsList = new ArrayList<SelectOption>();
					slotIdMap.put(dim, optionsList);
				}
				optionsList.add(selectOption);
			}
		}
		return slotIdMap.get(width+"*"+height);
	}
    private static List<SelectOption> validateDirectVideo() { 
        List<SelectOption> slotIds = MetadataAPI.creativeSlots();
        return slotIds; 
    }
    private static List<SelectOption> validateUploadedIcon(int width,
            int height) { 
        List<SelectOption> slotIds = MetadataAPI.iconSlots();
        HashMap<String, List<SelectOption>> slotIdMap = new HashMap<String, List<SelectOption>>();
        List<SelectOption> optionsList = new ArrayList<SelectOption>();
        String dim = "";
        for (SelectOption selectOption : slotIds) {
            String[] tmp = selectOption.getLabel().split("\\-");
            if(tmp.length ==2){
                dim = tmp[0]; 
                if(slotIdMap.containsKey(dim))
                    optionsList = slotIdMap.get(dim);
                else{
                    optionsList = new ArrayList<SelectOption>();
                    slotIdMap.put(dim, optionsList);
                }
                optionsList.add(selectOption);
            }
        }
        return slotIdMap.get(width+"*"+height);
    }
    private static List<SelectOption> validateUploadedScreenshot(int width,
            int height) { 
        List<SelectOption> slotIds = MetadataAPI.screenshotSlots();
        HashMap<String, List<SelectOption>> slotIdMap = new HashMap<String, List<SelectOption>>();
        List<SelectOption> optionsList = new ArrayList<SelectOption>();
        String dim = "";
        for (SelectOption selectOption : slotIds) {
            String[] tmp = selectOption.getLabel().split("\\-");
            if(tmp.length ==2){
                dim = tmp[0]; 
                if(slotIdMap.containsKey(dim))
                    optionsList = slotIdMap.get(dim);
                else{
                    optionsList = new ArrayList<SelectOption>();
                    slotIdMap.put(dim, optionsList);
                }
                optionsList.add(selectOption);
            }
        }
        return slotIdMap.get(width+"*"+height);
    }

	private static boolean postToEvrest(File originalOutputFile, String destFileUuid){
	    boolean cdn_upload_success = false;
	    IUploadToCDN uploadTOCDN = new UploadToCDN();
	    String url = Play.application().configuration().getString("cdn_upload_url");
	    HashMap<String, String> postParams =  new HashMap<String, String>();
	    postParams.put("account", Play.application().configuration().getString("cdn_upload_account"));
	    postParams.put("j_security_username", Play.application().configuration().getString("cdn_upload_j_security_username"));
	    postParams.put("j_security_password", Play.application().configuration().getString("cdn_upload_j_security_password"));
	    cdn_upload_success = uploadTOCDN.upload(url, null, postParams, originalOutputFile, destFileUuid); 
	    return cdn_upload_success ;
	}
	   private static boolean postToS3(File originalOutputFile){
	       try{
	           String s3_bucket_name = Play.application().configuration().getString("s3_bucket_name");
	           String s3_access_key = Play.application().configuration().getString("s3_access_key");
	           String s3_secret_key = Play.application().configuration().getString("s3_secret_key");
	           String s3_key_name_prefix = Play.application().configuration().getString("s3_key_name_prefix");
	           UploadToS3.upload(s3_bucket_name,s3_access_key, s3_secret_key,s3_key_name_prefix+"/"+originalOutputFile.getName(), originalOutputFile);
	           return true;
	       }catch(Exception e){
	           Logger.error(e.getMessage(),e);
	           return false;
	       }
	   }
       private static boolean postToEdgeCast(File originalOutputFile){
           try{
               String edgecast_host = Play.application().configuration().getString("edgecast_host");
               String edgecast_port = Play.application().configuration().getString("edgecast_port");
               String edgecast_username = Play.application().configuration().getString("edgecast_username");
               String edgecast_password = Play.application().configuration().getString("edgecast_password");
               return UploadToEdgecast.upload(edgecast_host, edgecast_port, originalOutputFile, 
                       edgecast_username, edgecast_password);
           }catch(Exception e){
               Logger.error(e.getMessage(),e);
               return false;
           }
       }
       private static boolean postToUCloud(File originalOutputFile){
           try{
               String ucloudPublicKey = Play.application().configuration().getString("ucloudPublicKey");
               String ucloudPrivateKey = Play.application().configuration().getString("ucloudPrivateKey");
               String ucloudproxySuffix = Play.application().configuration().getString("ucloudproxySuffix");
               String uclouddownloadProxySuffix = Play.application().configuration().getString("uclouddownloadProxySuffix");
               String ucloudbucketName = Play.application().configuration().getString("ucloudbucketName");
               return UploadToUCloud.uploadToUCloud(ucloudPublicKey, ucloudPrivateKey, 
                       ucloudproxySuffix, uclouddownloadProxySuffix, originalOutputFile, ucloudbucketName);
           }catch(Exception e){
               Logger.error(e.getMessage(),e);
               return false;
           }
       }
	public static Result saveBanner(){
		JsonNode reqData = request().body().asJson();
		ObjectMapper om = new ObjectMapper();
		ObjectNode  response = new ObjectNode(JsonNodeFactory.instance); 
		Connection con = null; 
		ArrayNode savedBanners = response.putArray("banners");
		if(reqData.get("banners") !=  null ){
			try {
				con = DB.getConnection();
				ArrayNode banners =(ArrayNode) reqData.get("banners");
				BannerDisplay bannerDisplay = null;
				for (JsonNode jsonNode : banners) {
				((ObjectNode)jsonNode).remove("slotName");
				((ObjectNode)jsonNode).remove("slotDescription");
					Creative_banner cb = om.treeToValue(jsonNode, Creative_banner.class);
					cb.setModified_by(1);
					Message msg = ApiDef.insert_creative_banner(con, cb );
					if(msg.getError_code()==0){		
						bannerDisplay = new BannerDisplay(cb);
						String name = bannerDisplay.getBannerUrl();
						String uuid = name.substring(name.lastIndexOf("/")+1);
						uuid = uuid.substring(0, uuid.lastIndexOf("."));
						savedBanners.add(bannerDisplay.toJson());
						String uploadtoCDNFlag = Play.application().configuration().getString("cdn_upload");
						if("evrest".equals(uploadtoCDNFlag)){
						    boolean cdn_upload_success = postToEvrest(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile(), uuid);
						    if(!cdn_upload_success){
						        response.put("message", "CDN upload failed");
						        return badRequest(response);
						    }
						}else if("s3".equals(uploadtoCDNFlag)){
	                            boolean s3_upload_success = postToS3(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
	                            if(!s3_upload_success){
	                                response.put("message", "S3 upload failed");
	                                return badRequest(response);
	                            }
						}else if("edgecast".equals(uploadtoCDNFlag)){
                            boolean upload_success = postToEdgeCast(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                            if(!upload_success){
                                response.put("message", "Edgecast upload failed");
                                return badRequest(response);
                            }
						}else if("ucloud".equals(uploadtoCDNFlag)){
                            boolean upload_success = postToUCloud(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                            if(!upload_success){
                                response.put("message", "Ucloud upload failed");
                                return badRequest(response);
                            }
                        }
					} 
				}
				response.put("message", "Banner Saved Sucessfully");  
				return ok(response);

			} catch (Exception e) {
				Logger.error("An exception has occured while saving banner",e);
			}
			finally{
				try{
					if(con != null)
						con.close();
				}catch(SQLException ex){
					Logger.error("Failed to close DD connection",ex);
				}
			}
			response.put("message", "Banner save failed. Please retry");

		}
		return badRequest(response);
	}
	
	public static String saveBannerApi(Creative_banner cb){
		BannerDisplay bannerDisplay = null;

		bannerDisplay = new BannerDisplay(cb);
		String name = bannerDisplay.getBannerUrl();
		String uuid = name.substring(name.lastIndexOf("/")+1);
		uuid = uuid.substring(0, uuid.lastIndexOf("."));
		String uploadtoCDNFlag = Play.application().configuration().getString("cdn_upload");
		if("evrest".equals(uploadtoCDNFlag)){
		    boolean cdn_upload_success = postToEvrest(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile(), uuid);
		    if(!cdn_upload_success){
		        return "CDN upload failed";
		    }
		}else if("s3".equals(uploadtoCDNFlag)){
                boolean s3_upload_success = postToS3(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                if(!s3_upload_success){
                    return "S3 upload failed";
                }
		}else if("edgecast".equals(uploadtoCDNFlag)){
            boolean upload_success = postToEdgeCast(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
            if(!upload_success){
                return "Edgecast upload failed";
            }
		}else if("ucloud".equals(uploadtoCDNFlag)){
            boolean upload_success = postToUCloud(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
            if(!upload_success){
                return "Ucloud upload failed";
            }
        }
		return "success";
	
	}

    public static Result saveIcon(){
        JsonNode reqData = request().body().asJson();
        ObjectMapper om = new ObjectMapper();
        ObjectNode  response = new ObjectNode(JsonNodeFactory.instance); 
        Connection con = null; 
        ArrayNode savedIcons = response.putArray("nativeicons");
        if(reqData.get("nativeicons") !=  null ){
            try {
                con = DB.getConnection();
                ArrayNode nativeicons =(ArrayNode) reqData.get("nativeicons");
                NativeIconDisplay nativeIconDisplay = null;
                for (JsonNode jsonNode : nativeicons) {
                ((ObjectNode)jsonNode).remove("slotName");
                ((ObjectNode)jsonNode).remove("slotDescription");
                    NativeIcon cb = om.treeToValue(jsonNode, NativeIcon.class);
                    cb.setModified_by(1);
                    Message msg = ApiDef.insert_native_icon(con, cb );
                    if(msg.getError_code()==0){     
                        nativeIconDisplay = new NativeIconDisplay(cb);
                        String name = nativeIconDisplay.getBannerUrl();
                        String uuid = name.substring(name.lastIndexOf("/")+1);
                        uuid = uuid.substring(0, uuid.lastIndexOf("."));
                        savedIcons.add(nativeIconDisplay.toJson());
                        String uploadtoCDNFlag = Play.application().configuration().getString("cdn_upload");
                        if("evrest".equals(uploadtoCDNFlag)){
                            boolean cdn_upload_success = postToEvrest(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile(), uuid);
                            if(!cdn_upload_success){
                                response.put("message", "CDN upload failed");
                                return badRequest(response);
                            }
                        }else if("s3".equals(uploadtoCDNFlag)){
                                boolean s3_upload_success = postToS3(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                                if(!s3_upload_success){
                                    response.put("message", "S3 upload failed");
                                    return badRequest(response);
                                }
                        }else if("edgecast".equals(uploadtoCDNFlag)){
                            boolean upload_success = postToEdgeCast(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                            if(!upload_success){
                                response.put("message", "EdgeCast upload failed");
                                return badRequest(response);
                            }
                        }else if("ucloud".equals(uploadtoCDNFlag)){
                            boolean upload_success = postToUCloud(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                            if(!upload_success){
                                response.put("message", "UCloud upload failed");
                                return badRequest(response);
                            }
                        }
                    } 
                }
                response.put("message", "Icon Saved Sucessfully");  
                return ok(response);

            } catch (Exception e) {
                Logger.error("An exception has occured while saving icon",e);
            }
            finally{
                try{
                    if(con != null)
                        con.close();
                }catch(SQLException ex){
                    Logger.error("Failed to close DD connection",ex);
                }
            }
            response.put("message", "Icon save failed. Please retry");

        }
        return badRequest(response);
    }

    public static Result saveScreenshot(){
        JsonNode reqData = request().body().asJson();
        ObjectMapper om = new ObjectMapper();
        ObjectNode  response = new ObjectNode(JsonNodeFactory.instance); 
        Connection con = null; 
        ArrayNode savedScreenshots = response.putArray("nativescreenshots");
        if(reqData.get("nativescreenshots") !=  null ){
            try {
                con = DB.getConnection();
                ArrayNode nativescreenshots =(ArrayNode) reqData.get("nativescreenshots");
                NativeScreenshotDisplay nativescreenshotDisplay = null;
                for (JsonNode jsonNode : nativescreenshots) {
                ((ObjectNode)jsonNode).remove("slotName");
                ((ObjectNode)jsonNode).remove("slotDescription");
                    NativeScreenshot cb = om.treeToValue(jsonNode, NativeScreenshot.class);
                    cb.setModified_by(1);
                    Message msg = ApiDef.insert_native_screenshot(con, cb );
                    if(msg.getError_code()==0){     
                        nativescreenshotDisplay = new NativeScreenshotDisplay(cb);
                        String name = nativescreenshotDisplay.getBannerUrl();
                        String uuid = name.substring(name.lastIndexOf("/")+1);
                        uuid = uuid.substring(0, uuid.lastIndexOf("."));
                        savedScreenshots.add(nativescreenshotDisplay.toJson());
                        String uploadtoCDNFlag = Play.application().configuration().getString("cdn_upload");
                        if("evrest".equals(uploadtoCDNFlag)){
                            boolean cdn_upload_success = postToEvrest(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile(), uuid);
                            if(!cdn_upload_success){
                                response.put("message", "CDN upload failed");
                                return badRequest(response);
                            }
                        }else if("s3".equals(uploadtoCDNFlag)){
                                boolean s3_upload_success = postToS3(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                                if(!s3_upload_success){
                                    response.put("message", "S3 upload failed");
                                    return badRequest(response);
                                }
                        }else if("edgecast".equals(uploadtoCDNFlag)){
                            boolean upload_success = postToEdgeCast(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                            if(!upload_success){
                                response.put("message", "Edgecast upload failed");
                                return badRequest(response);
                            }
                        }else if("ucloud".equals(uploadtoCDNFlag)){
                            boolean upload_success = postToUCloud(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                            if(!upload_success){
                                response.put("message", "Ucloud upload failed");
                                return badRequest(response);
                            }
                        }
                    } 
                }
                response.put("message", "Screenshot Saved Sucessfully");  
                return ok(response);

            } catch (Exception e) {
                Logger.error("An exception has occured while saving screenshot",e);
            }
            finally{
                try{
                    if(con != null)
                        con.close();
                }catch(SQLException ex){
                    Logger.error("Failed to close DD connection",ex);
                }
            }
            response.put("message", "Screenshot save failed. Please retry");
        }
        return badRequest(response);
    }
    public static Result saveDirectvideo(){
        JsonNode reqData = request().body().asJson();
        ObjectMapper om = new ObjectMapper();
        ObjectNode  response = new ObjectNode(JsonNodeFactory.instance); 
        Connection con = null; 
        ArrayNode saveddirectvideos = response.putArray("directvideos");
        if(reqData.get("directvideos") !=  null ){
            try {
                con = DB.getConnection();
                ArrayNode directvideos =(ArrayNode) reqData.get("directvideos");
                DirectvideoDisplay directVideoDisplay = null;
                for (JsonNode jsonNode : directvideos) {
                ((ObjectNode)jsonNode).remove("slotName");
                ((ObjectNode)jsonNode).remove("slotDescription");
                    VideoInfo cb = om.treeToValue(jsonNode, VideoInfo.class);
                    cb.setModified_by(1);
                    Message msg = ApiDef.insert_video_info(con, cb );
                    if(msg.getError_code()==0){     
                    	directVideoDisplay = new DirectvideoDisplay(cb);
                        String name = directVideoDisplay.getBannerUrl();
                        String uuid = name.substring(name.lastIndexOf("/")+1);
                        uuid = uuid.substring(0, uuid.lastIndexOf("."));
                        saveddirectvideos.add(directVideoDisplay.toJson());
                        String uploadtoCDNFlag = Play.application().configuration().getString("cdn_upload");
                        if("evrest".equals(uploadtoCDNFlag)){
                            boolean cdn_upload_success = postToEvrest(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile(), uuid);
                            if(!cdn_upload_success){
                                response.put("message", "CDN upload failed");
                                return badRequest(response);
                            }
                        }else if("s3".equals(uploadtoCDNFlag)){
                                boolean s3_upload_success = postToS3(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                                if(!s3_upload_success){
                                    response.put("message", "S3 upload failed");
                                    return badRequest(response);
                                }
                        }else if("edgecast".equals(uploadtoCDNFlag)){
                            boolean upload_success = postToEdgeCast(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                            if(!upload_success){
                                response.put("message", "EdgeCast upload failed");
                                return badRequest(response);
                            }
                        }else if("ucloud".equals(uploadtoCDNFlag)){
                            boolean upload_success = postToUCloud(new File("public"+name.substring(name.indexOf("/images/")+7)).getAbsoluteFile());
                            if(!upload_success){
                                response.put("message", "UCloud upload failed");
                                return badRequest(response);
                            }
                        }
                    } 
                }
                response.put("message", "directvideo Saved Sucessfully");  
                return ok(response);

            } catch (Exception e) {
                Logger.error("An exception has occured while saving directvideo",e);
            }
            finally{
                try{
                    if(con != null)
                        con.close();
                }catch(SQLException ex){
                    Logger.error("Failed to close DD connection",ex);
                }
            }
            response.put("message", "directvideo save failed. Please retry");

        }
        return badRequest(response);
    }

    
    @SecuredAction
	public static Result saveCreative() {
		Form<CreativeContainerEntity> creativeContainerForm = creativeContainerFormTemplate.bindFromRequest(); 
		CreativeContainerEntity creativeContainer = null;
		Creative_container cc = new Creative_container();
		Connection con = null;
		Message msg = null; 
		try {
			if(!creativeContainerForm.hasErrors()){
				creativeContainer = creativeContainerForm.get();
				cc = creativeContainer.getEntity();
				if(cc.getCreative_attr()==null ||"".equals(cc.getCreative_attr())||"[]".equals(cc.getCreative_attr())){
				    creativeContainerForm.reject("format_id","Select At least One Creative Attribute Below");
				    return ok(views.html.advt.creative.creativeFormPage.render(creativeContainerForm ,  new CreativeDisplay(cc) ));
				}
                if(cc.getLabel()==null ||"".equals(cc.getLabel().trim())){
                    creativeContainerForm.reject("label","Empty Label");
                    return ok(views.html.advt.creative.creativeFormPage.render(creativeContainerForm ,  new CreativeDisplay(cc) ));
                }
				cc.setModified_by(1); 

				con = DB.getConnection();
				if(creativeContainer.getId()  == -1)
					msg = ApiDef.insert_creative_container(con, cc);
				else
					msg = ApiDef.update_creative_container(con, cc);

				if(msg.getError_code() == 0) {
					String destination = creativeContainerForm.field("destination").value();
					if(!"".equals(destination)){
						destination = destination+"&creativeId="+ cc.getGuid();
						return redirect(destination);
					}

					else
						return redirect(routes.CreativeController.listView(cc.getAccount_guid()));
				} 
			}else{ 
			    return ok(views.html.advt.creative.creativeFormPage.render(creativeContainerForm ,  new CreativeDisplay(cc) ));
			}
			
		} catch (Exception e) {
			Logger.error("A problem was encountered while saving the creative. Please try again", e );
		}
		finally{
			try{
				if(con!=null){ 
					con.close();
				}
			}catch(Exception e){
				Logger.error("Exception while closing DB connection", e);
				return badRequest("Creative Upload Failed "); 
			}
		}

		String accountGuid = creativeContainerForm.field("account_guid").value();
		String id = creativeContainerForm.field("id").value();
		if(creativeContainer == null)
			creativeContainer = new CreativeContainerEntity();
		cc = new Creative_container();
		if(id != ""){
			cc = DataAPI.getCreativeContainer(Integer.parseInt(id));			 
		} 
		creativeContainer.setAccount_guid(accountGuid);
		return badRequest(creativeform.render(creativeContainerForm ,  new CreativeDisplay(cc) ));
	}

	public static Result bannerList(int creativeId){
		ArrayNode banners = new ArrayNode(JsonNodeFactory.instance);
		if(creativeId != -1){
			Creative_container cc = DataAPI.getCreativeContainer(creativeId);
			List<Creative_banner> bannerList = DataAPI.getCreativeBannerList(cc.getResource_uri_ids());
			for (Creative_banner creative_banner : bannerList) {  
				banners.addPOJO(new BannerDisplay(creative_banner).toJson());
			}
		}
		return ok(banners);
	}
    public static Result nativeIconList(int creativeId){
        ArrayNode nativeicons = new ArrayNode(JsonNodeFactory.instance);
        if(creativeId != -1){
            Creative_container cc = DataAPI.getCreativeContainer(creativeId);

            List<NativeIcon> nativeList = DataAPI.getNativeIconList(cc.getNative_icons());

            for (NativeIcon native_icon : nativeList) {  
                nativeicons.addPOJO(new NativeIconDisplay(native_icon).toJson());
            }
        }
        return ok(nativeicons);
    }
    public static Result nativeScreenshotList(int creativeId){
        ArrayNode nativescreenshots = new ArrayNode(JsonNodeFactory.instance);
        if(creativeId != -1){
            Creative_container cc = DataAPI.getCreativeContainer(creativeId);

            List<NativeScreenshot> nsList = DataAPI.getNativeScreenshotList(cc.getNative_screenshots());

            for (NativeScreenshot native_screenshot : nsList) {  
                nativescreenshots.addPOJO(new NativeScreenshotDisplay(native_screenshot).toJson());
            }
        }

        return ok(nativescreenshots);
    }
    public static Result directvideoList(int creativeId){
        ArrayNode directvideos = new ArrayNode(JsonNodeFactory.instance);
        if(creativeId != -1){
            Creative_container cc = DataAPI.getCreativeContainer(creativeId);

            List<VideoInfo> videoinfoList = DataAPI.getVideoInfoList(cc.getDirect_videos());

            for (VideoInfo video_info : videoinfoList) {  
            	directvideos.addPOJO(new DirectvideoDisplay(video_info).toJson());
            }
        }
        return ok(directvideos);
    }
	
	@SecuredAction
	public static Result updateStatusForm(int creativeId,   String action) {
	    CreativeWorkFlowEntity creativeWfEntity = new CreativeWorkFlowEntity( creativeId, action); 
	    return ok(views.html.advt.creative.creativeWorkflowForm.render(creativeWFForm.fill(creativeWfEntity), action));
	}
    @SecuredAction
    public static Result updateStatus(){
        Form<CreativeWorkFlowEntity> filledForm = creativeWFForm.bindFromRequest();
        Connection con = null;
        ObjectNode response = Json.newObject();
        try{
            Message statusMsg = null;
            if(!filledForm.hasErrors()){
                con = DB.getConnection();
                CreativeWorkFlowEntity creativeWfEntity = filledForm.get();
                CreativeContainerListEntity cc = new CreativeContainerListEntity();
                cc.setId(creativeWfEntity.getCreativeId());
                switch(Actions.valueOf(creativeWfEntity.getAction())){        
                case Approve: 
                    cc.setCcenum(CreativeContainerAPIEnum.update_status);
                    cc.setStatus_id(StatusIdEnum.Active);
                    statusMsg = ApiDef.update_creative_container_status(con, cc);        
                    break;
                case Reject: 
                    cc.setCcenum(CreativeContainerAPIEnum.update_status);
                    cc.setStatus_id(StatusIdEnum.Rejected);
                    statusMsg = ApiDef.update_creative_container_status(con, cc); 
                    break;
                default:
                    break;
                }
                if(statusMsg.getError_code()==0){
                    response.put("message", "Update Successful");
                    return ok(response);
                }else{
                    response.put("message", "Update Failed. Please retry");
                    return badRequest(response);
                } 
            }
            response.put("message", "Update Failed. Please retry");
            return badRequest(response);
        }catch(Exception e){
            play.Logger.error(e.getMessage(),e);
            response.put("message", "Update Failed. Please retry");
            return badRequest(response);
        }
        finally{
                try {
                    if(con != null){
                        con.close();
                    }
                } catch (SQLException e) {
                        play.Logger.error(e.getMessage(),e);
                }
        }

    }
    @SecuredAction
    public static Result updateMultipleCreative(String action, Option<String> ids){
        ObjectNode response = Json.newObject();
        Connection con = null;
        try{
            if(action != null && ids != null){
                String idlist = ids.get();
                if(idlist == null){
                    response.put("message", "ids null");return badRequest(response);
                }
                String idfinal = idlist.replaceAll("_", ",");
                con = DB.getConnection();
                CreativeContainerListEntity cc= new CreativeContainerListEntity();
                cc.setId_list(idfinal);
                if("Approve".equals(action)){
                    cc.setCcenum(CreativeContainerAPIEnum.update_multiple_status);
                    cc.setStatus_id(StatusIdEnum.Active);
                    ApiDef.update_creative_container_status(con, cc);        
                    response.put("message", "approve done");return ok(response);

                }else if("Reject".equals(action)){
                    cc.setCcenum(CreativeContainerAPIEnum.update_multiple_status);
                    cc.setStatus_id(StatusIdEnum.Rejected);
                    ApiDef.update_creative_container_status(con, cc);        
                    response.put("message", "reject done");return ok(response);

                }else{
                    response.put("message", "action : not present");return badRequest(response);
                }
            }else{
                response.put("message", "action or ids : null");return badRequest(response);
            }
        }catch(Exception e){
            play.Logger.error(e.getMessage(),e);
            response.put("message", "action or ids : null");return badRequest(response);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                play.Logger.error(e.getMessage(),e);
            }
        }
    }
}
