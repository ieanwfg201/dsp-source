package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;

import java.util.Properties;

/**
 * Date: 10/6/13
 * Class:
 */
@Getter
public class SampleStatsReloadableCache extends AbstractStatsReloadableCache
{
    private int counter = 0;
    public boolean throwException = false;

    public SampleStatsReloadableCache(Logger log, Properties props) throws InitializationException {
        super(log, props);
    }

    @Override
    protected void cleanUp() throws ProcessingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void refreshCache() throws RefreshException
    {
        if(throwException)
            throw new RefreshException("Deliberately thrown exception");
        counter++;
    }

    /*
        Example usage for a derived class that wants to enhance/add more stats
     */
    @Override
    public String getStats()
    {
        String parentStats = super.getStats();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("counter", counter);
        jsonObject.put("parent_stats", parentStats);
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName()
    {
        return "SampleStatsReloadableCache";
    }
}
