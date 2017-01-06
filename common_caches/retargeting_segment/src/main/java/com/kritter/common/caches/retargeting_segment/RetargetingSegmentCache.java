package com.kritter.common.caches.retargeting_segment;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.common.caches.retargeting_segment.entity.RetargetingSegmentEntity;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**

 */
public class RetargetingSegmentCache extends AbstractDBStatsReloadableQueryableCache<Integer, RetargetingSegmentEntity>{
    private static Logger logger = LogManager.getLogger("cache.logger");
    private final String name;

    public RetargetingSegmentCache(List<Class> secIndexKeyClassList,
                                     Properties props,
                                     DatabaseManager dbMgr,
                                     String cacheName) throws InitializationException{
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className,
            RetargetingSegmentEntity entity){
        return null;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    protected RetargetingSegmentEntity buildEntity(ResultSet resultSet) throws RefreshException{
        int id = -1;
        try{
            id = resultSet.getInt("id");
            String account_guid = resultSet.getString("account_guid");
            Timestamp last_modified = resultSet.getTimestamp("last_modified");
            return new RetargetingSegmentEntity(id, account_guid, last_modified);
        }
        catch (SQLException e){
            addToErrorMap(id, "SQLException while processing RetargetingSegmentEntity ");
            logger.error("SQLException thrown while processing RetargetingSegmentEntity Entry",e);
            throw new RefreshException("SQLException thrown while processing RetargetingSegmentEntity Entry",e);
        }catch (IOException e)
        {
            addToErrorMap(id, "IOException while processing RetargetingSegmentEntity ");
            logger.error("IOException thrown while processing RetargetingSegmentEntity Entry",e);
            throw new RefreshException("IOException thrown while processing RetargetingSegmentEntity Entry",e);
        }
    }

    @Override
    protected void release() throws ProcessingException{
    }
}
