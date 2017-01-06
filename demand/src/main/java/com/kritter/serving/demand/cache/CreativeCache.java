package com.kritter.serving.demand.cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.constants.CreativeFormat;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.native_props.demand.NativeDemandProps;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * This cache is responsible for loading/unloading Creative entities periodically to the in-memory Cache
 */
public class CreativeCache extends AbstractDBStatsReloadableQueryableCache<Integer,Creative>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    @Getter private final String name;

    public CreativeCache(List<Class> secIndexKeyClassList, Properties props,
                         DatabaseManager dbMgr, String cacheName)
                     throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className,Creative entity)
    {
        return null;
    }

    @Override
    protected Creative buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        try
        {
            id = resultSet.getInt("id");
            String guid = resultSet.getString("guid");
            String accountId = resultSet.getString("account_guid");
            Integer formatId = resultSet.getInt("format_id");
            Short[] creativeAttributes = ResultSetHelper.getResultSetShortArray(resultSet,"creative_attr");
            String text = resultSet.getString("text");
            Integer[] bannerUriIds = ResultSetHelper.getResultSetIntegerArray(resultSet,"resource_uri_ids");
            String htmlContent = resultSet.getString("html_content");
            String externalResourceURL = resultSet.getString("ext_resource_url");
            boolean isMarkedForDeletion = false;
            String native_demand_props = resultSet.getString("native_demand_props");
            NativeDemandProps ndp = null;
            if(native_demand_props != null){
                String native_demand_props_trim = native_demand_props.trim();
                if(!"".equals(native_demand_props_trim)){
                    try{
                        ndp = NativeDemandProps.getObject(native_demand_props_trim);
                    }catch(Exception e){
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            String creative_macro = resultSet.getString("creative_macro");
            CreativeMacro creativeMacro = null;

            if(creative_macro != null)
            {
                String creative_macro_trim = creative_macro.trim();
                if(!"".equals(creative_macro_trim))
                {
                    try
                    {
                        creativeMacro = CreativeMacro.getObject(creative_macro_trim);
                    }
                    catch(Exception e)
                    {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            String video_props = resultSet.getString("video_props");
            VideoProps videoProps = null;

            if(video_props != null)
            {
                String video_props_trim = video_props.trim();
                if(!"".equals(video_props_trim))
                {
                    try
                    {
                        videoProps = VideoProps.getObject(video_props_trim);
                    }
                    catch(Exception e)
                    {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            Integer[] slotIdsFromCreativeContainer = ResultSetHelper.getResultSetIntegerArray(resultSet,"slot_info");

            Long lastModified = resultSet.getTimestamp("last_modified").getTime();
            return new Creative.CreativeEntityBuilder(id, guid, accountId,
                                                      CreativeFormat.getCreativeFormats(formatId),
                                                      creativeAttributes,
                                                      isMarkedForDeletion, lastModified, ndp,
                                                      creativeMacro, videoProps)
                                                      .setHtmlContent(htmlContent)
                                                      .setText(text)
                                                      .setBannerUriIds(bannerUriIds)
                                                      .setExternalResourceURL(externalResourceURL)
                                                      .setSlotInfo(slotIdsFromCreativeContainer)
                                                      .build();
        }
        catch(Exception e)
        {
            addToErrorMap(id, "Exception while processing CreativeCache entry: " + id);
            logger.error("Exception thrown while processing CreativeCache Entry. ", e);
            throw new RefreshException("Exception thrown while processing CreativeCache Entry", e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {}
}
