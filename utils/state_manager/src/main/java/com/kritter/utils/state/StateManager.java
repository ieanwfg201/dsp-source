package com.kritter.utils.state;


import com.kritter.api_caller.core.action.DatabaseApiAction;
import com.kritter.api_caller.core.model.DatabaseRequestEntity;
import com.kritter.api_caller.core.model.DatabaseResponseEntity;
import com.kritter.api_caller.core.util.DBTableColumnValueWithType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.sql.Types;

/**
 * This class is responsible to maintain status of different jobs that require
 * exclusivity in terms of execution, if a job is running or completed then
 * the same job from some other node/machine cannot proceed until the status
 * has been marked as false for that job.
 * Examples: handset_data_population, location_data_population, third party
 * web server authorization.
 *
 * To implement this a table is maintained with schema as:
 * state_identifier | description | status | last_modified.
 */
public class StateManager
{
    private DatabaseApiAction databaseApiAction;

    private static final String QUERY_INSERT_NEW_STATE = "insert into process_state values (?,?,?,?)";
    private static final String READ_STATE_DATA = "select status from process_state where state_identifier = ?";
    private static final String UPDATE_STATE_DATA_AS_BUSY = "update process_state set status = true , " +
                                                            "last_modified = now() where state_identifier = ?" +
                                                            " and status = false";

    private static final String UPDATE_STATE_DATA_AS_FREE = "update process_state set status = false , " +
                                                            "last_modified = now() where state_identifier = ?" +
                                                            " and status = true";

    private static final String[] COLUMN_FOR_STATE_STATUS = {"status"};

    @Getter @Setter
    private static boolean handsetBusyStateByThisNode = false;
    @Getter @Setter
    private static boolean locationBusyStateByThisNode = false;

    public enum STATE_IDENTIFIER
    {
        HANDSET_DATA_POPULATOR_JOB("handset_data_population","handset data populator job"),
        LOCATION_DATA_POPULATOR_JOB("location_data_population","location data populator job");

        @Getter
        private String stateIdentifier;
        @Getter
        private String description;

        private STATE_IDENTIFIER(String stateIdentifier,String description)
        {
            this.stateIdentifier = stateIdentifier;
            this.description = description;
        }
    }

    public static class STATE_DATA
    {
        @Getter
        private STATE_IDENTIFIER stateIdentifier;
        @Getter
        private boolean status;
        @Getter
        private Timestamp lastModified;

        public STATE_DATA(STATE_IDENTIFIER stateIdentifier,
                           boolean status,Timestamp lastModified)
        {
            this.stateIdentifier = stateIdentifier;
            this.status = status;
            this.lastModified = lastModified;
        }
    }

    public StateManager(DatabaseApiAction databaseApiAction)
    {
        this.databaseApiAction = databaseApiAction;
    }

    /**
     * This function inserts a new state into database marked as in progress.
     * Any other module or reader looking at this state should skip its process.
     * This function retries at a given frequency for a given number of attempts.
     *
     * @param stateData
     * @return
     */
    public boolean insertNewProcessStateInProgress(STATE_DATA stateData,long timeToSleep,int numberOfAttempts)
    {
        DatabaseRequestEntity databaseRequestEntity = new DatabaseRequestEntity(QUERY_INSERT_NEW_STATE,null,true);

        STATE_IDENTIFIER stateIdentifier = stateData.getStateIdentifier();
        databaseRequestEntity.setColumnForPreparedStatement(new DBTableColumnValueWithType(
                                                                stateIdentifier.getStateIdentifier(), Types.VARCHAR));

        databaseRequestEntity.setColumnForPreparedStatement(new DBTableColumnValueWithType(
                                                                stateIdentifier.getDescription(), Types.VARCHAR));

        databaseRequestEntity.setColumnForPreparedStatement(new DBTableColumnValueWithType(
                                                                stateData.isStatus(), Types.BOOLEAN));

        databaseRequestEntity.setColumnForPreparedStatement(new DBTableColumnValueWithType(
                                                                stateData.getLastModified(), Types.TIMESTAMP));

        DatabaseResponseEntity databaseResponseEntity = (DatabaseResponseEntity)databaseApiAction.
                                                                  fetchApiResponseFromDatabase(databaseRequestEntity);

        boolean operationStatus = databaseResponseEntity.getRowsUpdated() > 0 ? true : false;

        int attemptsMade = 1;

        //if operation is not successful, retry.
        //could be possible that some other process got hold of the state and made
        //insertion before this one could.
        //So check for status again, if not null check for false if false make true and proceed.
        //if true then wait till number of attempts and as per configured sleep time.

        if(!operationStatus)
        {
            while (attemptsMade <= numberOfAttempts)
            {
                Boolean status = getStatusForTheProcessState(stateIdentifier);

                //means some other node has already made the insertion.
                if(null != status)
                {
                    //no other node has taken the lock or is done with using it.
                    if(!status)
                    {
                        return updateBusyStatusForTheProcessState(stateIdentifier, timeToSleep, numberOfAttempts);
                    }
                    //else go for retry.
                    else
                    {
                        try
                        {
                            Thread.sleep(timeToSleep);
                        }
                        catch (InterruptedException ie)
                        {

                        }
                    }
                }
                //means some database problem.so try again.
                else
                {
                    databaseResponseEntity = (DatabaseResponseEntity)databaseApiAction.
                                                            fetchApiResponseFromDatabase(databaseRequestEntity);
                    operationStatus = databaseResponseEntity.getRowsUpdated() > 0 ? true : false;

                    if(operationStatus)
                        break;

                    try
                    {
                        Thread.sleep(timeToSleep);
                    }
                    catch (InterruptedException ie)
                    {

                    }
                }

                attemptsMade ++;
            }
        }
        return operationStatus;
    }

    /**
     * This function reads status for a given process state identifier.
     * null as return value means the state does not exist in database.
     *
     * @param stateIdentifier
     * @return
     */
    public Boolean getStatusForTheProcessState(STATE_IDENTIFIER stateIdentifier)
    {
        DatabaseRequestEntity databaseRequestEntity = new DatabaseRequestEntity(READ_STATE_DATA,
                                                                                COLUMN_FOR_STATE_STATUS,false);

        databaseRequestEntity.setColumnForPreparedStatement(new DBTableColumnValueWithType(
                                                                stateIdentifier.getStateIdentifier(),Types.VARCHAR));

        DatabaseResponseEntity databaseResponseEntity = (DatabaseResponseEntity)databaseApiAction.
                                                                  fetchApiResponseFromDatabase(databaseRequestEntity);

        if(databaseResponseEntity.getRetrievedColumnValues().size() <= 0)
            return null;

        return (Boolean)databaseResponseEntity.getRetrievedColumnValues().get(0).get(COLUMN_FOR_STATE_STATUS[0]);
    }


    public boolean updateBusyStatusForTheProcessState(
                                                      STATE_IDENTIFIER stateIdentifier,
                                                      long timeToSleep,
                                                      int numberOfAttempts
                                                     )
    {
        DatabaseRequestEntity databaseRequestEntity = new DatabaseRequestEntity(UPDATE_STATE_DATA_AS_BUSY,null,true);

        databaseRequestEntity.setColumnForPreparedStatement
                                    (
                                     new DBTableColumnValueWithType(
                                                                    stateIdentifier.getStateIdentifier(),
                                                                    Types.VARCHAR
                                                                   )
                                    );

        DatabaseResponseEntity databaseResponseEntity =
                               (DatabaseResponseEntity)databaseApiAction.
                                                       fetchApiResponseFromDatabase(databaseRequestEntity);

        boolean operationStatus = databaseResponseEntity.getRowsUpdated() > 0 ? true : false;

        int attemptsMade = 1;

        //if operation is not successful, retry.
        if(!operationStatus)
        {
            while (attemptsMade <= numberOfAttempts)
            {
                databaseResponseEntity =
                    (DatabaseResponseEntity)databaseApiAction.fetchApiResponseFromDatabase(databaseRequestEntity);

                operationStatus = databaseResponseEntity.getRowsUpdated() > 0 ? true : false;

                if(operationStatus)
                    break;

                try
                {
                    Thread.sleep(timeToSleep);
                }
                catch (InterruptedException ie)
                {

                }

                attemptsMade ++;
            }
        }
        return operationStatus;
    }

    public boolean updateFreeStatusForTheProcessState(
                                                      STATE_IDENTIFIER stateIdentifier,
                                                      long timeToSleep,
                                                      int numberOfAttempts
                                                     )
    {
        DatabaseRequestEntity databaseRequestEntity = new DatabaseRequestEntity(UPDATE_STATE_DATA_AS_FREE,null,true);

        databaseRequestEntity.setColumnForPreparedStatement
                (
                        new DBTableColumnValueWithType(
                                stateIdentifier.getStateIdentifier(),
                                Types.VARCHAR
                        )
                );

        DatabaseResponseEntity databaseResponseEntity =
                (DatabaseResponseEntity)databaseApiAction.
                        fetchApiResponseFromDatabase(databaseRequestEntity);

        boolean operationStatus = databaseResponseEntity.getRowsUpdated() > 0 ? true : false;

        int attemptsMade = 1;

        //if operation is not successful, retry.
        if(!operationStatus)
        {
            while (attemptsMade <= numberOfAttempts)
            {
                databaseResponseEntity =
                        (DatabaseResponseEntity)databaseApiAction.fetchApiResponseFromDatabase(databaseRequestEntity);

                operationStatus = databaseResponseEntity.getRowsUpdated() > 0 ? true : false;

                if(operationStatus)
                    break;

                try
                {
                    Thread.sleep(timeToSleep);
                }
                catch (InterruptedException ie)
                {

                }

                attemptsMade ++;
            }
        }
        return operationStatus;
    }
}
