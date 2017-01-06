package com.kritter.api_caller.core.action;

import com.kritter.api_caller.core.model.DatabaseRequestEntity;
import com.kritter.api_caller.core.model.DatabaseResponseEntity;
import com.kritter.api_caller.core.util.DBTableColumnValueWithType;
import com.kritter.api_caller.core.util.DatabaseConnector;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * This class represents api action to interact with database and execute
 * requests and return corresponding response.
 *
 * -- Explanation :
 *
 * 1. The database api action class serves as base class to any database related action.
 * 2. The input entity it takes contains query and column values to set in prepared statement,
 *    the column value comes with its sql type.
 * 3. The response entity contains, column name versus its value map.The end user has to
 *    know the column type and where to populate the resulting value.
 */

public class DatabaseApiActionImplementation implements DatabaseApiAction
{
    private Logger logger;
    private DatabaseConnector databaseConnector;

    public DatabaseApiActionImplementation(Logger logger,DatabaseConnector databaseConnector)
    {
        this.logger = logger;
        this.databaseConnector = databaseConnector;
    }

    @Override
    public DatabaseResponseEntity fetchApiResponseFromDatabase(DatabaseRequestEntity databaseRequestEntity)
    {
        logger.debug("Inside fetchApiResponseFromDatabase() of DatabaseApiAction.");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        DatabaseResponseEntity databaseResponseEntity = null;
        List<Map<String,Object>> retrievedColumnValues = new LinkedList<Map<String, Object>>();
        Map<String,Object> columnValues = null;

        Integer rowsUpdated = null;

        DatabaseManager databaseManager = this.databaseConnector.getDatabaseManager();

        try
        {
            connection = databaseManager.getConnectionFromPool();
            preparedStatement = connection.prepareStatement(databaseRequestEntity.getQuery());

            if(
               null != databaseRequestEntity.getColumnsToSet() &&
               databaseRequestEntity.getColumnsToSet().size() > 0
              )
            {

                Iterator<DBTableColumnValueWithType> iterator = databaseRequestEntity.getColumnsToSet().iterator();
                int counter = 1;

                while (iterator.hasNext())
                {
                    DBTableColumnValueWithType entry = iterator.next();
                    preparedStatement.setObject(counter,entry.getValue(),entry.getSqlType());
                    counter++;
                }
            }

            if(databaseRequestEntity.getIsQueryForModification())
            {
                rowsUpdated = preparedStatement.executeUpdate();
                logger.debug("The database action is for updation, rows updated/inserted {}", rowsUpdated);
            }
            else
            {
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()){

                    columnValues = new HashMap<String, Object>();

                    for(String columnName :  databaseRequestEntity.getColumnNamesToRead()){

                        Object value = resultSet.getObject(columnName);

                        columnValues.put(columnName,value);
                        logger.debug("The database action is for retrieval, column read:{} value read: ",columnName, value);
                    }

                    retrievedColumnValues.add(columnValues);
                }
            }

            databaseResponseEntity = new DatabaseResponseEntity(retrievedColumnValues,rowsUpdated);

        }
        catch (SQLException sqle)
        {
            logger.error("SQLException inside DatabaseApiAction execution method,result could not be {} retrieved ", sqle);

            //set database response entity with empty retrieved columns and 0 updated rows.
            databaseResponseEntity = new DatabaseResponseEntity(new ArrayList<Map<String, Object>>(),0);
        }
        finally
        {
            DBExecutionUtils.closeResources(connection,preparedStatement,resultSet);
        }

        return databaseResponseEntity;
    }

}
