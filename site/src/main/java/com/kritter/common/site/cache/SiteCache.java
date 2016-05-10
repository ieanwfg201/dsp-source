package com.kritter.common.site.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.constants.StatusIdEnum;
import com.kritter.entity.native_props.NativeProps;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import org.slf4j.Logger;
import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.common.site.entity.SecondaryIndexBuilder;
import com.kritter.common.site.entity.Site;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.LoggerFactory;

/**
 * This class keeps site entities in memory. The repository initialization may
 * happen to incorporate key of choice.
 * 
 */
public class SiteCache extends AbstractDBStatsReloadableQueryableCache<String, Site>
{
    private static Logger logger = LoggerFactory.getLogger("cache.logger");
	private final String name;

	public SiteCache(List<Class> secIndexKeyClassList,
                     Properties props,
                     DatabaseManager dbMgr,
                     String cacheName) throws InitializationException
    {
		super(secIndexKeyClassList, logger, props, dbMgr);
		this.name = cacheName;
	}

	@Override
	public ISecondaryIndexWrapper getSecondaryIndexKey(Class className,
                                                       Site entity)
    {
		return SecondaryIndexBuilder.getSecondaryIndexKey(className,entity);
	}

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
	protected Site buildEntity(ResultSet resultSet) throws RefreshException
    {
        String siteId = null;
		try
        {
			Integer id = resultSet.getInt("id");
			siteId = resultSet.getString("guid");
			String name = resultSet.getString("name");
            Integer publisherIncId = resultSet.getInt("pub_id");
			String publisherId = resultSet.getString("pub_guid");
			String siteUrl = resultSet.getString("site_url");
            String applicationId = resultSet.getString("app_id");
            Short appStoreId = resultSet.getShort("app_store_id");
            Short[] categoriesArray = ResultSetHelper.getTier1Tier2CategoriesUnion(resultSet, "categories_list",logger);
            Short[] categoriesArrayIncExc = ResultSetHelper.getTier1Tier2CategoriesUnion(resultSet, "category_list_inc_exc",logger);
            boolean isCategoryListExcluded = resultSet.getBoolean("is_category_list_excluded");
            Short[] hygieneList = ResultSetHelper.getResultSetShortArray(resultSet, "hygiene_list");
            Short[] optInHygieneList = ResultSetHelper.getResultSetShortArray(resultSet, "opt_in_hygiene_list");
            Short[] creativeAttributesIncExc = ResultSetHelper.
                                                       getResultSetShortArray(resultSet,"creative_attr_inc_exc");
            boolean isCreativeAttributesForExclusion = resultSet.getBoolean("is_creative_attr_exc");
			Short sitePlatform = resultSet.getShort("site_platform_id");
			Short status = resultSet.getShort("status_id");
            String[] adDomainsToExclude = ResultSetHelper.getResultSetStringArray(resultSet, "url_exclusion");
            double ecpmFloorValue = resultSet.getDouble("floor");
            boolean isAdvertiserIdListExcluded = resultSet.getBoolean("is_advertiser_excluded");
            String campaignInclusionExclusionSchemaMap = resultSet.getString("campaign_inc_exc_schema");
            String nofillBackupContent = resultSet.getString("nofill_backup_content");
            short sitePassbackType = resultSet.getShort("passback_type");
            boolean isRichMediaAllowed = resultSet.getBoolean("is_richmedia_allowed");
            boolean is_native = resultSet.getBoolean("is_native");
            String native_props = resultSet.getString("native_props");
            NativeProps nativeProps = null;
            if(is_native && native_props != null){
                String native_props_trim = native_props.trim();
                if(!"".equals(native_props_trim)){
                    try{
                        nativeProps = NativeProps.getObject(native_props_trim);
                    }catch(Exception e){
                    
                    }
                }
            }

            boolean excludeDefinedAdDomains = true; //!resultSet.getBoolean("allow_house_ads");
			Timestamp lastModifiedOn = resultSet.getTimestamp("last_modified");

			boolean isMarkedForDeletion = false;
			if (!(status == StatusIdEnum.Active.getCode()))
				isMarkedForDeletion = true;

			return new Site.SiteEntityBuilder(
                                              id, siteId, name, publisherIncId, publisherId,siteUrl,
                                              categoriesArray,categoriesArrayIncExc,isCategoryListExcluded,
                                              hygieneList,optInHygieneList,creativeAttributesIncExc,
                                              isCreativeAttributesForExclusion,sitePlatform, status,
                                              adDomainsToExclude,excludeDefinedAdDomains,
                                              isMarkedForDeletion,lastModifiedOn,
                                              is_native, nativeProps
                                             )
                                             .setApplicationId(applicationId).setAppStoreId(appStoreId)
                                             .setEcpmFloor(ecpmFloorValue)
                                             .setIsAdvertiserIdListExcluded(isAdvertiserIdListExcluded)
                                             .setCampaignInclusionExclusionSchemaMap(campaignInclusionExclusionSchemaMap)
                                             .setNofillBackupContent(nofillBackupContent)
                                             .setSitePassbackType(sitePassbackType)
                                             .setIsRichMediaAllowed(isRichMediaAllowed)
					                         .build();

		}
        catch (SQLException e)
        {
			addToErrorMap(siteId,"SQL exception while processing SiteCache entry: " + siteId);
			logger.error("SQLException thrown while processing SiteCache Entry",e);
			throw new RefreshException("SQLException thrown while processing SiteCache Entry",e);
		}

	}

	@Override
	protected void release() throws ProcessingException
    {
		// no data structures held. Nothing to release
	}
}
