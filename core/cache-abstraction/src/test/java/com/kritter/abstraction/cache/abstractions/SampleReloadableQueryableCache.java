package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Date: 10/6/13
 * Class:
 */
@Getter
public class SampleReloadableQueryableCache extends AbstractStatsReloadableQueryableCache<Integer, SampleQueryableEntity>
{
    private int counter = 0;

    public SampleReloadableQueryableCache(List<Class> secIndexKeyClassList, Logger log, Properties props) throws InitializationException {
        super(secIndexKeyClassList, log, props);
    }

    @Override
    public void refreshEntities() throws RefreshException
    {
        counter++;
    }

    @Override
    public String getName() {
        return "SampleStatsReloadableCache";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getLastRefreshStartTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getLastSuccessfulRefreshTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getLastFailedRefreshTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNoOfRefreshes() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNoOfFailures() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNoOfSuccesses() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSecondaryIndexCount() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, SampleQueryableEntity entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getFailedEntityCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getErrorMessages() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getStats() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void clean() throws ProcessingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
