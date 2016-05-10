package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * Date: 11-06-2013
 * Class:
 */
public class SampleDBStatsReloadableQueryableCache extends AbstractDBStatsReloadableQueryableCache<Integer, SampleQueryableUpdatableEntity>
{
    private int counter = 0;
    public boolean addToError = false;

    public SampleDBStatsReloadableQueryableCache(List<Class> secIndexKeyClassList, Logger log, Properties props, DatabaseManager dbMgr) throws InitializationException
    {
        super(secIndexKeyClassList, log, props, dbMgr);
    }

    @Override
    protected SampleQueryableUpdatableEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        if(addToError)
        {
            this.addToErrorMap(420, "Naughty boy");
            return null;
        }
        return TestDBStatsReloadableQueryableCache.list.get(counter++);
    }

    @Override
    protected void release() throws ProcessingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, SampleQueryableUpdatableEntity entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
