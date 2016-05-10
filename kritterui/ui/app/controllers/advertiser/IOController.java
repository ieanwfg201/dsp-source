package controllers.advertiser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import models.StaticUtils;
import models.advertiser.IoDisplay;
import models.entities.InsertionOrderEntity;
import models.formbinders.IOWorkFlowEntity;
import play.Logger;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.OperationsDataService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.insertion_order.IOListEntity;
import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.api.entity.insertion_order.Insertion_Order_List;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.IOStatus;
import com.kritter.constants.PageConstants;
import com.kritter.kritterui.api.def.ApiDef;

import controllers.routes;


public class IOController extends Controller{

	static Form<InsertionOrderEntity> ioForm = Form.form(InsertionOrderEntity.class);
	static Form<IOWorkFlowEntity> ioWorkFlowForm = Form.form(IOWorkFlowEntity.class);


	public enum IO_ACTIONS {approve, reject, pause}

	@SecuredAction
	public static Result create() {
		return ok(views.html.advt.insertionOrderForm.render(ioForm));
	}

	@SecuredAction
	public static Result editForm(String ioId, String guid,  String action) {
		IOWorkFlowEntity ioWfEntity = new IOWorkFlowEntity(guid, ioId, action); 
		return ok(views.html.advt.insertionOrderEditForm.render(ioWorkFlowForm.fill(ioWfEntity), action));
	}

	@SecuredAction
	public static Result updateIOStatus(){

		Form<IOWorkFlowEntity> ioWfEntityForm = ioWorkFlowForm.bindFromRequest();
		Connection con = null;
		Message statusMsg = null;

		ObjectNode response = Json.newObject();
		if(!ioWfEntityForm.hasErrors()){
			try {
				con = DB.getConnection();
				IOWorkFlowEntity ioWfEntity = ioWfEntityForm.get();
				Insertion_Order io = new Insertion_Order();
				io.setAccount_guid(ioWfEntity.getAccountGuid());
				io.setOrder_number(ioWfEntity.getIoNumber());
				io.setModified_by(1);
				if(IO_ACTIONS.valueOf(ioWfEntity.getAction())==IO_ACTIONS.approve){
					io.setStatus(IOStatus.Approved);
					statusMsg = ApiDef.approve_io(con, io);				 
				}else if(IO_ACTIONS.valueOf(ioWfEntity.getAction())==IO_ACTIONS.reject){
					io.setStatus(IOStatus.REJECTED);
					io.setComment(ioWfEntity.getComment());
					io.setModified_by(1);
					statusMsg = ApiDef.reject_io(con, io); 
				}
				if(statusMsg.getError_code()==0){
					response.put("message", "Update Successful");
					return ok(response);
				}else{
					response.put("message", "Update Failed. Please retry");
					return badRequest(response);
				}
			} catch (Exception e) {
				Logger.error("Exception while updating IO status", e); 
			}
			finally{
				try{
					if(con!=null){ 
						con.close();
					}
				}catch(Exception e){
					Logger.error("Exception while closing DB connection", e); 
				}
			}
		}

		response.put("message", "Update Failed. Please retry");
		return badRequest(response);
	}

	@SecuredAction
	public static Result list(String status ) { 
		Connection con = null;
		
		try {
			con = DB.getConnection();
			IOListEntity ioListEntity = new IOListEntity();
			ioListEntity.setPage_no(PageConstants.start_index);
			ioListEntity.setStatus(IOStatus.valueOf(status));
			ioListEntity.setPage_size(PageConstants.page_size);

			Insertion_Order_List ioListStatus = ApiDef.list_io_by_status(con, ioListEntity);
			List<Insertion_Order> ioList = ioListStatus.getInsertion_order_list();
			return ok(views.html.advt.insertionOrderList.render(status, ioList)); 
		} catch (Exception e) {
			Logger.error("Exception while fetching IO List", e); 
		}
		finally{
			try{
				if(con!=null){ 
					con.close();
				}
			}catch(Exception e){
				Logger.error("Exception while closing DB connection", e); 
			}
		}
		return badRequest("An Exception has Occiured");		
	}


	@SecuredAction
	public static Result iosByAccount( String guid, String status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Insertion_Order> ioList = OperationsDataService.ioByAccount(guid, status, pageNo,pageSize );
		ObjectNode result = Json.newObject();

		ArrayNode cnodes = result.putArray("list");
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode snode = null;
		for (Insertion_Order io : ioList) { 
			snode= objectMapper.valueToTree(new IoDisplay(io)); 
			cnodes.addPOJO(snode);
		}
		result.put("size", ioList.size());  

		return ok(result); 
	}


	@SecuredAction
	public static Result save() {  
		Form<InsertionOrderEntity> filledForm = ioForm.bindFromRequest();
		Connection con = null;
		try{
			if(!filledForm.hasErrors()){
				InsertionOrderEntity io = ioForm.bindFromRequest().get();
				io.setModified_by(1); 

				con = DB.getConnection();  
				Message msg = null;   
				msg = ApiDef.check_io(con, io.getEntity());
				if( msg.getError_code()==0 ){
				    Insertion_Order entity = io.getEntity();
				    entity.setCreated_by(StaticUtils.loggedinUserId());
					msg = ApiDef.insert_io(con, entity);

					if(msg.getError_code() ==0){ 
						return redirect(routes.OperationsController.ioApprovalQueue());
					} 
				}
			}
		}catch(Exception e){
			Logger.error("Error  while saving Account:"+ e.getMessage(),e);
		}
		finally{
			try {
				if(con != null)
					con.close();

			} catch (SQLException e) {

			}
		}
		return badRequest(views.html.advt.insertionOrderForm.render(filledForm)); 
	}
}
