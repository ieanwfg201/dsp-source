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

import models.entities.testpages.TestPagesFormEntity;

import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;


public class TestPagesController extends Controller{

    static Form<TestPagesFormEntity> testPagesFormEntity = Form.form(TestPagesFormEntity.class);
    private static String debug_url = Play.application().configuration().getString("debug_url");

    @SecuredAction
    public static Result viewtestsite() {
        return ok(views.html.testpages.testsite.render(testPagesFormEntity.fill(new TestPagesFormEntity()),debug_url));
    }

    @SecuredAction
    public static Result viewtestclick() {
        return ok(views.html.testpages.testclick.render(testPagesFormEntity.fill(new TestPagesFormEntity())));
    }

    @SecuredAction
    public static Result viewtestexchange() {
        return ok(views.html.testpages.testexchange.render(testPagesFormEntity.fill(new TestPagesFormEntity())));
    }


    private static String debugS2SRequest(TestPagesFormEntity tpe){
        URL url;
        StringBuffer sbuff = new StringBuffer("<pre>");
        BufferedReader br = null;
        try {
            String a=debug_url+"?ua="+URLEncoder.encode(tpe.getUa(),"UTF-8")+"&site-id="+
                    URLEncoder.encode(tpe.getSite_guid(),"UTF-8")+"&ip="+
                    URLEncoder.encode(tpe.getIp(),"UTF-8")+"&fmt=xml&ver=s2s_1";
            url = new URL(a);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("KRITTER-DEBUG-SYSTEM", "true");
            // open the stream and put it into BufferedReader
            br= new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sbuff.append(inputLine);
                sbuff.append("\n");
            }
            conn.disconnect();
        } catch (Exception e) {
            Logger.error(e.getMessage(),e);
        } finally{
            if(br!= null){
                try {
                    br.close();
                } catch (IOException e) {
                    Logger.error(e.getMessage(),e);
                }
            }
            sbuff.append("</pre>");
            return sbuff.toString();
        }
    }

    @SecuredAction
    public static Result getadordebug() {
        Form<TestPagesFormEntity> filledForm = testPagesFormEntity.bindFromRequest();
        ObjectNode result = Json.newObject();
        TestPagesFormEntity tpe = filledForm.get();
        if("".equals(tpe.getSite_guid())){
            result.put("data", "SITE GUID EMPTY");
            return ok(result);
        }else if("".equals(tpe.getIp())){
            result.put("data", "IP EMPTY");
            return ok(result);
        }else if("".equals(tpe.getUa())){
            result.put("data", "UA Empty");
            return ok(result);
        }
        result.put("data", debugS2SRequest(tpe));
        return ok(result);
    }

    private static String exchangeRequest(TestPagesFormEntity tpe){
        URL url;
        StringBuffer sbuff = new StringBuffer("<pre>");
        BufferedReader br = null;
        try {
            url = new URL(tpe.getExchange_endpoint());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            if("DEBUG".equalsIgnoreCase(tpe.getExchange_type())){
                conn.setRequestProperty("KRITTER-DEBUG-SYSTEM", "true");
            }
            OutputStream os = conn.getOutputStream();
            os.write(tpe.getExchange_request().getBytes());
            os.flush();

            // open the stream and put it into BufferedReader
            br= new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sbuff.append(inputLine);
                sbuff.append("\n");
            }
            conn.disconnect();
        } catch (Exception e) {
            Logger.error(e.getMessage(),e);
        } finally{
            if(br!= null){
                try {
                    br.close();
                } catch (IOException e) {
                    Logger.error(e.getMessage(),e);
                }
            }
            sbuff.append("</pre>");
            return sbuff.toString();
        }
    }
    @SecuredAction
    public static Result exchangeresponsedebug() {
        Form<TestPagesFormEntity> filledForm = testPagesFormEntity.bindFromRequest();
        ObjectNode result = Json.newObject();
        TestPagesFormEntity tpe = filledForm.get();
        if("".equals(tpe.getExchange_endpoint())){
            result.put("data", "Exchange Endpoint Empty");
            return ok(result);
        }else if("".equals(tpe.getExchange_request())){
            result.put("data", "Exchange Request Empty");
            return ok(result);
        }

        result.put("data", exchangeRequest(tpe));
        return ok(result);
    }
    @SecuredAction
    public static Result performclick() {
        Form<TestPagesFormEntity> filledForm = testPagesFormEntity.bindFromRequest();
        ObjectNode result = Json.newObject();
        TestPagesFormEntity tpe = filledForm.get();
        if("".equals(tpe.getClick_endpoint())){
            result.put("data", "Click Endpoint Empty");
            return ok(result);
        }else if("".equals(tpe.getIp())){
            result.put("data", "IP Empty");
            return ok(result);
        }else if("".equals(tpe.getUa())){
            result.put("data", "UA Empty");
            return ok(result);
        }

        result.put("data", performclick(tpe));
        return ok(result);
    }
    private static String performclick(TestPagesFormEntity tpe){
        URL url;
        StringBuffer sbuff = new StringBuffer("");
        BufferedReader br = null;
        try {
            url = new URL(tpe.getClick_endpoint());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", tpe.getUa());
            conn.setRequestProperty("X-Forwarded-For", tpe.getIp());
            OutputStream os = conn.getOutputStream();
            os.write(tpe.getExchange_request().getBytes());
            os.flush();

            // open the stream and put it into BufferedReader
            br= new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String redirecturl= conn.getURL().toString();
            sbuff.append(redirecturl);
            /*String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sbuff.append(inputLine);
                sbuff.append("\n");
            }*/
            conn.disconnect();
        } catch (Exception e) {
            Logger.error(e.getMessage(),e);
        } finally{
            if(br!= null){
                try {
                    br.close();
                } catch (IOException e) {
                    Logger.error(e.getMessage(),e);
                }
            }
            sbuff.append("");
            return sbuff.toString();
        }
    }
}
