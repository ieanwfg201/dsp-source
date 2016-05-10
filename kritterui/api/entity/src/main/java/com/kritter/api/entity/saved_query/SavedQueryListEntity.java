package com.kritter.api.entity.saved_query;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.PageConstants;
import com.kritter.constants.SavedQueryEnum;

public class SavedQueryListEntity {
    private int page_no = PageConstants.start_index;
    private int page_size = PageConstants.page_size;
    private SavedQueryEnum saveQueryEnum = SavedQueryEnum.list_saved_query_entity_by_account_guids;
    private String id_list = null;
    public int getPage_no() {
        return page_no;
    }
    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }
    public int getPage_size() {
        return page_size;
    }
    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }
    public SavedQueryEnum getSaveQueryEnum() {
        return saveQueryEnum;
    }
    public void setSaveQueryEnum(SavedQueryEnum saveQueryEnum) {
        this.saveQueryEnum = saveQueryEnum;
    }
    public String getId_list() {
        return id_list;
    }
    public void setId_list(String id_list) {
        this.id_list = id_list;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static SavedQueryListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        SavedQueryListEntity entity = objectMapper.readValue(str, SavedQueryListEntity.class);
        return entity;
    }
    
}
