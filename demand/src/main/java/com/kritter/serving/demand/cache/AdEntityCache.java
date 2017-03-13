package com.kritter.serving.demand.cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.constants.InclusionExclusionType;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.constants.StatusIdEnum;
import com.kritter.entity.ad_ext.AdExt;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.targeting_profile.column.Retargeting;
import com.kritter.entity.targeting_profile.column.TPExt;
import com.kritter.constants.MarketPlace;
import com.kritter.serving.demand.indexbuilder.AdEntitySecondaryIndexBuilder;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.dbextractionutil.ResultSetHelper;
import com.kritter.utils.entity.TargetingProfileLocationEntity;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.ResultSet;
import java.util.*;

/**
 * This cache is responsible for loading/unloading Ad entities periodically along with targeting
 * profile it is attached to.
 */
public class AdEntityCache extends AbstractDBStatsReloadableQueryableCache<Integer, AdEntity>
{
    private static Logger logger = LogManager.getLogger("cache.logger");
    @Getter private final String name;

    public AdEntityCache(List<Class> secIndexKeyClassList,Properties props,
                         DatabaseManager dbMgr, String cacheName)
            throws InitializationException
    {
        super(secIndexKeyClassList, logger, props, dbMgr);
        this.name = cacheName;
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class className, AdEntity entity)
    {
        return AdEntitySecondaryIndexBuilder.getIndex(className,entity);
    }

    @Override
    protected AdEntity buildEntity(ResultSet resultSet) throws RefreshException
    {
        Integer id = null;
        try
        {
            id = resultSet.getInt("id");
            String guid = resultSet.getString("guid");
            Integer creativeId = resultSet.getInt("creative_id");
            String creativeGuid = resultSet.getString("creative_guid");
            String landingUrl = resultSet.getString("landing_url");
            Integer campaignId = resultSet.getInt("campaign_id");
            String campaignGuid = resultSet.getString("campaign_guid");

            Short[] categoriesArray = ResultSetHelper.getTier1Tier2CategoriesUnion(resultSet,
                                                                                   "ad_categories_list",
                                                                                   logger);
            Short[] hygieneArray = ResultSetHelper.getResultSetShortArray(resultSet, "hygiene_list");
            Integer status = resultSet.getInt("status_id");
            Integer marketplaceId = resultSet.getInt("marketplace_id");
            Double bid = resultSet.getDouble("internal_max_bid");
            Double advertiserBid = resultSet.getDouble("advertiser_bid");
            Integer trackingPartnerId = resultSet.getInt("tracking_partner");
            Long lastModified = resultSet.getTimestamp("ad_last_modified").getTime();

            //now get targeting profile data.
            Integer targetingId = resultSet.getInt("targeting_profile_id");
            String targetingGuid = resultSet.getString("targeting_profile_guid");
            String accountId = resultSet.getString("account_guid");
            Integer accountIncId = resultSet.getInt("account_id");
            String accountGuid = resultSet.getString("account_guid");
            Integer[] targetedHandsetManufacturers = ResultSetHelper.getResultSetIntegerArray(resultSet,"brand_list");
            Integer[] targetedHandsetModels = ResultSetHelper.getResultSetIntegerArray(resultSet,"model_list");
            String targetedOsJson = resultSet.getString("os_json");
            String browserJson = resultSet.getString("browser_json");

            TargetingProfileLocationEntity targetedCountries = new TargetingProfileLocationEntity();
            targetedCountries.readTargetingProfileLocationJsonIntoDataMap(resultSet.getString("country_json"));

            TargetingProfileLocationEntity targetedCarriers = new TargetingProfileLocationEntity();
            targetedCarriers.readTargetingProfileLocationJsonIntoDataMap(resultSet.getString("carrier_json"));

            TargetingProfileLocationEntity targetedStates = new TargetingProfileLocationEntity();
            targetedStates.readTargetingProfileLocationJsonIntoDataMap(resultSet.getString("state_json"));

            try
            {
                targetedStates.readTargetingProfileLocationJsonIntoDataMap(resultSet.getString("state_json"));
            }
            catch (Exception e)
            {
                logger.error("Exception in reading state json for adid: {} ", id,e);
            }

            TargetingProfileLocationEntity targetedCities = new TargetingProfileLocationEntity();
            
            try
            {
                targetedCities.readTargetingProfileLocationJsonIntoDataMap(resultSet.getString("city_json"));
            }
            catch (Exception e)
            {
                logger.error("Exception in reading city json for adid: {} ", id,e);
            }
            targetedCities.readTargetingProfileLocationJsonIntoDataMap(resultSet.getString("city_json"));

            //TODO change zipcode as file list ids and then check for matching.
            Integer[] targetedZipCodes= null;//ResultSetHelper.getResultSetIntegerArray(resultSet,"zipcode_list");

            Short[] categoriesInclusionList = null;
            Short[] categoriesExclusionList = null;
            Short[] categoriesList = ResultSetHelper.
                    getTier1Tier2CategoriesUnion(resultSet, "targeting_profile_category_list", logger);

            Boolean isCategoryListExcluded = resultSet.getBoolean("is_category_list_excluded");

            if(!isCategoryListExcluded)
                categoriesInclusionList = categoriesList;
            else
                categoriesExclusionList = categoriesList;

            String[] customIpFileIdArray = ResultSetHelper.getResultSetStringArray(resultSet,"custom_ip_file_id_set");
            String[] zipCodeFileIdArray = ResultSetHelper.getResultSetStringArray(resultSet, "zipcode_file_id_set");
            String[] latLonFileIdArray = ResultSetHelper.getResultSetStringArray(resultSet, "lat_lon_radius_file");

            Short supplySourceType = resultSet.getShort("supply_source_type");
            Short supplySource = resultSet.getShort("supply_source");
            Short[] hoursOfDayTargetedArray = ResultSetHelper.getResultSetShortArray(resultSet, "hours_list");

            Set<Short> hoursOfDayTargeted = null;
            if(null != hoursOfDayTargetedArray)
            {
                hoursOfDayTargeted = new HashSet<Short>(Arrays.asList(hoursOfDayTargetedArray));
            }

            Short midpTargeted = resultSet.getShort("midp");
            String latLongSerializedArray = resultSet.getString("lat_long");
            Long profileLastModified = resultSet.getTimestamp("profile_last_modified").getTime();
            Double cpaGoal = resultSet.getDouble("cpa_goal");
            String[] advertiserDomains = ResultSetHelper.getResultSetStringArray(resultSet, "adv_domain");

            boolean isSiteListExcluded = resultSet.getBoolean("is_site_list_excluded");
            /*set supply attributes inc/exc for direct and exchange supply*/
            String supplyAttributesInclusionExclusion = resultSet.getString("supply_inc_exc");
            Short[] targetedConnectionTypes = ResultSetHelper.getResultSetShortArray(resultSet, "connection_type_targeting_json");
            boolean tabletTargeting = resultSet.getBoolean("tablet_targeting");
            boolean isFrequencyCapped = resultSet.getBoolean("is_frequency_capped");
            int maxCap = resultSet.getInt("frequency_cap");
            int timeWindowInHours = resultSet.getInt("time_window");
            int demandtype = resultSet.getInt("demandtype");
            int qps = resultSet.getInt("qps");
            boolean isRetargeted = false;
            String retargeting = resultSet.getString("retargeting");
            String pmpDealIdJson = resultSet.getString("pmp_deal_json");
            Short[] deviceTypeArray = ResultSetHelper.getResultSetShortArray(resultSet, "device_type");
            int userIdInclusionExclusion = resultSet.getInt("user_id_inc_exc");
            int impressionCap = resultSet.getInt("impression_cap");
            int impressionsAccrued = resultSet.getInt("impressions_accrued");
            int bidtype = resultSet.getInt("bidtype");
            String external_tracker = resultSet.getString("external_tracker");
            String frequencyCapStr = resultSet.getString("freqcap_json");
            int protocol = resultSet.getInt("protocol");
            ExtTracker extTracker = null;
            if(external_tracker != null && !"".equals(external_tracker.trim())){
                extTracker= ExtTracker.getObject(external_tracker.trim());
                if(extTracker != null){
                    if((extTracker.getImpTracker() == null || extTracker.getImpTracker().size()< 1) &&
                    		(extTracker.getClickTracker() == null || extTracker.getClickTracker().size()< 1)){
                        extTracker=null;
                    }
                }
            }
            String extStr = resultSet.getString("ext");
            AdExt adExt = null;
            if(extStr != null && !"".equals(extStr.trim())){
            	adExt =  AdExt.getObject(extStr.trim());
            }
            String targetingExtStr = resultSet.getString("targetingExt");
            TPExt targetingExt = null;
            if(targetingExtStr != null && !"".equals(targetingExtStr.trim())){
            	targetingExt =  TPExt.getObject(targetingExtStr.trim());
            }
            int lat_lon_radius_unit = resultSet.getInt("lat_lon_radius_unit");
            String audienceTags = resultSet.getString("audience_tags");

            TargetingProfile.TargetingBuilder targetingBuilder = new
                TargetingProfile.TargetingBuilder(targetingId, targetingGuid, accountId, false, profileLastModified);
            targetingBuilder.setTargetedBrands(targetedHandsetManufacturers);
            targetingBuilder.setTargetedModels(targetedHandsetModels);
            targetingBuilder.setTargetedOSJson(targetedOsJson);
            targetingBuilder.setTargetedBrowserJson(browserJson);
            targetingBuilder.setTargetedCountries(targetedCountries);
            targetingBuilder.setTargetedCarriers(targetedCarriers);
            targetingBuilder.setTargetedStates(targetedStates);
            targetingBuilder.setTargetedCities(targetedCities);
            targetingBuilder.setTargetedZipcodes(targetedZipCodes);

            targetingBuilder.setCategoriesInclusionList(categoriesInclusionList);
            targetingBuilder.setCategoriesExclusionList(categoriesExclusionList);
            targetingBuilder.setCustomIpFileIdArray(customIpFileIdArray);
            targetingBuilder.setZipCodeFileIdArray(zipCodeFileIdArray);
            targetingBuilder.setSupplySourceType(supplySourceType);
            targetingBuilder.setSupplySource(supplySource);
            targetingBuilder.setHoursOfDayTargeted(hoursOfDayTargeted);
            targetingBuilder.setMidpValue(midpTargeted);
            targetingBuilder.setLatitudeLongitudeRadius(latLongSerializedArray);
            targetingBuilder.setIsSiteListExcluded(isSiteListExcluded);
            targetingBuilder.setExchangePublisherWithSiteSetForInclusionExclusionMap
                    (supplyAttributesInclusionExclusion, targetingGuid);
            targetingBuilder.setTargetedConnectionTypes(targetedConnectionTypes);
            targetingBuilder.setTabletTargeting(tabletTargeting);
            targetingBuilder.setExchangeSpecificPMPDealIdInfo(pmpDealIdJson, targetingGuid);
            targetingBuilder.setDeviceTypeTargetingArray(deviceTypeArray);
            targetingBuilder.setTPExt(targetingExt);
            targetingBuilder.setLatLonFileIdArray(latLonFileIdArray);
            targetingBuilder.setUserIdInclusionExclusionType(InclusionExclusionType.getEnum(userIdInclusionExclusion));
            targetingBuilder.setLatLonRadiusUnit(lat_lon_radius_unit);
            targetingBuilder.setAudienceTags(audienceTags);

            if(retargeting != null){
                String tmp_retargeting = retargeting.trim();
                if(!"".equals(tmp_retargeting)){
					Retargeting ret = Retargeting.getObject(tmp_retargeting);
                    targetingBuilder.setRetargeting(ret);
                    if(ret != null && ret.getSegment() != null && ret.getSegment().size()>0){
                        isRetargeted = true;
                    }
                }
            }

            boolean isMarkedForDeletion = false;
            if(!(status == StatusIdEnum.Active.getCode()))
                isMarkedForDeletion = true;

            // If the impression cap has already been hit, mark the ad for deletion
            if(impressionCap != 0 && impressionsAccrued >= impressionCap)
                isMarkedForDeletion = true;

            return new AdEntity.AdEntityBuilder(
                                                id, guid, creativeId,creativeGuid, campaignId,
                                                campaignGuid,categoriesArray,hygieneArray,
                                                targetingBuilder.build(),
                                                MarketPlace.getMarketPlace(marketplaceId),
                                                bid,advertiserBid,isMarkedForDeletion,lastModified,
                                                isFrequencyCapped, maxCap, timeWindowInHours,
                                                demandtype, qps, accountGuid, bidtype, extTracker, isRetargeted,
                                                protocol
                                               )
                                               .setLandingUrl(landingUrl)
                                               .setAccountId(accountIncId)
                                               .setTrackingPartnerId(trackingPartnerId)
                                               .setCpaGoal(cpaGoal)
                                               .setAdvertiserDomain(advertiserDomains)
                                               .setAdExt(adExt)
                                               .setFrequencyCap(frequencyCapStr)
                                               .build();
        }
        catch(Exception e)
        {
            addToErrorMap(id, "Exception while processing AdEntityCache entry: " + id);
            logger.error("Exception thrown while processing AdEntityCache Entry",e);
            throw new RefreshException("Exception thrown while processing AdEntityCache Entry "+id, e);
        }
    }

    @Override
    protected void release() throws ProcessingException
    {

    }
}
