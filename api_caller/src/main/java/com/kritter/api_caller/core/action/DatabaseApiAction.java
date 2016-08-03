package com.kritter.api_caller.core.action;

import com.kritter.api_caller.core.model.DatabaseRequestEntity;
import com.kritter.api_caller.core.model.DatabaseResponseEntity;

/**
 * This interface represents methods used by some implementation to perform
 * relational database operations.
 */

public interface DatabaseApiAction extends ApiAction
{
    /**
     * This function processes data retrieved from database and returns back
     * response object for further processing.
     *
     * ApiRequestEntity would contain query, any parameters to be set to the
     * query using prepared statement etc.
     *
     * ApiResponseEntity would contain rows updated count or column values
     * inside set of rows.
     */
    public DatabaseResponseEntity fetchApiResponseFromDatabase(DatabaseRequestEntity databaseRequestEntity);
}