package models;

import org.mindrot.jbcrypt.BCrypt;

import play.libs.Scala;
import scala.Option;
import securesocial.core.AuthenticationMethod;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.OAuth1Info;
import securesocial.core.OAuth2Info;
import securesocial.core.PasswordInfo;
import securesocial.core.providers.utils.PasswordHasher;

import com.kritter.api.entity.account.Account;



public class AccountIdentity  implements Identity{
 
	private Account account =  null;
	
	public AccountIdentity(Account account) {
		this.account = account; 
	}

	public int getId(){
		return account.getId();
	}
	
	@Override
	public AuthenticationMethod authMethod() { 
		return AuthenticationMethod.UserPassword();
	}

	@Override
	public Option<String> avatarUrl() { 
		return null;
	}


	@Override
	public Option<String> email() { 
		return Scala.Option(account.getEmail());
	}


	@Override
	public String firstName() { 
		return account.getName();
	}


	@Override
	public String fullName() { 
		return account.getName();
	}


	public IdentityId identityId() { 
		return new IdentityId(account.getUserid(), "userpass");
	}


	public String lastName() { 
		return "";
	}


	public Option<OAuth1Info> oAuth1Info() { 
		return null;
	}

	public Option<OAuth2Info> oAuth2Info() { 
		return null;
	}


	public Option<PasswordInfo> passwordInfo() { 
		
		Option<PasswordInfo> info = Scala.Option(new PasswordInfo( PasswordHasher.BCryptHasher(), 
								account.getPassword(),
								Scala.Option(BCrypt.gensalt())));
		return info;
	}


	public AccountIdentity   deepCopy() { 
		AccountIdentity copy = null;
		try{
			copy =  new AccountIdentity(account);
		}catch(Exception e){

		}
		return copy;
	}



}
