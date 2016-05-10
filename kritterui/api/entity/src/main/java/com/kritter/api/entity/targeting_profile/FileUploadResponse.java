package com.kritter.api.entity.targeting_profile;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class FileUploadResponse {
    /** From ErrorEnum */
    private int errorCode = 0; 
    /** general message */
    private String message = "";
    /** path */
    private String path = "";
    /** preview URL */
    private String preview_label = "";
    /** preview URL */
    private String preview_url = "";
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + errorCode;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result
                + ((preview_label == null) ? 0 : preview_label.hashCode());
        result = prime * result
                + ((preview_url == null) ? 0 : preview_url.hashCode());
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
        FileUploadResponse other = (FileUploadResponse) obj;
        if (errorCode != other.errorCode)
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (preview_label == null) {
            if (other.preview_label != null)
                return false;
        } else if (!preview_label.equals(other.preview_label))
            return false;
        if (preview_url == null) {
            if (other.preview_url != null)
                return false;
        } else if (!preview_url.equals(other.preview_url))
            return false;
        return true;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPreview_label() {
        return preview_label;
    }
    public void setPreview_label(String preview_label) {
        this.preview_label = preview_label;
    }
    public String getPreview_url() {
        return preview_url;
    }
    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static FileUploadResponse getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        FileUploadResponse entity = objectMapper.readValue(str, FileUploadResponse.class);
        return entity;

    }

}
