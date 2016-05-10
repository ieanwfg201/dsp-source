package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.slf4j.Logger;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Date: 10/6/13
 * Class:
 */
public class SampleStatsReloadableQueryableCache extends AbstractStatsReloadableQueryableCache<Integer, SampleQueryableEntity>
{
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private int testCounter = 0;
    public boolean throwException = false;

    public SampleStatsReloadableQueryableCache(List<Class> secIndexKeyClassList, Logger log, Properties props) throws InitializationException {
        super(secIndexKeyClassList, log, props);
    }

    public int getTestCounter()
    {
        try
        {
            rwLock.readLock().lock();
            return testCounter;
        }
        finally {
            rwLock.readLock().unlock();
        }

    }

    @Override
    protected void refreshEntities() throws RefreshException
    {
        if(rwLock == null)
            return;
        try
        {
            rwLock.writeLock().lock();
            ++testCounter;
        }
        finally {
            rwLock.writeLock().unlock();
        }
        if(throwException)
            throw new RefreshException("RefreshException thrown deliberately");
    }

    @Override
    protected void clean() throws ProcessingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, SampleQueryableEntity entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return "SampleStatsReloadableQueryableCache";
    }
}
