package com.kritter.common.site.entity;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import com.kritter.constants.DemandPreference;
import com.kritter.constants.SITE_PASSBACK_TYPE;
import com.kritter.entity.native_props.NativeProps;
import com.kritter.entity.video_supply_props.VideoSupplyProps;

import lombok.Getter;
import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * This class represents a site entity in eco-system, the site could be a web
 * property requesting via an exchange, ssp, dcp or directly. The site could be
 * of pc context, mobile wap site or app.
 */
@ToString
public class Site implements IUpdatableEntity<String>
{
    @Getter
    private Integer siteIncId;
    @Getter
    private String siteGuid;
    @Getter
    private Integer publisherIncId;
    @Getter
    private String publisherId;
    @Getter
    private String name;
    @Getter
    private String siteUrl;
    @Getter
    private String applicationId;
    @Getter
    private Short appStoreId;
    @Getter
    private Short[] categoriesArray;
    @Getter @Setter
    private Short[] categoriesArrayForInclusionExclusion;
    @Getter @Setter
    private boolean isCategoryListExcluded;
    @Getter
    private Short[] hygieneList;
    @Getter
    private Short[] optInHygieneList;
    @Getter@Setter
    private Short[] creativeAttributesForInclusionExclusion;
    @Getter
    private boolean isCreativeAttributesForExclusion;
    /* tells whether the site is web property,wap property or application. */
    @Getter @Setter
    private Short sitePlatform;
    /* this keep site status as active,expired,inactive,rejected,unfit,etc. */
    @Getter
    private Short status;
    @Getter @Setter
    private String[] adDomainsToExclude;
    @Getter
    private boolean excludeDefinedAdDomains;
    @Getter
    private double ecpmFloorValue;
    @Getter
    private boolean isAdvertiserIdListExcluded;
    /*this map contains advertiser id as key and corresponding campaign id list for inc/exc as governed by flag.*/
    @Getter
    private Map<Integer,Set<Integer>> campaignInclusionExclusionSchemaMap;
    @Getter
    private NoFillPassbackContent[] nofillBackupContentArray;
    @Getter
    private SITE_PASSBACK_TYPE sitePassbackType;
    @Getter
    private boolean isRichMediaAllowed;
    @Getter@Setter
    private boolean isNative = false;
    @Getter@Setter
    private NativeProps nativeProps = null;
    @Getter@Setter
    private boolean isVideo = false;
    @Getter@Setter
    private VideoSupplyProps videoSupplyProps = null;
    /**
     * Exchange related external site/app attributes.
     */
    @Getter @Setter
    private String externalSupplyUrl;
    @Getter @Setter
    private String externalSupplyId;
    @Getter @Setter
    private String externalSupplyName;
    @Getter @Setter
    private String externalSupplyDomain;
    @Getter @Setter
    private String externalPageUrl;
    @Getter @Setter
    private String externalAppVersion;
    @Getter @Setter
    private String externalAppBundle;
    @Getter @Setter
    private String[] externalCategories;
    @Getter @Setter
    private Integer[] mmaCatgories;
    @Getter @Setter
    private boolean mmaCatgoriesExclude;
    @Getter @Setter
    private Integer[] mmaCatgoriesForExcInc;
    @Getter @Setter
    private Integer[] mmaindustryCode;
    @Getter @Setter
    private boolean mmaindustrCodeExclude;
    @Getter @Setter
    private Integer[] mmaindustrCodeExcInc;
    @Getter @Setter
    private String adPosition;
    @Getter @Setter
    private Integer adPositionUiId;
    @Getter @Setter
    private String channelFirstLevelCode;
    @Getter @Setter
    private String channelSecondLevelCode;
    @Getter @Setter
    private Integer channelInternalId;
    /*Demand preferences if set here can be used at site level.
    * By default they exist at account level.*/
    @Getter @Setter
    private DemandPreference demandPreference = null;

    private boolean isMarkedForDeletion;
    private final Timestamp updateTime;

    public Site(SiteEntityBuilder siteEntityBuilder)
    {
        this.siteIncId = siteEntityBuilder.siteIncId;
        this.siteGuid = siteEntityBuilder.siteGuid;
        this.name = siteEntityBuilder.name;
        this.publisherIncId = siteEntityBuilder.publisherIncId;
        this.publisherId = siteEntityBuilder.publisherId;
        this.siteUrl = siteEntityBuilder.siteUrl;
        this.applicationId = siteEntityBuilder.applicationId;
        this.appStoreId = siteEntityBuilder.appStoreId;
        this.categoriesArray = siteEntityBuilder.categoriesArray;
        this.categoriesArrayForInclusionExclusion = siteEntityBuilder.categoriesArrayForInclusionExclusion;
        this.isCategoryListExcluded = siteEntityBuilder.isCategoryListExcluded;
        this.hygieneList = siteEntityBuilder.hygieneList;
        this.optInHygieneList = siteEntityBuilder.optInHygieneList;
        this.creativeAttributesForInclusionExclusion = siteEntityBuilder.creativeAttributesForInclusionExclusion;
        this.isCreativeAttributesForExclusion = siteEntityBuilder.isCreativeAttributesForExclusion;
        this.sitePlatform = siteEntityBuilder.sitePlatform;
        this.status = siteEntityBuilder.status;
        this.adDomainsToExclude = siteEntityBuilder.adDomainsToExclude;
        this.excludeDefinedAdDomains = siteEntityBuilder.excludeDefinedAdDomains;
        this.ecpmFloorValue = siteEntityBuilder.ecpmFloorValue;
        this.isAdvertiserIdListExcluded = siteEntityBuilder.isAdvertiserIdListExcluded;
        this.campaignInclusionExclusionSchemaMap = siteEntityBuilder.advertiserCampaignArrayInclusionExclusionMap;
        this.nofillBackupContentArray = siteEntityBuilder.nofillBackupContentArray;
        this.sitePassbackType = siteEntityBuilder.sitePassbackType;
        this.isRichMediaAllowed = siteEntityBuilder.isRichMediaAllowed;
        this.isMarkedForDeletion = siteEntityBuilder.isMarkedForDeletion;
        this.updateTime = siteEntityBuilder.updateTime;
        this.isNative = siteEntityBuilder.isNative;
        this.nativeProps = siteEntityBuilder.nativeProps;
        this.isVideo = siteEntityBuilder.isVideo;
        this.videoSupplyProps = siteEntityBuilder.videoSupplyProps;
        this.adPosition = siteEntityBuilder.adPosition;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (this.siteIncId) + (this.sitePlatform)
                + (this.status)
                + (this.siteGuid != null ? this.siteGuid.hashCode() : 0)
                + (this.publisherId != null ? this.publisherId.hashCode() : 0)
                + (this.siteUrl != null ? this.siteUrl.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        Site externalObject = (Site) obj;

        if (this.siteIncId.equals(externalObject.siteIncId) &&
            this.siteGuid.equals(externalObject.siteGuid))
            return true;

        return false;
    }

    @Override
    public String getId()
    {
        return this.siteGuid;
    }

    @Override
    public Long getModificationTime()
    {
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return this.isMarkedForDeletion;
    }

    public static class SiteEntityBuilder
    {
        private final Integer siteIncId;
        private final String siteGuid;
        private final String name;
        private final String publisherId;
        private final Integer publisherIncId;
        private final String siteUrl;
        private String applicationId;
        private Short appStoreId;
        private Short[] categoriesArray;
        private Short[] categoriesArrayForInclusionExclusion;
        private boolean isCategoryListExcluded;
        private Short[] hygieneList;
        private Short[] optInHygieneList;
        private Short[] creativeAttributesForInclusionExclusion;
        private boolean isCreativeAttributesForExclusion;
        /* tells whether the site is web property,wap property or application. */
        private final Short sitePlatform;
        /* this keep site status as active,expired,inactive,rejected,unfit,etc. */
        private final Short status;
        private String[] adDomainsToExclude;
        private boolean excludeDefinedAdDomains;
        private double ecpmFloorValue;
        private boolean isAdvertiserIdListExcluded;
        /*this map contains advertiser id as key and corresponding campaign id list for inc/exc as governed by flag.*/
        private Map<Integer,Set<Integer>> advertiserCampaignArrayInclusionExclusionMap;
        private NoFillPassbackContent[] nofillBackupContentArray;
        private SITE_PASSBACK_TYPE sitePassbackType;
        private boolean isRichMediaAllowed;
        private final boolean isMarkedForDeletion;
        private final Timestamp updateTime;
        private static final ObjectMapper objectMapper = new ObjectMapper();
        private static final TypeReference<Map<Integer,Set<Integer>>>
                typeReferenceForCampaignInclusionExclusionJson = new TypeReference<Map<Integer,Set<Integer>>>(){};
        private static final TypeReference<NoFillPassbackContent[]>
                typeReferenceForPassbackContent= new TypeReference<NoFillPassbackContent[]>(){};
        private boolean isNative = false;
        private NativeProps nativeProps = null;
        private String adPosition;
        private boolean isVideo = false;
        private VideoSupplyProps videoSupplyProps = null;


        public SiteEntityBuilder(Integer siteIncId,
                                 String siteGuid,
                                 String name,
                                 Integer publisherIncId,
                                 String publisherId,
                                 String siteUrl,
                                 Short[] categoriesArray,
                                 Short[] categoriesArrayForInclusionExclusion,
                                 boolean isCategoryListExcluded,
                                 Short[] hygieneList,
                                 Short[] optInHygieneList,
                                 Short[] creativeAttributesForInclusionExclusion,
                                 boolean isCreativeAttributesForExclusion,
                                 Short sitePlatform,
                                 Short status,
                                 String[] adDomainsToExclude,
                                 boolean excludeDefinedAdDomains,
                                 boolean isMarkedForDeletion,
                                 Timestamp modifiedTime, 
                                 boolean isNative,
                                 NativeProps nativeProps)
        {
            this.siteIncId = siteIncId;
            this.siteGuid = siteGuid;
            this.name = name;
            this.publisherIncId = publisherIncId;
            this.publisherId = publisherId;
            this.siteUrl = siteUrl;
            this.categoriesArray = categoriesArray;
            this.categoriesArrayForInclusionExclusion = categoriesArrayForInclusionExclusion;
            this.isCategoryListExcluded = isCategoryListExcluded;
            this.hygieneList = hygieneList;
            this.optInHygieneList = optInHygieneList;
            this.creativeAttributesForInclusionExclusion = creativeAttributesForInclusionExclusion;
            this.isCreativeAttributesForExclusion = isCreativeAttributesForExclusion;
            this.sitePlatform = sitePlatform;
            this.status = status;
            this.adDomainsToExclude = adDomainsToExclude;
            this.excludeDefinedAdDomains = excludeDefinedAdDomains;
            this.isMarkedForDeletion = isMarkedForDeletion;
            this.updateTime = modifiedTime;
            this.isNative = isNative;
            this.nativeProps = nativeProps;
        }

        public SiteEntityBuilder setApplicationId(String applicationId)
        {
            this.applicationId = applicationId;
            return this;
        }

        public SiteEntityBuilder setAppStoreId(Short appStoreId)
        {
            this.appStoreId = appStoreId;
            return this;
        }

        public SiteEntityBuilder setEcpmFloor(double ecpmFloorValue)
        {
            this.ecpmFloorValue = ecpmFloorValue;
            return this;
        }

        public SiteEntityBuilder setIsAdvertiserIdListExcluded(boolean isAdvertiserIdListExcluded)
        {
            this.isAdvertiserIdListExcluded = isAdvertiserIdListExcluded;
            return this;
        }
        public SiteEntityBuilder setAdPosition(String adPosition)
        {
            this.adPosition = adPosition;
            return this;
        }


        public SiteEntityBuilder setCampaignInclusionExclusionSchemaMap(String campaignInclusionExclusionJson)
        {
            if(null != campaignInclusionExclusionJson)
            {
                try
                {

                    this.advertiserCampaignArrayInclusionExclusionMap =
                            objectMapper.readValue(campaignInclusionExclusionJson,typeReferenceForCampaignInclusionExclusionJson);
                }
                catch (IOException ioe)
                {
                }
            }

            return this;
        }

        public SiteEntityBuilder setCampaignInclusionExclusionSchemaMap(Map<Integer,Set<Integer>> campaignInclusionExclusionSchemaMap)
        {
            this.advertiserCampaignArrayInclusionExclusionMap = campaignInclusionExclusionSchemaMap;
            return this;
        }

        public SiteEntityBuilder setNofillBackupContent(String nofillBackupContent)
        {

            if(null != nofillBackupContent)
            {
                try
                {

                    this.nofillBackupContentArray =
                            objectMapper.readValue(nofillBackupContent,typeReferenceForPassbackContent);
                }
                catch (IOException ioe)
                {
                }
            }

            return this;
        }

        public SiteEntityBuilder setSitePassbackType(short sitePassbackType)
        {
            if(sitePassbackType == SITE_PASSBACK_TYPE.DIRECT_PASSBACK.getCode())
                this.sitePassbackType = SITE_PASSBACK_TYPE.DIRECT_PASSBACK;
            else if(sitePassbackType == SITE_PASSBACK_TYPE.SSP_WATERFALL_PASSBACK.getCode())
                this.sitePassbackType = SITE_PASSBACK_TYPE.SSP_WATERFALL_PASSBACK;

            return this;
        }

        public SiteEntityBuilder setIsRichMediaAllowed(boolean isRichMediaAllowed)
        {
            this.isRichMediaAllowed = isRichMediaAllowed;
            return this;
        }
        public SiteEntityBuilder setIsVideo(boolean isVideo)
        {
            this.isVideo = isVideo;
            return this;
        }
        public SiteEntityBuilder setVideoSupplyProps(VideoSupplyProps videoSupplyProps)
        {
            this.videoSupplyProps = videoSupplyProps;
            return this;
        }

        public Site build()
        {
            return new Site(this);
        }
    }
}