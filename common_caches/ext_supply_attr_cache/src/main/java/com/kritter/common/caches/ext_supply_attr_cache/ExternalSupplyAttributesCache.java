package com.kritter.common.caches.ext_supply_attr_cache;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.serving.demand.entity.ExternalSupplyAttributes;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class keeps external supply attributes that are found in ad-exchange bid requests.
 */
public class ExternalSupplyAttributesCache
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private Map<String,ExternalSupplyAttributes> externalSupplyAttributesDataMap;
    private DatabaseManager databaseManager;
    private static final String QUERY =
            "select id,site_inc_id,ext_supply_id,ext_supply_name,ext_supply_domain from ext_supply_attr where approved = true and unapproved = false";


    private Timer timer;
    private TimerTask timerTask;
    private static final String DELIMITER = String.valueOf((char)1);

    public ExternalSupplyAttributesCache(
                                         DatabaseManager databaseManager,
                                         long reloadFrequency
                                        ) throws InitializationException
    {
        this.databaseManager = databaseManager;
        this.externalSupplyAttributesDataMap = new ConcurrentHashMap<String, ExternalSupplyAttributes>();

        try
        {
            buildDatabase();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside ExternalSupplyAttributesCache ",re);
            throw new InitializationException("RefreshException inside ExternalSupplyAttributesCache ",re);
        }

        this.timer = new Timer();
        this.timerTask = new ExternalSupplyAttributesReloadTimerTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    private void buildDatabase() throws RefreshException
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try
        {
            connection = this.databaseManager.getConnectionFromPool();

            statement = connection.createStatement();
            resultSet = statement.executeQuery(QUERY);

            while(resultSet.next())
            {
                //default value of internal id as -1.
                Integer internalId = resultSet.getInt("id");;
                Integer siteIncId = resultSet.getInt("site_inc_id");
                String externalSupplyId = resultSet.getString("ext_supply_id");
                String externalSupplyName = resultSet.getString("ext_supply_name");
                String externalSupplyDomain = resultSet.getString("ext_supply_domain");

                ExternalSupplyAttributes externalSupplyAttributes = new ExternalSupplyAttributes();
                externalSupplyAttributes.setInternalSupplyId(internalId);
                externalSupplyAttributes.setExternalSupplyId(externalSupplyId);
                externalSupplyAttributes.setExternalSupplyName(externalSupplyName);
                externalSupplyAttributes.setExternalSupplyDomain(externalSupplyDomain);

                String key = prepareKeyForStorage(siteIncId,externalSupplyId);

                externalSupplyAttributesDataMap.put(key,externalSupplyAttributes);
            }
        }
        catch(SQLException sqle)
        {
            logger.error("SQLException inside ExternalSupplyAttributesCache ",sqle);
            throw new RefreshException("SQLException thrown while processing ExternalSupplyAttributesCache Entry ",
                                       sqle);
        }
        finally
        {
            DBExecutionUtils.closeResources(connection,statement,resultSet);
        }
    }

    public Integer fetchExternalSupplyAttributesInternalId(Integer siteIncId,String externalSupplyId)
    {
        String key = prepareKeyForStorage(siteIncId,externalSupplyId);
        ExternalSupplyAttributes externalSupplyAttributes = externalSupplyAttributesDataMap.get(key);

        if(null != externalSupplyAttributes)
            return externalSupplyAttributes.getInternalSupplyId();

        return null;
    }

    private String prepareKeyForStorage(Integer siteIncId,String externalSupplyId)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(siteIncId);
        sb.append(DELIMITER);
        sb.append(externalSupplyId);

        return sb.toString();
    }

    /**
     * This class is responsible for reloading external supply attributes from database.
     */
    private class ExternalSupplyAttributesReloadTimerTask extends TimerTask
    {
        private Logger cacheLogger = LogManager.getLogger("cache.logger");

        @Override
        public void run()
        {
            try
            {
                buildDatabase();
            }
            catch (RefreshException re)
            {
                cacheLogger.error("RefreshException while loading external supply attributes data inside ExternalSupplyAttributesReloadTimerTask in the class ExternalSupplyAttributesCache",re);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.externalSupplyAttributesDataMap = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }
}
