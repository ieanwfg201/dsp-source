package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import models.EntityList;
import models.EntityListFilter;
import models.Constants.Actions;
import models.accounts.displays.AccountDisplay;
import models.accounts.displays.AdvertiserDisplay;
import models.accounts.displays.PublisherDisplay;
import models.entities.AccountEntity;
import models.formbinders.AccountWorkflowEntity;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.Play;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import services.EntityListDataService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Files;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.targeting_profile.FileUploadResponse;
import com.kritter.api.upload_to_cdn.IUploadToCDN;
import com.kritter.api.upload_to_cdn.everest.UploadToCDN;
import com.kritter.constants.Account_Type;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utild.ucloud_upload.upload.UploadToUCloud;
import com.kritter.utils.amazon_s3_upload.UploadToS3;
import com.kritter.utils.edgecast_upload.UploadToEdgecast;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

import controllers.advertiser.ValidateIpFile;


public class AccountsController extends Controller{

	static Form<AccountEntity> accountFormData = Form.form(AccountEntity.class);
	static Form<AccountWorkflowEntity> accountWorkflowFormData = Form.form(AccountWorkflowEntity.class);
	private static String adx_based_exchanges = Play.application().configuration().getString("adx_based_exchanges");
	private static String adx_ext = Play.application().configuration().getString("adx_ext");

	public static Account getAccount(String userId){
		Account account = new Account();
		if(userId !=null){
			account.setUserid(userId);
			Connection dbConnection = null;
			try{
			    dbConnection = DB.getConnection(); 
				AccountMsgPair actMsgPair = ApiDef.get_Account(dbConnection, account);
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


	@SecuredAction
	public static Result add(   String accountType) {
		AccountEntity account = new AccountEntity();
		account.setStatus(StatusIdEnum.Active);
		try {
			account.setType_id(Account_Type.valueOf(accountType));
			return ok(views.html.accounts.accountForm.render(accountFormData.fill(account), new AccountDisplay(account.getEntity()),adx_based_exchanges,adx_ext)); 
		} catch (Exception e) {
			return badRequest("Invalid account type specified");
		}
	}

	@SecuredAction
	public static Result edit(   String accountId ) {
		Account  account = DataAPI.getAccountByGuid(accountId); 
		AccountEntity accountEntity = new AccountEntity();
		BeanUtils.copyProperties(account, accountEntity);
		accountEntity.setPassword("");
		if(account != null){  
			if(account.getType_id()== Account_Type.directadvertiser)
				return ok(views.html.accounts.accountForm.render(accountFormData.fill(accountEntity),  new AdvertiserDisplay(account),adx_based_exchanges,adx_ext)); 
			else
				return ok(views.html.accounts.accountForm.render(accountFormData.fill(accountEntity),  new PublisherDisplay(account),adx_based_exchanges,adx_ext));
		}
		else
			return ok("Invalid user id supplied");
	}

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	@SecuredAction
	public static Result accountList(){ 
		JsonNode queryParams = request().body().asJson();
		ObjectNode result = Json.newObject();
		
		try {
			EntityListFilter entityListFilter = objectMapper.treeToValue(queryParams, EntityListFilter.class);
			EntityList<Account> accountList = EntityListDataService.listData(entityListFilter, new Account()); 

			ArrayNode accounts = result.putArray("list");
			
			ObjectNode adnode = null;

			for (Account account : accountList.getEntityList()) {
				if(account.getType_id() == Account_Type.directadvertiser){
					adnode= objectMapper.valueToTree(new AdvertiserDisplay(account));
				}else{
					adnode= objectMapper.valueToTree(new PublisherDisplay(account));
				}			
				accounts.add(adnode);
			}		

			result.put("size", accountList.getCount()); 
			return ok(result);  
		} catch (Exception e) {
			Logger.debug("Error while fetchinng account list", e);
		} 
		return badRequest();
	}
 


	@SecuredAction
	public static Result accountListView(String accountType, Option<String> status) {
		StatusIdEnum accountStatus = StatusIdEnum.Active;
		String label = "";
		if(status.nonEmpty())
			accountStatus = StatusIdEnum.valueOf(status.get());
		if(StatusIdEnum.Active == accountStatus)
			label = "Active " + accountType;
		else if(StatusIdEnum.Pending == accountStatus)
			label = "Pending " + accountType;
		else
			label = "InActive " + accountType;
		return ok(views.html.accounts.accountListPage.render(accountType, label));
	}

	@SecuredAction
	public static Result save() {  
		Account account = new Account(); 
		AccountEntity accountEntity  = null;
		Form<AccountEntity> filledForm = accountFormData.bindFromRequest(); 
		if(filledForm.hasErrors()){
			return ok(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges,adx_ext)); 
		} 

		accountEntity = filledForm.get();
		account = accountEntity.getEntity();

		Http.Request request = play.mvc.Controller.request();

		if(account.getId() ==-1){ 
			Map<String, String[]> requestParams= request.body().asFormUrlEncoded();
			String password = requestParams.get("password")[0]; 
			if(password.length()<6){

			}
			String confirmPassword = requestParams.get("verifyPassword")[0];
			if(password.equals(confirmPassword))
				account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt()));
			else{
				filledForm.reject("password", "Password and Verify Password donot match");
				return badRequest(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges,adx_ext)); 
			}

		}else  {
			Account originalAccount = getAccount(account.getUserid());
			Map<String, String[]> requestParams= request.body().asFormUrlEncoded();
			String password = requestParams.get("password")[0];
			String encryptedPsswd = "";
			if(!"".equals(password)) {
				encryptedPsswd = BCrypt.hashpw(password, BCrypt.gensalt());
			} 
			String confirmPassword = requestParams.get("verifyPassword")[0]; 

			//||(!password.equals(originalAccount.getPassword()) && password.equals(confirmPassword))
			if("".equals(password) && "".equals(confirmPassword)){
				account.setPassword(originalAccount.getPassword());				
			}else if(! "".equals(password)&& password.equals(confirmPassword) ){
				account.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
			}else if(! "".equals(password)&& !password.equals(confirmPassword)  ){
				filledForm.reject("verifyPassword", "Password and Verify Password donot match");
				return badRequest(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges,adx_ext)); 
			} 
		}

		account.setModified_by(1);

		Connection dbConnection = null;

			try{
		        dbConnection = DB.getConnection(); 
				Message msg = null;  
				if(account.getId() ==-1){
					msg = ApiDef.verifyAccount(dbConnection, account);
					if( msg.getError_code()==0 ){
						msg = ApiDef.createAccount(dbConnection, account);
					}
				}else{
					msg = ApiDef.updateAccount(dbConnection, account);
				}
				if(msg.getError_code() ==0){ 
					return redirect(routes.AccountsController.accountListView(account.getType_id().getName(),Scala.Option(StatusIdEnum.Active.getName())));
				} else{ 
				    if(msg.getError_code() == ErrorEnum.EMAIL_ALREADY_PRESENT.getId()){
				        filledForm.reject("email", msg.getMsg());    
				    }else if(msg.getError_code() == ErrorEnum.USERID_ALREADY_TAKEN.getId()){
				        filledForm.reject("userid", msg.getMsg());
				    }else{
				        filledForm.reject("email", msg.getMsg());
				    }
					
					return badRequest(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges,adx_ext)); 
				} 
			}catch(Exception e){
				Logger.error("Error  while saving Account:"+ e.getMessage(),e);
			}
			finally{
				try {
					if(dbConnection != null)
						dbConnection.close();
				} catch (SQLException e) {
                     Logger.error(e.getMessage(),e);
				}
			}
		return badRequest(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges,adx_ext)); 
	}

	@SecuredAction
	public static Result updateAccountStatus(String accountGuid, String action  ) {   
		AccountWorkflowEntity awe = new AccountWorkflowEntity(accountGuid, action); 
		return ok(views.html.accounts.accountWorkflowForm.render(accountWorkflowFormData.fill(awe), action));
	}

	@SecuredAction
	public static Result saveAccountStatus() { 
		Form<AccountWorkflowEntity> accountWfForm = accountWorkflowFormData.bindFromRequest();
		Connection con = null;
		Message statusMsg = null;

		ObjectNode response = Json.newObject();
		if(!accountWfForm.hasErrors()){
			try { 
				con = DB.getConnection();
				AccountWorkflowEntity accountWfEntity = accountWfForm.get();
				Account account = DataAPI.getAccountByGuid(accountWfEntity.getAccountGuid()); 

				account.setModified_by(1);
				if(Actions.Activate.name().equalsIgnoreCase(accountWfEntity.getAction())){
					account.setStatus(StatusIdEnum.Active);				  
				}else if(Actions.Deactivate.name().equalsIgnoreCase(accountWfEntity.getAction())){
					account.setComment(accountWfEntity.getComment());
					account.setStatus(StatusIdEnum.Rejected);	
				}

				statusMsg = ApiDef.updateAccountStatus(con, account);

				if(statusMsg.getError_code()==0){
					response.put("message", "Update Successful");
					return ok(response);
				}else{
					response.put("message", "Update Failed. Please retry");
					return badRequest(response);
				}
			} catch (Exception e) {
				Logger.error("Error in updating accout status", e);
			}
			finally{
				try {
					if(con != null)
						con.close();
				} catch (SQLException e) {
					Logger.error("Error in closing DB Connection", e);
				}
			}
		} 

		response.put("message", "Update Failed. Please retry");
		return badRequest(response);
	}
    @SecuredAction
    public static Result uploadQualificationImage(){
        return uploadQualificationImage(false, null,null);
    }
	public static Result uploadQualificationImage(boolean isApicall, String targeting_type, String account_guid){ 
		MultipartFormData multipartFormData = request().body().asMultipartFormData(); 
 
				
		String targetingType = null; 
		if(isApicall){
		    targetingType = targeting_type;
		}else{
		      targetingType = multipartFormData.asFormUrlEncoded().get("targeting_type")[0]; 
		}
		 
		
		FilePart geoFile = multipartFormData.getFile("file"); 
		ObjectNode  response = Json.newObject();	
		if (geoFile != null) { 
			String fileName = geoFile.getFilename();
			String extension = FilenameUtils.getExtension(fileName);
			if(!("jpg".equalsIgnoreCase(extension)|| "jpeg".equalsIgnoreCase(extension)|| "png".equalsIgnoreCase(extension))){
			    if(isApicall){
			        FileUploadResponse fur = new FileUploadResponse();
                    fur.setErrorCode(ErrorEnum.FILE_EXTENSION_NOT_ALLOWED.getId());
                    fur.setMessage(ErrorEnum.FILE_EXTENSION_NOT_ALLOWED.getName());
                    return ok(fur.toJson().toString());
			    }
			    response.put("message", "Incorrect File " +fileName+ " of Type "+extension+" being uploaded. Allowed format is jpg|jpeg|png");
			    return badRequest(response);
			}
			File file = geoFile.getFile();
			String destFilePath = generateFileUrl(targetingType, fileName);
			File outputFile = new File("public/"+destFilePath).getAbsoluteFile();
			outputFile.getParentFile().mkdirs();
			FileInputStream fis = null;
			try {
				if(!outputFile.exists()){
					outputFile.createNewFile();
				}

				Files.copy(file, outputFile);
				String uploadtoCDNFlag = Play.application().configuration().getString("cdn_upload");
				if("evrest".equals(uploadtoCDNFlag)){
				    boolean cdn_upload_success = postToEvrest(outputFile, outputFile.getName());
				    if(!cdn_upload_success){
				        response.put("message", "CDN upload failed");
				        return badRequest(response);
				    }
				}else if("s3".equals(uploadtoCDNFlag)){
                        boolean s3_upload_success = postToS3(outputFile);
                        if(!s3_upload_success){
                            response.put("message", "S3 upload failed");
                            return badRequest(response);
                        }
				}else if("edgecast".equals(uploadtoCDNFlag)){
                    boolean upload_success = postToEdgeCast(outputFile);
                    if(!upload_success){
                        response.put("message", "Edgecast upload failed");
                        return badRequest(response);
                    }
				}else if("ucloud".equals(uploadtoCDNFlag)){
                    boolean upload_success = postToUCloud(outputFile);
                    if(!upload_success){
                        response.put("message", "Ucloud upload failed");
                        return badRequest(response);
                    }
                }
				fis = new FileInputStream(outputFile);
				String md5 = getMD5(fis);
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
				response.put("md5", md5);
				response.put("preview_url", controllers.routes.StaticFileController.download(Scala.Option(destFilePath)).url()); 
				return ok(response);

			} catch (Exception e) {
			    Logger.error("Error in reading uploaded file property. "+ e.getMessage(),e);
			    if(isApicall){
			        FileUploadResponse fur = new FileUploadResponse();
                    fur.setErrorCode(ErrorEnum.FILE_READ_ERROR.getId());
                    fur.setMessage(ErrorEnum.FILE_READ_ERROR.getName());
                    return ok(fur.toJson().toString());
                }
			}finally{
				if(fis!=null){
					try {
						fis.close();
					} catch (IOException e) {
						Logger.error(e.getMessage(),e);
					}
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
	
	private static String generateFileUrl(String targetingType, String filename){ 
		String ext = "";
		if(filename.indexOf('.')!= -1){
			String splits[] = filename.split("\\.");
			if(splits.length == 2)
				ext = splits[1];
		}
		String uuidname = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
		String basePath = "/targeting/geo/"+targetingType+"/"+uuidname+"."+ext;	  
		return basePath; 
	}
	public static String getMD5(FileInputStream f) throws Exception {

		MessageDigest md = MessageDigest.getInstance("MD5");
		String digest = getDigest(f, md, 2048);

		return digest;

	}
	public static String getDigest(InputStream is, MessageDigest md, int byteArraySize)
			throws Exception {

		md.reset();
		byte[] bytes = new byte[byteArraySize];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			md.update(bytes, 0, numBytes);
		}
		byte[] digest = md.digest();
		String result = new String(Hex.encodeHex(digest));
		return result;
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



}
