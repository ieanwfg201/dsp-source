package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.File;
import java.util.Properties;

/**
 * Date: 11-06-2013
 * Class:
 */
public class SampleFileStatsReloadableCache extends AbstractFileStatsReloadableCache
{
    @Getter private int counter = 0;
    public SampleFileStatsReloadableCache(Logger log, Properties props) throws InitializationException {
        super(log, props);
    }

    @Override
    protected void refreshFile(File file) throws RefreshException
    {
        counter++;
    }

    @Override
    protected void release() throws ProcessingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
