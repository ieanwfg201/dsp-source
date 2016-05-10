package com.kritter.api_caller.core.action;

import com.kritter.api_caller.core.model.ApiRequestEntity;
import com.kritter.api_caller.core.model.ApiResponseEntity;

/**
 * This interface represents a relational database interaction
 * for an api action.
 *
 */

public interface ApiDatabaseAction extends ApiAction {

    /**
     * Database Connector which would handle this api action
     * processing with some relational database.
     * DatabaseConnector internally contains DatabaseManager
     * obtained through jndi configured inside tomcat.
     */
    public DatabaseConnector getDatabaseConnector();

    /**
     * This function processes data retrieved from database and returns back
     * response object for further processing.
     * ApiRequestEntity would contain query, any parameters to be set to the
     * query using prepared statement etc.
     */
    public ApiResponseEntity fetchApiResponseFromDatabase(ApiRequestEntity apiRequestEntity);

}
