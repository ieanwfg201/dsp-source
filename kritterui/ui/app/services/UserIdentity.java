package services;

import models.AccountIdentity;
import scala.Option;
import securesocial.core.AuthenticationMethod;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.OAuth1Info;
import securesocial.core.OAuth2Info;
import securesocial.core.PasswordInfo;

public class UserIdentity implements Identity{

	private AccountIdentity user;
	
	public UserIdentity(AccountIdentity user){
		this.user = user;
	}
	@Override
	public AuthenticationMethod authMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option<String> avatarUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option<String> email() {
		// TODO Auto-generated method stub
		return  null;
	}

	@Override
	public String firstName() {
		// TODO Auto-generated method stub
		return "Priyabrata";
	}

	@Override
	public String fullName() { 
		return "Priyabrata Kuanr";
	}

	@Override
	public IdentityId identityId() { 
		return null;
	}

	@Override
	public String lastName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option<OAuth1Info> oAuth1Info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option<OAuth2Info> oAuth2Info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option<PasswordInfo> passwordInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
