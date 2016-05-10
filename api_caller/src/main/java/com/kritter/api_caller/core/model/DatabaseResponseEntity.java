package com.kritter.api_caller.core.model;

import com.kritter.api_caller.core.model.ApiResponseEntity;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * This class represents the sql result from database.
 * This stores column_name -> value in a map , which
 * when retrieved by the end user, is converted to
 * appropriate types and stored as result.
 */
public class DatabaseResponseEntity implements ApiResponseEntity{

    @Getter
    private List<Map<String,Object>> retrievedColumnValues;
    @Getter
    private Integer rowsUpdated;

    public DatabaseResponseEntity(List<Map<String,Object>> retrievedColumnValues,Integer rowsUpdated){
        this.retrievedColumnValues = retrievedColumnValues;
        this.rowsUpdated = rowsUpdated;
    }
}
