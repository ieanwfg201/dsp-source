package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Date: 9-June-2013<br></br>
 * Class: Abstraction for a File entity-based cache<br></br>
 * If the file has been modified after the last refresh began, entities from the new file are loaded in the cache<br></br>
 * <ul><b>Expectation from the concrete implementation:</b>
 *  <li>refresh_interval and file name should be provided in the Properties configuration under the keys 'refresh_interval' and 'file_name'</li>
 *  <li>Implementation of buildEntity should simply build an entity from the line and NEVER iterate through the file</li>
 *  <li>During a refresh, the entity has to be marked for deletion if the entity's indices have to be removed from the ICache</li>
 *  <li>If a entity data is inconsistent, add its details to the error map and return null from buildEntity</li>
 *  <li>Entities should be versioned. If not, set 'is_version_maintained' to false in Properties</li>
 *  <li>The secondary index key list provided should contain the Secondary index class names and not any wrappers. Same would be used while querying</li>
 *  <li>For each secondary index, an index builder should provide a ISecondaryIndexWrapper per entity</li>
 * </ul>
 */
public abstract class AbstractFileStatsReloadableQueryableCache<I, E extends IUpdatableEntity<I>> extends AbstractStatsReloadableQueryableCache<I,E>
{
    private final String fileName;
    private Long lastRefreshStartTime = null;

    public AbstractFileStatsReloadableQueryableCache(List<Class> secIndexKeyClassList, Logger log, Properties props) throws InitializationException
    {
        super(secIndexKeyClassList, log, props);
        fileName = props.getProperty("file_name");
        if(StringUtils.isEmpty(fileName))
            throw new InitializationException("\"file_name\" key is missing in properties for cache");

        if(!(new File(fileName).exists()))
            throw new InitializationException("At location: " + fileName + ", there is no file present");
    }

    /**
     * This method reads the file if it has been modified since the last time we checked.
     * The concrete child class is expected to build entities out of each line of the file or return null
     * in error cases. Error map could be used for saving info on failed/inconsistent entities.
     * @throws RefreshException
     */
    @Override
    protected void refreshEntities() throws RefreshException
    {
        File file = new File(fileName);
        long fileModifyTime = file.lastModified();
        if(lastRefreshStartTime == null || lastRefreshStartTime < fileModifyTime)
        {
            BufferedReader br = null;
            try
            {
                Long startTime = new Date().getTime();
                br = new BufferedReader(new FileReader(file));
                String line;
                while((line = br.readLine()) != null)
                {
                    E entity = buildEntity(line);
                    // if the derived class added this to error map
                    if(entity == null)
                        continue;

                    // Check if the entity is updated for addition or removal
                    if(!entity.isMarkedForDeletion())
                        this.add(entity);
                    else
                        this.remove(entity.getId());
                    this.removeFromErrorMap(entity.getId());
                }
                lastRefreshStartTime = startTime;
            }
            catch (IOException ioExcp)
            {
                throw new RefreshException("IOException thrown while running file based refresh in cache: " + this.getName(), ioExcp);
            }
            finally
            {
                if(br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch (IOException ioExcp)
                    {
                        throw new RefreshException("IOException thrown while closing file handle during refresh in cache: " + this.getName(), ioExcp);
                    }
                }
            }
        }
    }

    @Override
    protected void clean() throws ProcessingException
    {
        // Nothing to clean up but provides the child an option to clean itself
        release();
    }

    protected abstract E buildEntity(String line) throws RefreshException;
    protected abstract E release() throws ProcessingException;
}
