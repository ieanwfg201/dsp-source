package com.kritter.serving.demand.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.constants.InclusionExclusionType;
import com.kritter.constants.MidpValue;
import com.kritter.entity.targeting_profile.column.Retargeting;
import com.kritter.entity.targeting_profile.column.TPExt;
import com.kritter.utils.entity.TargetingProfileLocationEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.*;

/**
 * This class represents targeting profile which comes directly under an account.
 * Any user of DSP can choose targeting profiles from its account.
 */
@Getter
@ToString
@EqualsAndHashCode(of = {"targetingGuid"})

public class TargetingProfile implements IUpdatableEntity<String>{
    private final Integer targetingId;
    private final String targetingGuid;
    private final String accountId;

    // Targeting parameters
    private final Integer[] targetedBrands;
    private final Integer[] targetedHandsetModels;
    private final String targetedOSJson;
    private final Map<String,String> targetedOSJsonMap;
    private final String targetedBrowserJson;
    private final Map<String,String> targetedBrowserJsonMap;
    private final TargetingProfileLocationEntity targetedCountries;
    private final TargetingProfileLocationEntity targetedCarriers;
    private final TargetingProfileLocationEntity targetedStates;
    private final TargetingProfileLocationEntity targetedCities;
    private final Integer[] targetedZipcodeIds;
    private final Short[] categoriesInclusionList;
    private final Short[] categoriesExclusionList;
    private final String[] customIpFileIdArray;
    private final String[] zipCodeFileIdArray;
    private final Short supplySourceType;
    private final Short supplySource;
    private final Set<Short> hoursTargetedInTheDay;
    private final MidpValue midpTargetingValue;
    private final LatitudeLongitudeRadius[] latitudeLongitudeRadiusArray;
    private final boolean isSiteListExcluded;
    private final Map<Integer,Map<Integer,Map<Integer,List<ExternalSupplyAttributes>>>> supplyInclusionExclusionMap;
    private final Short[] targetedConnectionTypes;
    private final boolean tabletTargeting;
    private final boolean markedForDeletion;
    private final Long modificationTime;
    private final Retargeting retargeting;
    private final Map<String,String[]> pmpDealIdInfoMap;
    private final Short[] deviceTypeArray;
    private final TPExt tpExt;
    private final String[] latLonFileIdArray;
    private final InclusionExclusionType userIdInclusionExclusionType;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TargetingProfile(TargetingBuilder targetingBuilder)
    {
        this.targetingId = targetingBuilder.targetingId;
        this.targetingGuid = targetingBuilder.targetingGuid;
        this.accountId = targetingBuilder.accountId;
        this.targetedBrands = targetingBuilder.targetedBrands;
        this.targetedHandsetModels = targetingBuilder.targetedModels;
        this.targetedOSJson = targetingBuilder.targetedOSJson;
        this.targetedBrowserJson = targetingBuilder.targetedBrowserJson;
        this.targetedBrowserJsonMap = targetingBuilder.targetedBrowserJsonMap;
        this.targetedOSJsonMap = targetingBuilder.targetedOSJsonMap;
        this.targetedCountries = targetingBuilder.targetedCountries;
        this.targetedCarriers = targetingBuilder.targetedCarriers;
        this.targetedStates = targetingBuilder.targetedStates;
        this.targetedCities = targetingBuilder.targetedCities;
        this.targetedZipcodeIds = targetingBuilder.targetedZipIds;
        this.categoriesInclusionList = targetingBuilder.categoriesInclusionList;
        this.categoriesExclusionList = targetingBuilder.categoriesExclusionList;
        this.customIpFileIdArray = targetingBuilder.customIpFileIdArray;
        this.zipCodeFileIdArray = targetingBuilder.zipCodeFileIdArray;
        this.supplySourceType = targetingBuilder.supplySourceType;
        this.supplySource = targetingBuilder.supplySource;
        this.midpTargetingValue = targetingBuilder.midpTargetingValue;
        this.latitudeLongitudeRadiusArray = targetingBuilder.latitudeLongitudeRadiusArray;
        this.isSiteListExcluded = targetingBuilder.isSiteListExcluded;
        this.supplyInclusionExclusionMap = targetingBuilder.supplyInclusionExclusionMap;
        this.hoursTargetedInTheDay = targetingBuilder.hoursTargetedInTheDay;
        this.targetedConnectionTypes = targetingBuilder.targetedConnectionTypes;
        this.tabletTargeting = targetingBuilder.tabletTargeting;
        this.markedForDeletion = targetingBuilder.isMarkedForDeletion;
        this.modificationTime = targetingBuilder.updateTime;
        this.retargeting = targetingBuilder.retargeting;
        this.pmpDealIdInfoMap = targetingBuilder.pmpDealIdInfoMap;
        this.deviceTypeArray = targetingBuilder.deviceTypeArray;
        this.tpExt = targetingBuilder.tpExt;
        this.latLonFileIdArray = targetingBuilder.latLonFileIdArray;
        this.userIdInclusionExclusionType = targetingBuilder.userIdInclusionExclusionType;
    }

    @Override
    public String getId()
    {
        return this.targetingGuid;
    }

    public static class TargetingBuilder
    {
        private final Integer targetingId;
        private final String targetingGuid;
        private final String accountId;

        // Targeting parameters
        private Integer[] targetedBrands;
        private Integer[] targetedModels;
        private String targetedOSJson;
        private String targetedBrowserJson;
        private Map<String,String> targetedBrowserJsonMap;
        private Map<String,String> targetedOSJsonMap;
        private TargetingProfileLocationEntity targetedCountries;
        private TargetingProfileLocationEntity targetedCarriers;
        private TargetingProfileLocationEntity targetedStates;
        private TargetingProfileLocationEntity targetedCities;
        private Integer[] targetedZipIds;
        private Short[] categoriesInclusionList;
        private Short[] categoriesExclusionList;
        private String[] customIpFileIdArray;
        private String[] zipCodeFileIdArray;
        private Short supplySourceType;
        private Short supplySource;
        private Set<Short> hoursTargetedInTheDay;
        private MidpValue midpTargetingValue;
        private LatitudeLongitudeRadius[] latitudeLongitudeRadiusArray;
        private boolean isSiteListExcluded;
        private Map<Integer,Map<Integer,Map<Integer,List<ExternalSupplyAttributes>>>> supplyInclusionExclusionMap;
        private Short[] targetedConnectionTypes;
        private boolean tabletTargeting;
        private boolean isMarkedForDeletion;
        private Long updateTime;
        private Retargeting retargeting;
        private Map<String,String[]> pmpDealIdInfoMap;
        private Short[] deviceTypeArray;
        private TPExt tpExt;
        private String[] latLonFileIdArray;
        private InclusionExclusionType userIdInclusionExclusionType;
        private static final TypeReference<Map<String,String>>
                     typeReferenceForOSBrowserJSon = new TypeReference<Map<String, String>>() {};

        private static final TypeReference<Map<Integer,Map<Integer,Map<Integer,List<ExternalSupplyAttributes>>>>>
                                                    typeReferenceForSupplyInclusionExclusion =
                new TypeReference<Map<Integer,Map<Integer,Map<Integer,List<ExternalSupplyAttributes>>>>>() {};

        private static final TypeReference<Map<String,String[]>> typeReferenceForPMPDealId =
                new TypeReference<Map<String,String[]>>() {};

        public TargetingBuilder(Integer targetingId, String targetingGuid, String accountId,
                                boolean isMarkedForDeletion,Long updateTime)
        {
            this.targetingId = targetingId;
            this.targetingGuid = targetingGuid;
            this.accountId = accountId;
            this.isMarkedForDeletion = isMarkedForDeletion;
            this.updateTime = updateTime;
        }

        public TargetingBuilder setTargetedBrands(Integer[] targetedBrands)
        {
            this.targetedBrands = targetedBrands;
            return this;
        }

        public TargetingBuilder setTargetedModels(Integer[] targetedModels)
        {
            this.targetedModels = targetedModels;
            return this;
        }

        public TargetingBuilder setTargetedOSJson(String targetedOSJson)
        {
            this.targetedOSJson = targetedOSJson;
            this.targetedOSJsonMap = null;
            try
            {
                this.targetedOSJsonMap = objectMapper.readValue(targetedOSJson,typeReferenceForOSBrowserJSon);
            }
            catch (Exception ioe)
            {
            }

            return this;
        }

        public TargetingBuilder setTargetedBrowserJson(String targetedBrowserJson) throws IOException
        {
            this.targetedBrowserJson = targetedBrowserJson;
            this.targetedBrowserJsonMap = null;

            try
            {
                this.targetedBrowserJsonMap = objectMapper.readValue(targetedBrowserJson,typeReferenceForOSBrowserJSon);
            }
            catch (Exception ioe)
            {

            }

            return this;
        }

        public TargetingBuilder setTargetedCountries(TargetingProfileLocationEntity targetedCountries)
        {
            this.targetedCountries = targetedCountries;
            return this;
        }

        public TargetingBuilder setTargetedCarriers(TargetingProfileLocationEntity targetedCarriers)
        {
            this.targetedCarriers = targetedCarriers;
            return this;
        }

        public TargetingBuilder setTargetedStates(TargetingProfileLocationEntity targetedStates)
        {
            this.targetedStates = targetedStates;
            return this;
        }

        public TargetingBuilder setTargetedCities(TargetingProfileLocationEntity targetedCities)
        {
            this.targetedCities = targetedCities;
            return this;
        }

        public TargetingBuilder setTargetedZipcodes(Integer[] targetedZipcodeIds)
        {
            this.targetedZipIds = targetedZipcodeIds;
            return this;
        }

        public TargetingBuilder setCategoriesInclusionList(Short[] categoriesInclusionList)
        {
            this.categoriesInclusionList = categoriesInclusionList;
            return this;
        }

        public TargetingBuilder setCategoriesExclusionList(Short[] categoriesExclusionList)
        {
            this.categoriesExclusionList = categoriesExclusionList;
            return this;
        }

        public TargetingBuilder setCustomIpFileIdArray(String[] customIpFileIdArray)
        {
            this.customIpFileIdArray = customIpFileIdArray;
            return this;
        }

        public TargetingBuilder setZipCodeFileIdArray(String[] zipCodeFileIdArray)
        {
            this.zipCodeFileIdArray = zipCodeFileIdArray;
            return this;
        }

        public TargetingBuilder setSupplySourceType(Short supplySourceType)
        {
            this.supplySourceType = supplySourceType;
            return this;
        }

        public TargetingBuilder setSupplySource(Short supplySource)
        {
            this.supplySource = supplySource;
            return this;
        }

        public TargetingBuilder setMidpValue(Short midpValueCode)
        {
            this.midpTargetingValue = MidpValue.fetchMipValue(midpValueCode);
            return this;
        }

        public TargetingBuilder setLatitudeLongitudeRadius(String serializedLatitudeLongitudeArray)
        {
            if(null != serializedLatitudeLongitudeArray)
            {
                try
                {
                    this.latitudeLongitudeRadiusArray =
                            objectMapper.readValue(serializedLatitudeLongitudeArray,LatitudeLongitudeRadius[].class);
                }
                catch (IOException ioe)
                {
                }
            }

            return this;
        }

        public TargetingBuilder setIsSiteListExcluded(boolean isSiteListExcluded)
        {
            this.isSiteListExcluded = isSiteListExcluded;
            return this;
        }

        public TargetingBuilder setExchangePublisherWithSiteSetForInclusionExclusionMap
                                                (String supplyInclusionExclusionMapJson,
                                                 String targetingGuid) throws IOException
        {
            if(null != supplyInclusionExclusionMapJson)
            {
                try
                {
                    this.supplyInclusionExclusionMap = objectMapper.
                                  readValue(supplyInclusionExclusionMapJson, typeReferenceForSupplyInclusionExclusion);
                }
                catch (IOException ioe)
                {
                    throw new IOException("IOException in setting supply inc/exc attributes, for targeting " +
                                          "guid: " + targetingGuid, ioe);
                }
            }

            return this;
        }

        public TargetingBuilder setExchangeSpecificPMPDealIdInfo(
                                                                 String pmpDealIdJson,
                                                                 String targetingGuid
                                                                ) throws IOException
        {
            if(null != pmpDealIdJson)
            {
                try
                {
                    this.pmpDealIdInfoMap = objectMapper.readValue(pmpDealIdJson, typeReferenceForPMPDealId);
                }
                catch (IOException ioe)
                {
                    throw new IOException("IOException in setting pmp deal id info, for targeting guid: " +
                                          targetingGuid, ioe);
                }
            }

            return this;
        }

        public TargetingBuilder setDeviceTypeTargetingArray(Short[] deviceTypeArray)
        {
            this.deviceTypeArray = deviceTypeArray;
            return this;
        }

        public TargetingBuilder setHoursOfDayTargeted(Set<Short> hoursOfDayTargeted)
        {
            this.hoursTargetedInTheDay = hoursOfDayTargeted;
            return this;
        }

        public TargetingBuilder setTargetedConnectionTypes(Short[] targetedConnectionTypes) {
            this.targetedConnectionTypes = targetedConnectionTypes;
            return this;
        }

        public TargetingBuilder setTabletTargeting(boolean tabletTargeting) {
            this.tabletTargeting = tabletTargeting;
            return this;
        }
        public TargetingBuilder setRetargeting(Retargeting retargeting)
        {
            this.retargeting = retargeting;
            return this;
        }
        public TargetingBuilder setLatLonFileIdArray(String[] latLonFileIdArray)
        {
            this.latLonFileIdArray = latLonFileIdArray;
            return this;
        }
        public TargetingBuilder setTPExt(TPExt tpExt)
        {
            this.tpExt = tpExt;
            return this;
        }

        public TargetingBuilder setUserIdInclusionExclusionType(InclusionExclusionType userIdInclusionExclusionType) {
            this.userIdInclusionExclusionType = userIdInclusionExclusionType;
            return this;
        }

        public TargetingProfile build()
        {
            return new TargetingProfile(this);
        }
    }

    public static class LatitudeLongitudeRadius
    {
        @Getter @Setter
        @JsonProperty("lat")
        private double latitude;
        @Getter @Setter
        @JsonProperty("lon")
        private double longitude;
        @Getter @Setter
        @JsonProperty("r")
        private double radius;
    }
}
