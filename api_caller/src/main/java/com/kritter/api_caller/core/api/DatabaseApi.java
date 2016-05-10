package com.kritter.api_caller.core.api;

import com.kritter.api_caller.core.action.ApiAction;
import com.kritter.api_caller.core.action.DatabaseApiAction;
import com.kritter.api_caller.core.model.DatabaseRequestEntity;

/**
 * This class implements an api which talks to database provided to it.
 * This class acts as base class for any database api which wishes to
 * perform some concrete actions.
 */
public class DatabaseApi implements Api{

    private DatabaseApiAction databaseApiAction;

    public DatabaseApi(DatabaseApiAction databaseApiAction){
        this.databaseApiAction = databaseApiAction;
    }

    @Override
    public String getApiSignature() {
        return null;
    }

    @Override
    public ApiAction getApiActionInstance() {
        return this.databaseApiAction;
    }

    @Override
    public Object processApiRequest(Object input) {

        DatabaseRequestEntity databaseRequestEntity = (DatabaseRequestEntity)input;

        return this.databaseApiAction.fetchApiResponseFromDatabase(databaseRequestEntity);
    }
}
