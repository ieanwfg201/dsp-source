package controllers.reporting;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.reporting.*;

public class CustomReportController extends Controller{

	public static Result index(){
		return ok(customReport.render());
	}
	public static Result demand(){
		return ok(customReport.render());
	}
	public static Result supply(){
		return ok(customReport.render());
	}
	public static Result network(){
		return ok(customReport.render());
	}


}
