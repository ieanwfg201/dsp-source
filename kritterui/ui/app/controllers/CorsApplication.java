package controllers;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SecureSocial.SecuredAction;
import models.entities.reporting.ReportFormEntity;
import play.data.Form;

public class CorsApplication extends Controller {
	   public static Result preflight(String all) {
	        response().setHeader("Access-Control-Allow-Origin", "*");
	        response().setHeader("Allow", "*");
	        response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
	        response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent");
	        return ok();
	    }

}
