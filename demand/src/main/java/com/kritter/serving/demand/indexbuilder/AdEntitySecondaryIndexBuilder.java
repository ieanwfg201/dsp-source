package com.kritter.serving.demand.indexbuilder;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.serving.demand.index.*;
import com.kritter.utils.entity.TargetingProfileLocationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


/**
 * This class builds secondary indexes for ad entity.
 */

public class AdEntitySecondaryIndexBuilder {

    private static Logger logger = LoggerFactory.getLogger("cache.logger");

    public static ISecondaryIndexWrapper getIndex(Class className, AdEntity entity)
    {
        if(className.equals(BrandIdIndex.class))
            return AdEntitySecondaryIndexBuilder.getBrandIdIndex(entity);
        if(className.equals(CountryIdIndex.class))
            return AdEntitySecondaryIndexBuilder.getCountryIdIndex(entity);
        if(className.equals(CountryCarrierIdIndex.class))
            return AdEntitySecondaryIndexBuilder.getCountryCarrierIdIndex(entity);
        if(className.equals(ModelIdIndex.class))
            return AdEntitySecondaryIndexBuilder.getModelIdIndex(entity);
        if(className.equals(AdGuidIndex.class))
            return AdEntitySecondaryIndexBuilder.getAdGuidSecondaryIndex(entity);

        return null;
    }

    private static ISecondaryIndexWrapper getAdGuidSecondaryIndex(AdEntity entity){

        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
        indexKeyList.add(new AdGuidIndex(entity.getAdGuid()));

        return new ISecondaryIndexWrapper()
        {
            @Override
            public boolean isAllTargeted()
            {
                return false;
            }

            @Override
            public Set<ISecondaryIndex> getSecondaryIndexSet()
            {
                return indexKeyList;
            }
        };
    }

    private static ISecondaryIndexWrapper getCampaignIncIdSecondaryIndex(AdEntity entity){

        final Set<ISecondaryIndex> indexKeyList = new HashSet<ISecondaryIndex>();
        indexKeyList.add(new CampaignIncIdSecondaryIndex(entity.getCampaignIncId()));

        return new ISecondaryIndexWrapper() {
            @Override
            public boolean isAllTargeted() {
                return false;
            }

            @Override
            public Set<ISecondaryIndex> getSecondaryIndexSet() {
                return indexKeyList;
            }
        };
    }

    private static ISecondaryIndexWrapper getBrandIdIndex(AdEntity adEntity)
    {
        TargetingProfile targetingProfile = adEntity.getTargetingProfile();
        final Integer[] targetedBrands =  targetingProfile.getTargetedBrands();
        return new ISecondaryIndexWrapper()
        {
            @Override
            public boolean isAllTargeted()
            {
                return (targetedBrands == null || targetedBrands.length == 0);
            }

            @Override
            public Set<ISecondaryIndex> getSecondaryIndexSet()
            {
                Set<ISecondaryIndex> brandIdIndexSet = new HashSet<ISecondaryIndex>();
                if(targetedBrands != null && targetedBrands.length > 0)
                {
                    for(Integer brandId :  targetedBrands)
                        brandIdIndexSet.add(new BrandIdIndex(brandId));
                }
                else{
                    brandIdIndexSet.add(new BrandIdIndex(null));
                }
                return brandIdIndexSet;
            }
        };
    }

    private static ISecondaryIndexWrapper getModelIdIndex(AdEntity adEntity)
    {
        TargetingProfile targetingProfile = adEntity.getTargetingProfile();
        final Integer[] targetedModels =  targetingProfile.getTargetedHandsetModels();
        return new ISecondaryIndexWrapper()
        {
            @Override
            public boolean isAllTargeted()
            {
                return (targetedModels == null || targetedModels.length == 0);
            }

            @Override
            public Set<ISecondaryIndex> getSecondaryIndexSet()
            {
                Set<ISecondaryIndex> modelIdIndexSet = new HashSet<ISecondaryIndex>();
                if(targetedModels != null && targetedModels.length > 0)
                {
                    for(Integer modelId :  targetedModels)
                        modelIdIndexSet.add(new ModelIdIndex(modelId));
                }
                else{
                    modelIdIndexSet.add(new ModelIdIndex(null));
                }
                return modelIdIndexSet;
            }
        };
    }

    //this function creates secondary indexes on country user interface id.
    private static ISecondaryIndexWrapper getCountryIdIndex(final AdEntity adEntity)
    {
        TargetingProfile targetingProfile = adEntity.getTargetingProfile();
        TargetingProfileLocationEntity targetedCountries = targetingProfile.getTargetedCountries();
        final Integer[] targetedCountryIds = targetedCountries.fetchAllKeyArrayFromDataMap();

        logger.debug("Generating secondary indexes for ad: {} against country ids: {}", adEntity.getId(), targetedCountries.prepareJsonMapForTargetingProfilePopulation());

        return new ISecondaryIndexWrapper()
        {
            @Override
            public boolean isAllTargeted()
            {
                return (targetedCountryIds == null || targetedCountryIds.length == 0);
            }

            @Override
            public Set<ISecondaryIndex> getSecondaryIndexSet()
            {
                Set<ISecondaryIndex> countryIdIndexSet = new HashSet<ISecondaryIndex>();
                if(targetedCountryIds != null && targetedCountryIds.length > 0)
                {
                    for(Integer countryId :  targetedCountryIds)
                        countryIdIndexSet.add(new CountryIdIndex(countryId));
                }
                else {
                    countryIdIndexSet.add(new CountryIdIndex(null));
                }
                return countryIdIndexSet;
            }
        };
    }

    //this function creates secondary indexes on carrier user interface id...
    private static ISecondaryIndexWrapper getCountryCarrierIdIndex(final AdEntity adEntity)
    {
        TargetingProfile targetingProfile = adEntity.getTargetingProfile();

        TargetingProfileLocationEntity targetedCarriers = targetingProfile.getTargetedCarriers();
        final Integer[] targetedCarrierIds = targetedCarriers.fetchAllKeyArrayFromDataMap();

        logger.debug("Generating secondary indexes for ad: {} against carrier ids: {}", adEntity.getId(), targetedCarriers.prepareJsonMapForTargetingProfilePopulation());

        return new ISecondaryIndexWrapper()
        {
            @Override
            public boolean isAllTargeted()
            {
                return (targetedCarrierIds == null || targetedCarrierIds.length == 0);
            }

            @Override
            public Set<ISecondaryIndex> getSecondaryIndexSet()
            {
                Set<ISecondaryIndex> carrierIdIndexSet = new HashSet<ISecondaryIndex>();
                if(targetedCarrierIds != null && targetedCarrierIds.length > 0)
                {
                    for(Integer carrierId :  targetedCarrierIds)
                        carrierIdIndexSet.add(new CountryCarrierIdIndex(carrierId));
                }
                else{
                    carrierIdIndexSet.add(new CountryCarrierIdIndex(null));
                }
                return carrierIdIndexSet;
            }
        };
    }
}
