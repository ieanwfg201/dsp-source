package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Date: 11-06-2013
 * Class:
 */
public class SampleFileStatsReloadableQueryableCache extends AbstractFileStatsReloadableQueryableCache<Integer, SampleQueryableUpdatableEntity>
{
    public boolean returnFixed = false;
    public boolean addToError = false;

    public SampleFileStatsReloadableQueryableCache(List<Class> secIndexKeyClassList, Logger log, Properties props) throws InitializationException
    {
        super(secIndexKeyClassList, log, props);
    }

    @Override
    protected SampleQueryableUpdatableEntity buildEntity(String line) throws RefreshException
    {
        if(addToError)
        {
            this.addToErrorMap(420, "Naughty boy");
            return null;
        }
        if(returnFixed)
            return new SampleQueryableUpdatableEntity(100, new Date().getTime(), 1000, false, new Date().getTime());

        String args[] = line.split(",");
        return new SampleQueryableUpdatableEntity(Integer.parseInt(args[0]), Long.parseLong(args[1]), Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]), Long.parseLong(args[4]));
    }

    @Override
    protected SampleQueryableUpdatableEntity release() throws ProcessingException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
