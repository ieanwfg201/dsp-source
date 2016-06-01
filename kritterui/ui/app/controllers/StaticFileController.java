package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.io.FilenameUtils;

import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;

public class StaticFileController extends Controller{

	public static Result serveImage(String filePath)
	{
		response().setContentType("image");    
		ByteArrayOutputStream img_stream = null;
		try { 
			File file =  new File( "public/"+filePath).getAbsoluteFile();
			BufferedImage thumbnail = ImageIO.read(new MemoryCacheImageInputStream(new FileInputStream(file)));
			img_stream = new ByteArrayOutputStream();
			String ext = FilenameUtils.getExtension(filePath);
			ImageIO.write(thumbnail, ext, img_stream);
		} catch (FileNotFoundException e) {
			return badRequest("image not found");
		} catch (IOException e) {
			return badRequest("image not found");
		}
		return ok(img_stream.toByteArray()); 
	}   

	public static Result download(Option<String> path){
		if(path.nonEmpty()){
			File file = new File("public/"+ path.get()).getAbsoluteFile(); 
			response().setContentType("application/x-download");  
			response().setHeader("Content-disposition","attachment; filename="+file.getName()); 
			return ok(file);
		}else
			return ok("File Missing");    
	}
	
    public static Result getapplicationlog(){
            File file = new File("public/../logs/application.log").getAbsoluteFile(); 
            response().setContentType("application/x-download");  
            response().setHeader("Content-disposition","attachment; filename="+file.getName()); 
            return ok(file);
    }

    public static Result loadTemplate(String template){
		Class<?> clazz;
		try {
			clazz = Class.forName("views.html." +  template);
			java.lang.reflect.Method render = clazz.getDeclaredMethod("render");
			play.api.templates.Html  html = (play.api.templates.Html) render.invoke(null);
		 
			return ok(html);
		} catch (ClassNotFoundException e) {
			return badRequest("Template not found");
		} catch (Exception e) {
			return badRequest("Unexpected error has occured");
		}  
		
    }
}
