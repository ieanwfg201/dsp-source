package com.kritter.common_caches.pmp;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * This class keeps a third party connection and its DSP mapping database.
 */
public class ThirdPartyConnectionDSPMappingCache extends AbstractDBStatsReloadableQueryableCache<Integer, ThirdPartyConnectionDSPMappingEntity>
{
    private static final Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    public ThirdPartyConnectionDSPMappingCache(List<Class> secIndexKeyClassList,
                                               Properties props,
                                               DatabaseManager dbMgr,
                                               String cacheName) throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    protected ThirdPartyConnectionDSPMappingEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer  id = null;

        try
        {
            id = resultSet.getInt("id");
            String thirdPartyConnId = resultSet.getString("third_party_conn_id");
            String dspId = resultSet.getString("dsp_id");
            Timestamp lastModified = resultSet.getTimestamp("last_modified");

            return new ThirdPartyConnectionDSPMappingEntity(id,thirdPartyConnId,dspId,lastModified);
        }
        catch (SQLException sqle)
        {
            addToErrorMap(id, "SQLException while processing ThirdPartyConnectionDSPMappingEntity");
            logger.error("SQLException thrown while processing ThirdPartyConnectionDSPMappingEntity Entry",sqle);
            throw new RefreshException("SQLException thrown while processing ThirdPartyConnectionDSPMappingEntity Entry",sqle);
        }


    }

    @Override
    protected void release() throws ProcessingException
    {
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, ThirdPartyConnectionDSPMappingEntity entity)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return this.name;
    }
}
