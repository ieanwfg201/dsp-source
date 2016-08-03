package com.kritter.api_caller.core.api;

import com.kritter.api_caller.core.action.DatabaseApiAction;
import com.kritter.api_caller.core.model.DatabaseRequestEntity;
import com.kritter.api_caller.core.model.DatabaseResponseEntity;

/**
 * This class implements an api which talks to database provided to it.
 * This class acts as base class for any database api which wishes to
 * perform some concrete actions.
 */
public class DatabaseApi implements Api<DatabaseResponseEntity,DatabaseRequestEntity>
{
    private DatabaseApiAction databaseApiAction;
    private String databaseApiSignature;

    public DatabaseApi(DatabaseApiAction databaseApiAction,String databaseApiSignature)
    {
        this.databaseApiAction = databaseApiAction;
        this.databaseApiSignature = databaseApiSignature;
    }

    @Override
    public String getApiSignature()
    {
        return this.databaseApiSignature;
    }

    @Override
    public DatabaseResponseEntity processApiRequest(DatabaseRequestEntity input)
    {
        if(null == input)
            return null;

        return this.databaseApiAction.fetchApiResponseFromDatabase(input);
    }
}