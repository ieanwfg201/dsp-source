package controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import models.EntityList;
import models.EntityListFilter;
import models.Constants.Actions;
import models.accounts.displays.AccountDisplay;
import models.accounts.displays.AdvertiserDisplay;
import models.accounts.displays.PublisherDisplay;
import models.entities.AccountEntity;
import models.formbinders.AccountWorkflowEntity;

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
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import services.EntityListDataService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Account_Type;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.def.ApiDef;


public class AccountsController extends Controller{

	static Form<AccountEntity> accountFormData = Form.form(AccountEntity.class);
	static Form<AccountWorkflowEntity> accountWorkflowFormData = Form.form(AccountWorkflowEntity.class);
	private static String adx_based_exchanges = Play.application().configuration().getString("adx_based_exchanges");

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
			return ok(views.html.accounts.accountForm.render(accountFormData.fill(account), new AccountDisplay(account.getEntity()),adx_based_exchanges)); 
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
				return ok(views.html.accounts.accountForm.render(accountFormData.fill(accountEntity),  new AdvertiserDisplay(account),adx_based_exchanges)); 
			else
				return ok(views.html.accounts.accountForm.render(accountFormData.fill(accountEntity),  new PublisherDisplay(account),adx_based_exchanges));
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
			return ok(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges)); 
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
				return badRequest(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges)); 
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
				return badRequest(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges)); 
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
					
					return badRequest(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges)); 
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
		return badRequest(views.html.accounts.accountForm.render(filledForm, new AccountDisplay(account),adx_based_exchanges)); 
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


}
