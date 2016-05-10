package controllers.iddefinition;

import models.iddefinition.IddefinitionFormEntity;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;

public class IddefinitionController extends Controller{

	private static Form<IddefinitionFormEntity> iddefinitionConfigForm = Form.form(IddefinitionFormEntity.class);
	
	@SecuredAction
	public static Result iddefinition(){
	    IddefinitionFormEntity rfe = new IddefinitionFormEntity();
		return ok(views.html.iddefinition.iddefinition.render(iddefinitionConfigForm.fill(rfe)));
	}
    @SecuredAction
    public static Result getiddefinition(){
        Form<IddefinitionFormEntity> iddefinitionFormEntityData =  iddefinitionConfigForm.bindFromRequest();
        return ok(views.html.iddefinition.iddefinition.render(iddefinitionFormEntityData));
    }

	
}
