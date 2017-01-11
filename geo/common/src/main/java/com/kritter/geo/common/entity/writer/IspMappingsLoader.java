package com.kritter.geo.common.entity.writer;

import com.kritter.geo.common.entity.CountryIspUnique;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.IspMappingsValueEntity;
import com.kritter.geo.common.utils.GeoCommonUtils;
import com.kritter.utils.common.ServerConfig;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.*;
import java.util.*;

/**
 * This class looks for new isp mappings that are fed into database,
 * if new entries are found then for all the datasources configured
 * it will feed data into ui_targeting_isp table for new mappings
 * found.
 */
public class IspMappingsLoader
{
    private Logger logger;
    private DatabaseManager databaseManager;
    private String[] dataSourceNames;
    private int batchSizeForSqlInsertion;
    private Timer timer;
    private TimerTask timerTask;
    private boolean dataLoadMasterNode;

    public IspMappingsLoader(String loggerName,
                             DatabaseManager databaseManager,
                             String[] dataSourceNames,
                             int batchSizeForSqlInsertion,
                             int reloadFrequency,
                             ServerConfig serverConfig,
                             String dataLoadMasterNodeParam) throws Exception
    {
        this.logger = LogManager.getLogger(loggerName);
        this.databaseManager = databaseManager;
        this.dataSourceNames = dataSourceNames;
        this.batchSizeForSqlInsertion = batchSizeForSqlInsertion;
        this.dataLoadMasterNode = Boolean.valueOf(serverConfig.getValueForKey(dataLoadMasterNodeParam));

        if(this.dataLoadMasterNode)
        {
            buildAndUseIspMappingsDataForNewEntries();
            this.timer = new Timer();
            this.timerTask = new ISPMappingsReloadTimerTask();
            this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
        }
    }

    private void buildAndUseIspMappingsDataForNewEntries() throws Exception
    {
        if(logger.isDebugEnabled())
            logger.debug("Inside buildIspMappingsDataForNewEntries of IspMappingsLoader...");

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;



        try
        {
            connection = this.databaseManager.getConnectionFromPool();

            for(String dataSourceName : dataSourceNames)
            {
                //now for each datasource configured ,
                //feed into ui_targeting_isp table looking at the new entries found.
                Map<String,InternetServiceProvider> ispDataFromSql = GeoCommonUtils.
                                            fetchISPDataFromSqlDatabaseForDataSource(connection, dataSourceName);

                GeoCommonUtils.
                            feedISPDataForTargetingExposureOnUserInterface
                                    (logger,
                                     new HashSet<InternetServiceProvider>(ispDataFromSql.values()),
                                     connection,
                                     dataSourceName,
                                     batchSizeForSqlInsertion);
            }
        }
        catch (SQLException sqle)
        {
            logger.error("SQLException inside buildIspMappingsDataForNewEntries of IspMappingsLoader",sqle);
        }
        finally
        {
            DBExecutionUtils.closeResources(pstmt,rs);
            DBExecutionUtils.closeConnection(connection);
        }
    }

    /**
     * This class is responsible for looking up for any updates in isp_mappings
     * table and then use it for ui_targeting_isp table population.
     */
    private class ISPMappingsReloadTimerTask extends TimerTask
    {
        private Logger cacheLogger = LogManager.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                buildAndUseIspMappingsDataForNewEntries();
            }
            catch (Exception e)
            {
                cacheLogger.error("Exception inside ISPMappingsReloadTimerTask... " ,e);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }
}