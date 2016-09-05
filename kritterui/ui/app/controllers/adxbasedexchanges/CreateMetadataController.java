package controllers.adxbasedexchanges;

import java.sql.Connection;

import models.entities.adxbasedexchanges.AdxBasedExchangesMetadataEntity;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.data.Form;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;
import com.kritter.api.entity.adxbasedexchangesmetadata.AdxBasedExchangesMetadatList;
import com.kritter.api.entity.adxbasedexchangesmetadata.AdxBasedExchangesMetadataListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.AdxBasedExchangesMetadataQueryEnum;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;
import com.kritter.kritterui.api.def.ApiDef;

public class CreateMetadataController extends Controller{

	static Form<AdxBasedExchangesMetadataEntity> adxbasedExchangesMetadataFormTemplate = Form.form(AdxBasedExchangesMetadataEntity.class); 

	@SecuredAction
	public static Result add(){ 
		AdxBasedExchangesMetadataEntity adxbasedExchangesMetadata = new AdxBasedExchangesMetadataEntity(); 
		return ok(views.html.adxbasedexchanges.adxbasedexchangescreatemeta.render(adxbasedExchangesMetadataFormTemplate.fill(adxbasedExchangesMetadata)));
	}
	@SecuredAction
	public static Result list(){ 
		return ok(views.html.adxbasedexchanges.listadxbasedexchangescreatemeta.render());
	}

	@SecuredAction
	public static Result edit( int id){
		AdxBasedExchangesMetadataListEntity alEntity = new AdxBasedExchangesMetadataListEntity();
		alEntity.setId_list(id+"");
		alEntity.setQueryEnum(AdxBasedExchangesMetadataQueryEnum.get_by_internalids);
		Connection con = null;
		try {
			con = DB.getConnection();
			AdxBasedExchangesMetadatList alList = ApiDef.various_get_adbasedexchanges_metadata(con, alEntity);
			if(alList != null){
				if(alList.getEntity_list() != null && alList.getEntity_list().size()>0){
					AdxBasedExchangesMetadataEntity aEntity =  new AdxBasedExchangesMetadataEntity();
					BeanUtils.copyProperties(alList.getEntity_list().get(0), aEntity);
					return ok(views.html.adxbasedexchanges.adxbasedexchangescreatemeta.render(adxbasedExchangesMetadataFormTemplate.fill(aEntity)));
				}
			}

		} catch (Exception e) {
			Logger.error("Error while updating adx based metadata", e );
		} finally{
			try{
				if(con!=null){
					con.close();
				}
			}catch(Exception e){
				Logger.error("Error while closing DB connection", e );
			}
		}
		return badRequest(views.html.adxbasedexchanges.listadxbasedexchangescreatemeta.render());
	}



	@SecuredAction
	public static Result save(){
		Form<AdxBasedExchangesMetadataEntity> adxBasedForm = adxbasedExchangesMetadataFormTemplate.bindFromRequest();
		AdxBasedExchangesMetadataEntity adxBased = null;
		AdxBasedExchangesMetadata adxBasedMetadata =null;
		if(!adxBasedForm.hasErrors()){
			adxBased = adxBasedForm.get();
			Message msg = null; 
			Connection con = null;
			try {
				con = DB.getConnection();
				adxBasedMetadata = adxBased.getEntity();
				if(adxBasedMetadata.getInternalid()>0){
					msg = ApiDef.update_adbasedexchanges_metadata(con, adxBasedMetadata);
				}else{
					msg = ApiDef.insert_adbasedexchanges_metadata(con, adxBasedMetadata);
				}

				if(msg.getError_code()==0){ 
					return ok(views.html.adxbasedexchanges.listadxbasedexchangescreatemeta.render());
				}

			} catch (Exception e) {
				Logger.error("Error while updating adx based metadata", e );
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
		return badRequest(views.html.adxbasedexchanges.adxbasedexchangescreatemeta.render( adxBasedForm ));
	}
}
