package controllers.adxbasedexchanges;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import models.entities.QualificationEntity;
import models.entities.QualificationListFormEntity;
import models.publisher.SiteDisplayFull;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.Play;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Files;
import com.kritter.api.entity.qualification.QualificationList;
import com.kritter.api.entity.qualification.QualificationListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.targeting_profile.FileUploadResponse;
import com.kritter.api.upload_to_cdn.IUploadToCDN;
import com.kritter.api.upload_to_cdn.everest.UploadToCDN;
import com.kritter.constants.QualificationDefEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.account.Qualification;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utild.ucloud_upload.upload.UploadToUCloud;
import com.kritter.utils.amazon_s3_upload.UploadToS3;
import com.kritter.utils.edgecast_upload.UploadToEdgecast;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class QualificationController extends Controller{

	static Form<QualificationEntity> qualFormTemplate = Form.form(QualificationEntity.class);
	static Form<QualificationListFormEntity> qualListFormTemplate1 = Form.form(QualificationListFormEntity.class);

	@SecuredAction
	public static Result add(){ 
		QualificationEntity qual = new QualificationEntity(); 
		return ok(views.html.adxbasedexchanges.qualification.render(qualFormTemplate.fill(qual)));
	}
	@SecuredAction
	public static Result list(Option<String> adv){ 
        Form<QualificationListFormEntity> qualificationFormEntityData = Form.form(QualificationListFormEntity.class);
        QualificationListFormEntity qEntity  = new QualificationListFormEntity();

        if(!adv.isEmpty() && !adv.get().isEmpty()){
        	qEntity.setAdvIncId(Integer.parseInt(adv.get()));
        }

		return ok(views.html.adxbasedexchanges.listqualification.render(qualificationFormEntityData.fill(qEntity)));
	}
	@SecuredAction
	public static Result changeAdvertiser(){ 
		Form<QualificationListFormEntity> qualForm = qualListFormTemplate1.bindFromRequest();
		QualificationListFormEntity qualEntity = null;
		qualEntity = qualForm.get();

		return redirect(routes.QualificationController.list(Scala.Option(qualEntity.getAdvIncId()+"")));
	}

	@SecuredAction
	public static Result edit( int id){
		QualificationListEntity alEntity = new QualificationListEntity();
		alEntity.setId_list(id+"");
		alEntity.setQueryEnum(QualificationDefEnum.select_qualification_byinternalids);
		Connection con = null;
		try {
			con = DB.getConnection();
			QualificationList alList = ApiDef.various_get_qualification(con, alEntity);
			if(alList != null){
				if(alList.getEntity_list() != null && alList.getEntity_list().size()>0){
					QualificationEntity aEntity =  new QualificationEntity();
					BeanUtils.copyProperties(alList.getEntity_list().get(0), aEntity);
					return ok(views.html.adxbasedexchanges.qualification.render(qualFormTemplate.fill(aEntity)));
				}
			}

		} catch (Exception e) {
			Logger.error("Error while updating qualification", e );
		} finally{
			try{
				if(con!=null){
					con.close();
				}
			}catch(Exception e){
				Logger.error("Error while closing DB connection", e );
			}
		}
        Form<QualificationListFormEntity> qualificationFormEntityData = Form.form(QualificationListFormEntity.class);
        QualificationListFormEntity qEntity  = new QualificationListFormEntity();
		return badRequest(views.html.adxbasedexchanges.listqualification.render(qualificationFormEntityData.fill(qEntity)));
	}



	@SecuredAction
	public static Result save(){
		Form<QualificationEntity> qualForm = qualFormTemplate.bindFromRequest();
		QualificationEntity qualEntity = null;
		Qualification qual =null;
		if(!"cancel".equals(qualForm.data().get("action"))){
		if(!qualForm.hasErrors()){
			qualEntity = qualForm.get();
			Message msg = null; 
			Connection con = null;
			try {
				con = DB.getConnection();
				qual = qualEntity.getEntity();
				QualificationListEntity qE = new QualificationListEntity();
				qE.setName(qual.getQname());
				qE.setId_list(qual.getAdvIncId()+"");
				qE.setQueryEnum(QualificationDefEnum.select_by_name_adv);
				QualificationList qList =ApiDef.various_get_qualification(con, qE);
				if(qList==null || qList.getEntity_list()==null || qList.getEntity_list().size()<1){
					if(qual.getInternalid()>0){
						msg = ApiDef.update_qualification(con, qual);
					}else{
						msg = ApiDef.insert_qualification(con, qual);
					}

					if(msg.getError_code()==0){ 
						//Form<QualificationListFormEntity> qualificationFormEntityData = Form.form(QualificationListFormEntity.class);
						//QualificationListFormEntity qEntity  = new QualificationListFormEntity();
						//qEntity.setAdvIncId(qual.getAdvIncId());
						//; 
						return redirect(routes.QualificationController.list(Scala.Option(qual.getAdvIncId()+"")));
						//return ok(views.html.adxbasedexchanges.listqualification.render(qualificationFormEntityData.fill(qEntity)));
					}
				}else{
					qualForm.reject("qname", "Duplicate Name");
				}

			} catch (Exception e) {
				Logger.error("Error while updating qual", e );
			} finally{
				try{
					if(con!=null){
						con.close();
					}
				}catch(Exception e){
					Logger.error("Error while closing DB connection", e );
				}
			}
		}
		}else{
			QualificationEntity qual1 = new QualificationEntity(); 
			return ok(views.html.adxbasedexchanges.qualification.render(qualFormTemplate.fill(qual1)));

		}
		return badRequest(views.html.adxbasedexchanges.qualification.render( qualForm ));
	}
	
	
	@SecuredAction
	public static Result updatemultiple(String action, Option<String> ids){
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
	            if("DELETE".equals(action)){
	            	QualificationListEntity aListEntity = new QualificationListEntity();
	                aListEntity.setId_list(idfinal);
	                aListEntity.setQueryEnum(QualificationDefEnum.delete_multiple_qualification);
	                ApiDef.delete_multiple_qualification(con, aListEntity);
	                response.put("message", "start dne");return ok(response);
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
	@SecuredAction
	public static Result updatesingle(int id,String action){
	    ObjectNode response = Json.newObject();
	    Connection con = null;
	    try{
	        if(action != null){
	            con = DB.getConnection();
	            if("DELETE".equals(action)){
	            	QualificationListEntity aListEntity = new QualificationListEntity();
	                aListEntity.setId_list(id+"");
	                aListEntity.setQueryEnum(QualificationDefEnum.delete_multiple_qualification);
	                ApiDef.delete_multiple_qualification(con, aListEntity);
	                response.put("message", "start dne");return ok(response);
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
		String basePath = "/banners/original/"+targetingType+"/"+uuidname+"."+ext;	  
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
