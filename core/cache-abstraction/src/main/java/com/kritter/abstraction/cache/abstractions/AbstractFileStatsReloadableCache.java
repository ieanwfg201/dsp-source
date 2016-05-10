package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.Properties;

/**
 * Date: 9-June-2013<br></br>
 * Class: Abstraction for File based caches where the file does not expose entities but needs periodic refresh
 * <ul><b>Expectations from the concrete implementation:</b>
 *  <li>File name and refresh interval should be provided in the Properties configuration under the key 'file_name' and 'refresh_interval'</li>
 *  <li>refreshFile method implementation</li>
 *  <li>Clean up resources held, if any, in the release method</li>
 * </ul>
 */
public abstract class AbstractFileStatsReloadableCache extends AbstractStatsReloadableCache
{
    private String fileName;
    private Long lastModifiedTime = null;

    public AbstractFileStatsReloadableCache(Logger log, Properties props) throws InitializationException
    {
        super(log, props);
        fileName = props.getProperty("file_name");

        if(StringUtils.isEmpty(fileName))
            throw new InitializationException("\"file_name\" key is missing in properties for cache");

        if(!(new File(fileName).exists()))
            throw new InitializationException("At location: " + fileName + ", there is no file present");
    }

    @Override
    protected void refreshCache() throws RefreshException
    {
        File file = new File(fileName);
        long fileModifyTime = file.lastModified();
        if(lastModifiedTime == null || lastModifiedTime < fileModifyTime)
        {
            Long startTime = new Date().getTime();
            refreshFile(file);
            lastModifiedTime = startTime;
        }
    }

    @Override
    protected void cleanUp() throws ProcessingException
    {
        release();
    }

    protected abstract void refreshFile(File file) throws RefreshException;
    protected abstract void release() throws ProcessingException;
}
