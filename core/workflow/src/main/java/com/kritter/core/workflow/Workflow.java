package com.kritter.core.workflow;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.kritter.abstraction.cache.entities.CachePool;
import com.kritter.abstraction.cache.entities.CacheReloadTimerTask;
import com.kritter.abstraction.cache.interfaces.ICache;
import com.kritter.abstraction.cache.interfaces.IRefreshable;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.core.metrics.YammerMetrics;
import lombok.Getter;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Driver;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class Workflow
{
    private final JobSet initJobSet;
    @Getter
    private final CachePool cachePool;
    private final YammerMetrics metrics;

    public static String CONTEXT_REQUEST_KEY = "http-request";
    public static String CONTEXT_RESPONSE_KEY = "http-response";

    public static String WORKFLOW_LOGGER_NAME = "workflow.logger";
    private List<Timer> cacheReloadTimerList;
    private List<TimerTask> cacheReloadTimerTaskList;

    private static Logger workflowLogger = LoggerFactory.getLogger(WORKFLOW_LOGGER_NAME);

    private boolean useMacAddressImplForUUID;

    public Workflow(JobSet initJobSet, CachePool cachePool,boolean useMacAddressImplForUUID)
    {
        this.initJobSet = initJobSet;
        if(initJobSet == null)
            throw new RuntimeException("Init job set cannot be null");
        this.cachePool = cachePool;
        this.metrics = new YammerMetrics();
        this.cacheReloadTimerList = new ArrayList<Timer>();
        this.cacheReloadTimerTaskList = new ArrayList<TimerTask>();

        workflowLogger.debug("Cache Pool assigned to workflow.");
        Map<String,ICache> cacheMap = this.cachePool.getCacheMap();


        for(Map.Entry<String,ICache> entry: cacheMap.entrySet())
        {
            ICache cache = entry.getValue();
            workflowLogger.debug("Checking whether cache is refreshable for first time run. : {} ",cache.getName());

            if(IRefreshable.class.isAssignableFrom(entry.getValue().getClass())){

                workflowLogger.debug("The cache is refreshable IRefreshable ,running refresh for first time for: {}",
                                      cache.getName());

                IRefreshable refreshableCache = (IRefreshable)entry.getValue();

                try
                {
                    refreshableCache.refresh();
                }
                catch(RefreshException re)
                {
                    workflowLogger.error("Exception in refreshable cache first run ", re);
                    throw new RuntimeException("Exception in refreshable cache first run ",re);
                }
            }
        }

        workflowLogger.debug("Going to run timer task for each of the refreshable cache.Total Cache map size is: {}",
                              String.valueOf(cacheMap.size()));

        for(Map.Entry<String,ICache> entry: cacheMap.entrySet())
        {

            if(IRefreshable.class.isAssignableFrom(entry.getValue().getClass()))
            {
                workflowLogger.debug("The cache is refreshable, assigning refresh method to timer thread.");
                IRefreshable refreshableCache = (IRefreshable)entry.getValue();
                CacheReloadTimerTask timerTask = new CacheReloadTimerTask(workflowLogger,refreshableCache,refreshableCache.getName());
                Timer timer = new Timer("Timer-" + refreshableCache.getName());
                timer.schedule(timerTask,0,refreshableCache.getRefreshInterval());
                this.cacheReloadTimerList.add(timer);
                workflowLogger.debug("Created and added timer task to list");
                this.cacheReloadTimerTaskList.add(timerTask);
                workflowLogger.debug("Created and added timer to list");
            }

        }

        this.useMacAddressImplForUUID = useMacAddressImplForUUID;
    }

    public void executeRequest(HttpServletRequest request, HttpServletResponse response)
    {
        Context context = new Context(this.useMacAddressImplForUUID);
        context.setValue(CONTEXT_REQUEST_KEY, request);
        context.setValue(CONTEXT_RESPONSE_KEY, response);

        metrics.incrementTotalInvocations();
        long beginTime = System.nanoTime();

        JobSet currentJobSet = initJobSet;

        workflowLogger.debug("Starting job set: {} ", currentJobSet.getName());

        while(currentJobSet != null)
        {
            boolean successCode = currentJobSet.execute(context, metrics);

            workflowLogger.debug("The current job set {} executed with success code as : {} ",currentJobSet.getName(), successCode);

            if(!successCode)
                metrics.incrementTotalFailures();

            if(!successCode)
                currentJobSet = currentJobSet.getErrorJobSet();
            else
                currentJobSet = currentJobSet.getNextJob(context);
        }

        long endTime = System.nanoTime();
        // Update latency to the nearest microsecond.
        metrics.incrementTotalLatency((endTime - beginTime + 500) / 1000);
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

    /**
     * This method returns stats of each of the jobs
     * @return gives a map with job invocations, latency in nanoseconds and failures
     */
    public String getJobStats() {
        SortedMap<String, Counter> jobCounters = metrics.getMetricRegistry().getCounters(new MetricFilter() {
            @Override
            public boolean matches(String s, Metric metric) {
                return s != null && !s.isEmpty() && s.indexOf(':') != -1;
            }
        });

        Map<String, Map<String, Long>> jobStatsMap = new HashMap<String, Map<String, Long>>();
        for(Map.Entry<String, Counter> entry : jobCounters.entrySet()) {
            String name = entry.getKey();
            Counter value = entry.getValue();

            String[] tokens = name.split(":");
            if(tokens.length < 2) continue;
            String jobName = tokens[0];
            String statName = tokens[1];
            Map<String, Long> statsMap = jobStatsMap.get(jobName);
            if(statsMap == null) {
                statsMap = new HashMap<String, Long>();
                jobStatsMap.put(jobName, statsMap);
            }
            statsMap.put(statName, value.getCount());
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(jobStatsMap);
        } catch (IOException ioe) {
            // Do nothing
        }

        return "";
    }
}
