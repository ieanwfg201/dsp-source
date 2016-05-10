package controllers;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SecureSocial.SecuredAction;
import models.entities.reporting.ReportFormEntity;
import play.data.Form;

public class Application extends Controller {
    private static Form<ReportFormEntity> reportConfigForm = Form.form(ReportFormEntity.class); 
    private static String show_carrier_ui = Play.application().configuration().getString("show_carrier_ui");
	@SecuredAction
    public static Result index() {
	    ReportFormEntity rfe = new ReportFormEntity();        
        return ok(views.html.dashboard.render(reportConfigForm.fill(rfe),show_carrier_ui));	
        //return ok(views.html.dashboard.render());
    }
    
	
	public static String userName(){
		String userName = SecureSocial.currentUser().fullName();
		return userName;
	}
    
    public static Result dashboard() {
        return ok("Dashboard");
    }
    
    public static Result algoCenter() {
        return ok( "Dashboard");
    }
    public static Result testEditableTable() {
        return ok(views.html.testEditableTable.render());
    }
    public static Result editRow() {
        return ok(views.html.editRow.render());
    }


}
