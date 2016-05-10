package com.kritter.core.workflow;

import com.kritter.abstraction.cache.entities.CachePool;
import com.kritter.abstraction.cache.entities.CacheReloadTimerTask;
import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.abstraction.cache.interfaces.IRefreshable;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.core.metrics.YammerMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.Driver;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class Workflow {
    private final JobSet initJobSet;
    private final CachePool cachePool;
    private final YammerMetrics metrics;

    public static String CONTEXT_REQUEST_KEY = "http-request";
    public static String CONTEXT_RESPONSE_KEY = "http-response";

    public static String WORKFLOW_LOGGER_NAME = "workflow.logger";
    private List<Timer> cacheReloadTimerList;
    private List<TimerTask> cacheReloadTimerTaskList;

    private static Logger workflowLogger = LoggerFactory.getLogger(WORKFLOW_LOGGER_NAME);

    private boolean useMacAddressImplForUUID;

    public Workflow(JobSet initJobSet, CachePool cachePool,boolean useMacAddressImplForUUID) {

        this.initJobSet = initJobSet;
        if(initJobSet == null)
            throw new RuntimeException("Init job set cannot be null");
        this.cachePool = cachePool;
        this.metrics = new YammerMetrics();
        this.cacheReloadTimerList = new ArrayList<Timer>();
        this.cacheReloadTimerTaskList = new ArrayList<TimerTask>();

        workflowLogger.debug("Cache Pool assigned to workflow.");
        Map<String,ICache> cacheMap = this.cachePool.getCacheMap();


        for(Map.Entry<String,ICache> entry: cacheMap.entrySet()){

            ICache cache = entry.getValue();
            workflowLogger.debug(cache.getName());
            workflowLogger.debug("Checking whether cache is refreshable for first time run.");

            if(IRefreshable.class.isAssignableFrom(entry.getValue().getClass())){

                workflowLogger.debug("The cache is refreshable,running refresh for first time."
                                      + IRefreshable.class);
                IRefreshable refreshableCache = (IRefreshable)entry.getValue();
                try{
                    refreshableCache.refresh();
                }
                catch(RefreshException re){
                    workflowLogger.error("Exception in refreshable cache first run", re);
                    throw new RuntimeException("Exception in refreshable cache first run",re);
                }
            }
        }

        workflowLogger.debug("Going to run timer task for each of the refreshable cache.Total Cache map size is:");
        workflowLogger.debug(String.valueOf(cacheMap.size()));

        for(Map.Entry<String,ICache> entry: cacheMap.entrySet()){

            if(IRefreshable.class.isAssignableFrom(entry.getValue().getClass())){

                workflowLogger.debug("The cache is refreshable, assigning refresh method to timer thread.");
                IRefreshable refreshableCache = (IRefreshable)entry.getValue();
                CacheReloadTimerTask timerTask = new CacheReloadTimerTask(workflowLogger,refreshableCache,refreshableCache.getName());
                Timer timer = new Timer();
                timer.schedule(timerTask,0,refreshableCache.getRefreshInterval());
                this.cacheReloadTimerList.add(timer);
                workflowLogger.debug("Created and added timer task to list");
                this.cacheReloadTimerTaskList.add(timerTask);
                workflowLogger.debug("Created and added timer to list");
            }

        }

        this.useMacAddressImplForUUID = useMacAddressImplForUUID;
    }

    public void executeRequest(HttpServletRequest request, HttpServletResponse response) {

        Context context = new Context(this.useMacAddressImplForUUID);
        context.setValue(CONTEXT_REQUEST_KEY, request);
        context.setValue(CONTEXT_RESPONSE_KEY, response);

        metrics.incrementTotalInvocations();
        long beginTime = System.currentTimeMillis();

        JobSet currentJobSet = initJobSet;

        if(workflowLogger.isDebugEnabled())
            workflowLogger.debug("Starting job set: " + currentJobSet.getName());

        while(currentJobSet != null)
        {
            boolean successCode = currentJobSet.execute(context, metrics);

            if(workflowLogger.isDebugEnabled())
                workflowLogger.debug("The current job set " + currentJobSet.getName() +
                                     " executed with success code as : " + successCode);

            if(!successCode)
                metrics.incrementTotalFailures();

            if(!successCode)
                currentJobSet = currentJobSet.getErrorJobSet();
            else
                currentJobSet = currentJobSet.getNextJob(context);
        }

        long endTime = System.currentTimeMillis();
        metrics.incrementTotalLatency(endTime - beginTime);
    }

    public void destroy() {
        Map<String, ICache> cacheMap = cachePool.getCacheMap();
        for(Map.Entry<String, ICache> entry : cacheMap.entrySet()) {
            ICache cache = entry.getValue();
            cache.destroy();
        }

        Iterator<TimerTask> timerTaskIterator = this.cacheReloadTimerTaskList.iterator();
        while(timerTaskIterator.hasNext()) {
            TimerTask timerTask = timerTaskIterator.next();
            timerTask.cancel();
            workflowLogger.debug("Cancelled timer task");
        }

        Iterator<Timer> it = this.cacheReloadTimerList.iterator();

        while(it.hasNext()){
            Timer timer = it.next();
            timer.cancel();
            timer.purge();
            workflowLogger.debug("Cancelled timer");
        }

        // Manually deregister all the drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while(drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                workflowLogger.debug("Deregistering driver");
            } catch(SQLException e) {
                workflowLogger.error("Error deregistering driver");
            }
        }
    }

    /**
     * This method returns stats of the system.
     * @return
     */
    public String getStats()
    {
        StringBuffer stats = new StringBuffer();
        stats.append("{\"invocations\":");
        stats.append(metrics.getTotalInvocations());
        stats.append(",");
        stats.append("\"latency\":");
        stats.append(metrics.getTotalLatency());
        stats.append(",");
        stats.append("\"failures\":");
        stats.append(metrics.getTotalFailures());
        stats.append("}");

        return stats.toString();
    }
}
