package com.kritter.abstraction.cache.interfaces;

import java.util.Date;

/**
 * Date: 6-June-2013<br></br>
 * Class: Interface to enforce publishing of stats for each cache<br></br>
 * This has been enforced on most ICache interfaces. If some stat is not relevant to
 * your specific implementation, do not get sentimental (simply return 0s for int and "" for strings)
 * If you use the abstracted classes, you have nothing to worry about<br></br>
 */
public interface IStats
{
    public Date getConstructionTime();
    public Date getLastRefreshStartTime();
    public Date getLastSuccessfulRefreshTime();
    public Date getLastFailedRefreshTime();
    public int getNoOfRefreshes();
    public int getNoOfFailures();
    public int getNoOfSuccesses();
    public int getEntityCount();
    // entity count in JSON format against secondaryIndexKeys
    public String getSecondaryIndexCount();
    public int getFailedEntityCount();
    public String getErrorMessages();
    public String getStats();
}
