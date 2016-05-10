package com.kritter.abstraction.cache.utils;

import com.kritter.abstraction.cache.interfaces.IStats;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Date: 11-June-2013<br></br>
 * Class: Common utility to print stats. Each implementer is advised to set defaults for non-applicable stats
 */
public class StatsDumper
{
    public static String getStatsJSON(IStats stats)
    {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("construction_time", stats.getConstructionTime());
        jsonObj.put("last_refresh_time",stats.getLastRefreshStartTime());
        jsonObj.put("last_successful_refresh_time",stats.getLastSuccessfulRefreshTime());
        jsonObj.put("last_failed_refresh_time",stats.getLastFailedRefreshTime());
        jsonObj.put("no_of_refreshes",stats.getNoOfRefreshes());
        jsonObj.put("no_of_failed_refreshes",stats.getNoOfFailures());
        jsonObj.put("no_of_successful_refreshes",stats.getNoOfSuccesses());
        jsonObj.put("entity_count",stats.getEntityCount());
        jsonObj.put("secondary_index_count",stats.getSecondaryIndexCount());
        jsonObj.put("failed_entity_count",stats.getFailedEntityCount());
        jsonObj.put("error_messages",stats.getErrorMessages());

        return jsonObj.toString();
    }
}
