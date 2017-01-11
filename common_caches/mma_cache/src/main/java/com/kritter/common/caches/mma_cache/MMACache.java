package com.kritter.common.caches.mma_cache;


import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.mma_cache.entity.MMACacheEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * This class keeps mma cache entities
 */
public class MMACache extends AbstractDBStatsReloadableQueryableCache<String, MMACacheEntity>
{
    private static final String CTRL_A = String.valueOf((char)1);
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public MMACache(List<Class> secIndexKeyClassList, Properties props,
                               DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected MMACacheEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        String supplycode = null;
        Integer ui_id = null;
        try
        {
            id = resultSet.getInt("id");
            supplycode = resultSet.getString("supplycode");
            ui_id = resultSet.getInt("ui_id");
            boolean isMarkedForDeletion = false;
            Long lastModified = resultSet.getTimestamp("last_modified").getTime();

            MMACacheEntity vice=this.query(id+CTRL_A+supplycode);
            if(vice!=null){
                vice.getUi_id().add(ui_id);
            }else{
                vice = new MMACacheEntity(id, supplycode, new HashSet<Integer>(ui_id),
                        lastModified, isMarkedForDeletion);
            }
            return vice;
        } catch(Exception e) {
            addToErrorMap(supplycode+"id", "Exception while processing MMACacheEntity entry: " + id);
            logger.error("Exception thrown while processing MMACacheEntity Entry. ", e);
            throw new RefreshException("Exception thrown while processing MMACacheEntity Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, MMACacheEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
