package controllers;

import play.*;
import play.libs.F;
import play.mvc.*;
import play.mvc.Http.RequestHeader;
import static play.mvc.Results.*;

public class Global extends GlobalSettings {

  @Override
  public F.Promise<SimpleResult> onHandlerNotFound(RequestHeader request) { 
	  redirect(routes.Application.index());
	  return null;
  }  
    
}