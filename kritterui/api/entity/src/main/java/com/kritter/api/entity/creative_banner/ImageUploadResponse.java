package com.kritter.api.entity.creative_banner;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ImageUploadResponse {
    /** From ErrorEnum */
    private int errorCode = 0; 
    /** general message */
    private String message = "";
    /** slot option */
    private String slotOptions = "";
    /** thumbnail URL */
    private String thumbUrl = "";
    /** banner URL */
    private String bannerUrl = "";
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((bannerUrl == null) ? 0 : bannerUrl.hashCode());
        result = prime * result + errorCode;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result
                + ((slotOptions == null) ? 0 : slotOptions.hashCode());
        result = prime * result
                + ((thumbUrl == null) ? 0 : thumbUrl.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageUploadResponse other = (ImageUploadResponse) obj;
        if (bannerUrl == null) {
            if (other.bannerUrl != null)
                return false;
        } else if (!bannerUrl.equals(other.bannerUrl))
            return false;
        if (errorCode != other.errorCode)
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (slotOptions == null) {
            if (other.slotOptions != null)
                return false;
        } else if (!slotOptions.equals(other.slotOptions))
            return false;
        if (thumbUrl == null) {
            if (other.thumbUrl != null)
                return false;
        } else if (!thumbUrl.equals(other.thumbUrl))
            return false;
        return true;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getSlotOptions() {
        return slotOptions;
    }
    public void setSlotOptions(String slotOptions) {
        this.slotOptions = slotOptions;
    }
    public String getThumbUrl() {
        return thumbUrl;
    }
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
    public String getBannerUrl() {
        return bannerUrl;
    }
    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ImageUploadResponse getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ImageUploadResponse entity = objectMapper.readValue(str, ImageUploadResponse.class);
        return entity;

    }

}
