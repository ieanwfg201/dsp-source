package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * Date: 9/6/13
 * Class:
 */
public class SampleQueryableCache extends AbstractQueryableCache<Integer, SampleQueryableEntity>
{

    public SampleQueryableCache(List<Class> secIndexKeyClassList, Logger log) throws InitializationException
    {
        super(secIndexKeyClassList, log);
    }

    @Override
    public void cleanUp() throws ProcessingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, SampleQueryableEntity entity)
    {
        if(className.equals(SampleSecondaryIndex.class))
        {
            return SampleSecondaryIndexBuilder.getSecIndexSet(entity);
        }

        if(className.equals(SampleSecondaryIndex2.class))
        {
            return SampleSecondaryIndexBuilder.getSecIndexSet2(entity);
        }
        return null;
    }

    @Override
    public String getName() {
        return "SampleQueryableCache";  //To change body of implemented methods use File | Settings | File Templates.
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
    public String queryErrorMessage(Integer entityId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
