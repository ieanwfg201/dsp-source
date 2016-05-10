package com.kritter.api.upload_to_cdn.everest;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.upload_to_cdn.IUploadToCDN;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class UploadToCDN implements IUploadToCDN {
    private static final Logger LOG = LoggerFactory.getLogger(UploadToCDN.class);

    private String j_security_username;
    private String j_security_password;
    private String account;
    private String uploadURL;

    public String upload(File file, String reference) throws Exception{
        InputStream fileInputStream = FileUtils.openInputStream(file);
        ByteArrayPartSource source = new ByteArrayPartSource("banner", IOUtils.toByteArray(fileInputStream));

        StringBuilder urlBuilder = new StringBuilder(this.uploadURL);
        appendParameterToUrl(urlBuilder, "account", this.account);
        appendParameterToUrl(urlBuilder, "j_security_username", this.j_security_username);
        appendParameterToUrl(urlBuilder, "j_security_password", this.j_security_password);
        appendParameterToUrl(urlBuilder, "reference", reference);

        PostMethod post = null;
        try{
            post = new PostMethod(urlBuilder.toString());
            post.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE,true);
            post.setFollowRedirects(false);
            Part[] parts = {new FilePart("banner", source)};
            post.setRequestEntity(new MultipartRequestEntity(parts,post.getParams()));
            HttpClient client = new HttpClient();
            client.executeMethod(post);
            return IOUtils.toString(post.getResponseBodyAsStream());
        }catch(Exception e){
            LOG.error(e.getMessage(), e);
            return null;
        }finally{
            if(post != null){
                post.releaseConnection();
            }
        }

    }
    private void appendParameterToUrl(StringBuilder builder, String name, String value) throws UnsupportedEncodingException {
        String seperator = builder.indexOf("?") == -1 ? "?" : "&";
        builder.append(seperator).append(name).append("=").append(URLEncoder.encode(value, "UTF-8"));
    }
    @Override
    public boolean upload(String url, Map<String, String> getParams,
            Map<String, String> postParams, File file, String id)  {
        this.account = postParams.get("account");
        this.j_security_password = postParams.get("j_security_password");
        this.j_security_username = postParams.get("j_security_username");
        this.uploadURL = url;
        try{
            String responseStr = upload(file, id);
            if(responseStr == null){
                return false;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(responseStr, JsonNode.class);
            String status = jsonNode.get("status").getTextValue();
            if("successful".equals(status)){
                return true;
            }
            LOG.info(responseStr);
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return false;
        }
        return false;
    }
    
    

}
