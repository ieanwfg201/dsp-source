package controllers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.entities.cacheinfo.CacheInfoFormEntity;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;


public class CacheInfoController extends Controller{

    static Form<CacheInfoFormEntity> cacheInfoFormEntity = Form.form(CacheInfoFormEntity.class);
    
    @SecuredAction
    public static Result viewcacheinfo() {
        return ok(views.html.cacheinfo.cacheinfo.render(cacheInfoFormEntity.fill(new CacheInfoFormEntity())));
    }



    private static String cacheInfoRequst(CacheInfoFormEntity tpe){
        URL url;
        StringBuffer sbuff = new StringBuffer("<pre>");
        BufferedReader br = null;
        try {
        	String a = tpe.getUrl()+"/impag/jsp/CacheInfo.jsp?cache="+tpe.getCache_name();
            url = new URL(a);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");

            // open the stream and put it into BufferedReader
            br= new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sbuff.append(inputLine);
                sbuff.append("\n");
            }
            sbuff.append("</pre>");
            conn.disconnect();
            return sbuff.toString();
        } catch (Exception e) {
            Logger.error(e.getMessage(),e);
            return "<pre>Nothing found</pre>";
        } finally{
            if(br!= null){
                try {
                    br.close();
                } catch (IOException e) {
                    Logger.error(e.getMessage(),e);
                }
            }
        }
    }
    @SecuredAction
    public static Result exchangeresponsedebug() {
        Form<CacheInfoFormEntity> filledForm = cacheInfoFormEntity.bindFromRequest();
        ObjectNode result = Json.newObject();
        CacheInfoFormEntity tpe = filledForm.get();
        if("".equals(tpe.getUrl())){
            result.put("data", "Adserving domain name or IP Empty");
            return ok(result);
        }else if("".equals(tpe.getCache_name())){
            result.put("data", "Cache Name Empty");
            return ok(result);
        }

        result.put("data", cacheInfoRequst(tpe));
        return ok(result);
    }
}
