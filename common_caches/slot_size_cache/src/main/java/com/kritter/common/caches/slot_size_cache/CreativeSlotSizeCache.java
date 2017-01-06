package com.kritter.common.caches.slot_size_cache;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.slot_size_cache.entity.CreativeSlotSize;
import com.kritter.utils.databasemanager.DBExecutionUtils;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class keeps slot id stored against slot size (width and height).
 * Methods are implemented to find out for a width,height combination
 * the closest and best slot.
 */
public class CreativeSlotSizeCache
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private AtomicReference<List<CreativeSlotSize>> creativeSlotSizeList;
    private DatabaseManager databaseManager;
    private static final String QUERY = "select * from creative_slots";
    private CreativeSlotSizeComparator comparator;
    private Timer timer;
    private TimerTask timerTask;

    public CreativeSlotSizeCache(
                                 DatabaseManager databaseManager,
                                 long reloadFrequency
                                ) throws InitializationException
    {
        this.databaseManager = databaseManager;
        this.creativeSlotSizeList = new AtomicReference<List<CreativeSlotSize>>(new ArrayList<CreativeSlotSize>());
        this.comparator = new CreativeSlotSizeComparator();

        try
        {
            buildDatabase();
        }
        catch (RefreshException re)
        {
            this.logger.error("RefreshException inside CreativeSlotSizeCache",re);
            throw new InitializationException("RefreshException inside CreativeSlotSizeCache",re);
        }

        this.timer = new Timer();
        this.timerTask = new CreativeSlotSizeReloadTimerTask();
        this.timer.schedule(this.timerTask,reloadFrequency,reloadFrequency);
    }

    private void buildDatabase() throws RefreshException
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<CreativeSlotSize> creativeSlotSizeListTemp = new ArrayList<CreativeSlotSize>();

        try
        {
            connection = this.databaseManager.getConnectionFromPool();

            statement = connection.createStatement();
            resultSet = statement.executeQuery(QUERY);

            while(resultSet.next())
            {
                short id = resultSet.getShort("id");
                short width = resultSet.getShort("width");
                short height = resultSet.getShort("height");

                CreativeSlotSize creativeSlotSize = new CreativeSlotSize(width,height,id);

                creativeSlotSizeListTemp.add(creativeSlotSize);
            }
        }
        catch(SQLException sqle)
        {
            logger.error("SQLException inside CreativeSlotSizeCache ",sqle);
            throw new RefreshException("SQLException thrown while processing CreativeSlotSizeCache Entry",sqle);
        }
        finally
        {
            DBExecutionUtils.closeResources(connection,statement,resultSet);
        }

        //use temp list if available after sorting.
        if(null != creativeSlotSizeListTemp && creativeSlotSizeListTemp.size() > 0)
        {
            Collections.sort(creativeSlotSizeListTemp,this.comparator);

            //replace using atomic reference variable
            this.creativeSlotSizeList.getAndSet(creativeSlotSizeListTemp);
        }
    }

    public short fetchClosestSlotIdForSize(int width, int height)
    {
        CreativeSlotSize requestingCreativeSlotSize = new CreativeSlotSize((short)width,(short)height,(short)-1);

        List<CreativeSlotSize> dataList = this.creativeSlotSizeList.get();

        for(int i = 0; i < dataList.size(); i ++)
        {
            CreativeSlotSize element = dataList.get(i);

            //find the first fitting element.
            if(requestingCreativeSlotSize.getWidth() >= element.getWidth() &&
               requestingCreativeSlotSize.getHeight() >= element.getHeight())
                return element.getSlotId();
        }

        return -1;
    }
    public short fetchSlotIdForExactSize(int width, int height)
    {
        CreativeSlotSize requestingCreativeSlotSize = new CreativeSlotSize((short)width,(short)height,(short)-1);

        List<CreativeSlotSize> dataList = this.creativeSlotSizeList.get();

        for(int i = 0; i < dataList.size(); i ++)
        {
            CreativeSlotSize element = dataList.get(i);

            //find the first fitting element.
            if(requestingCreativeSlotSize.getWidth() == element.getWidth() &&
               requestingCreativeSlotSize.getHeight() == element.getHeight())
                return element.getSlotId();
        }

        return -1;
    }

    private class CreativeSlotSizeComparator implements Comparator<CreativeSlotSize> {
        @Override
        public int compare(CreativeSlotSize creativeSlotSizeFirst, CreativeSlotSize creativeSlotSizeSecond) {
            if(creativeSlotSizeFirst == creativeSlotSizeSecond)
                return 0;

            if(null == creativeSlotSizeFirst)
                return 1;

            if(null == creativeSlotSizeSecond)
                return -1;

            if(creativeSlotSizeFirst.getWidth() > creativeSlotSizeSecond.getWidth())
                return -1;

            if(creativeSlotSizeFirst.getWidth() < creativeSlotSizeSecond.getWidth())
                return 1;

            if(creativeSlotSizeFirst.getHeight() > creativeSlotSizeSecond.getHeight())
                return -1;

            if(creativeSlotSizeFirst.getHeight() < creativeSlotSizeSecond.getHeight())
                return 1;

            return 0;
        }
    }

    /**
     * This class is responsible for reloading creative
     * slot sizes.
     */
    private class CreativeSlotSizeReloadTimerTask extends TimerTask
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
                cacheLogger.error("RefreshException while loading creative slot data inside CreativeSlotSizeReloadTimerTask in the class CreativeSlotSizeCache",re);
            }
        }
    }

    /**
     * This function releases any resources used in the instance.
     */
    public void releaseResources()
    {
        this.creativeSlotSizeList = null;
        if(null != this.timerTask && null != this.timer)
        {
            this.timerTask.cancel();
            this.timer.cancel();
        }
    }
}
