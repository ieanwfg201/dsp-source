package com.kritter.api_caller.core.action;

import com.kritter.utils.databasemanager.DatabaseManager;
import lombok.Getter;

/**
 * DatabaseConnector internally contains DatabaseManager
 * obtained through jndi configured inside tomcat.
 */
public class DatabaseConnector
{
    @Getter
    private DatabaseManager databaseManager;

    public DatabaseConnector(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

}
