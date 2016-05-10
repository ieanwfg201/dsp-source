package services;

import java.sql.Connection;

import models.AccountIdentity;

import org.mindrot.jbcrypt.BCrypt;

import play.Application;
import play.Logger;
import play.cache.Cache;
import play.db.DB;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Account_Type;
import com.kritter.kritterui.api.def.ApiDef;

public class UserService extends BaseUserService {


	public UserService( Application application) {
		super(application); 
	}

	/**
	 * Saves the user.  This method gets called when a user logs in.
	 * This is your chance to save the user information in your backing store.
	 * @param user
	 * @return 
	 */
	@Override
	public Identity doSave(Identity user) {

		return user;
	}

	/**
	 * Finds an Identity in the backing store.	     
	 * @return an Identity instance or null if no user matches the specified id
	 */
	@Override
	public Identity doFind(IdentityId id) {
		Connection con = null;
		Account account = new Account();
		account.setUserid(id.userId());
		try{
		    con = DB.getConnection();
			AccountMsgPair acctMsgPair = ApiDef.get_Account(con, account); 
			acctMsgPair.getMsg();
			account = acctMsgPair.getAccount();
			Message msg = acctMsgPair.getMsg();
			if(msg.getError_code()==0){
				if(account.getType_id()== Account_Type.root)
					return new AccountIdentity(account);
				else
					return null;
			}else{
				return null;
			}
			
		}catch(Exception e){
			Logger.error("DB Connection error while retrieving user id ",e);
		}

		finally{
			try{
                if(con != null){
				    con.close();
                }
			}catch(Exception e){
				
			}
		}
		return null;  		
		
	}

	/**
	 * Finds an identity by email and provider id.
	 *
	 * Note: If you do not plan to use the UsernamePassword provider just provide en empty
	 * implementation.
	 *
	 * @param email - the user email
	 * @param providerId - the provider id
	 * @return an Identity instance or null if no user matches the specified id
	 */
	@Override
	public Identity doFindByEmailAndProvider(String email, String providerId) {
		Account account = new Account();
		account.setUserid("user");
		account.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));  		
		return new AccountIdentity(account);
	}

	/**
	 * Finds a token by id
	 *
	 * Note: If you do not plan to use the UsernamePassword provider just provide en empty
	 * implementation
	 *
	 * @return a Token instance or null if no token matches the id
	 */
	@Override
	public Token doFindToken(String tokenId) {
		Token token = null;
		if(Cache.get(tokenId)!=null)
			token = (Token)Cache.get(tokenId);
		return token;
	}


	/**
	 * Deletes a token
	 *
	 * Note: If you do not plan to use the UsernamePassword provider just provide en empty
	 * implementation
	 *
	 * @param uuid the token id
	 */
	@Override
	public void doDeleteToken(String uuid) {
		System.out.println("delete token"+ uuid);
	}

	/**
	 * Deletes all expired tokens
	 *
	 * Note: If you do not plan to use the UsernamePassword provider just provide en empty
	 * implementation
	 *
	 */
	@Override
	public void doDeleteExpiredTokens() {
		System.out.println("delete all expired token");
	}

	@Override
	public void doSave(Token arg0) {
		Cache.set(arg0.getUuid(), arg0);
		System.out.println("saving token"+ arg0.getUuid());
	}
}
