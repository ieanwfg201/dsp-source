package com.kritter.entity.offers;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.constants.StatusIdEnum;
import lombok.Getter;
import java.sql.Timestamp;
import java.util.Map;

/**
 * This class captures attributes that are applicable to defining an
 * offer as part of KritterOffers platform.
 */
public class Offer implements IUpdatableEntity<Integer>
{
    @Getter
    private int id;
    @Getter
    private String guid;
    @Getter
    private StatusIdEnum status;
    @Getter
    private String name;
    @Getter
    private String advertiserGuid;
    @Getter
    private String landingUrl;
    @Getter
    private int creativeId;
    @Getter
    private String creativeGuid;
    @Getter
    private String domain;
    @Getter
    private Integer[] targetedManufacturers;
    @Getter
    private Integer[] targetedCountries;
    @Getter
    private Map<String,String> targetedOperatingSystemMap;
    @Getter
    private Map<String,Double> goalBidMap;
    @Getter
    private String[] customIpFileList;
    @Getter
    private Integer[] publisherList;
    @Getter
    private Timestamp startDate;
    @Getter
    private Timestamp endDate;
    @Getter
    private Double totalBudget;
    @Getter
    private Double dailyBudget;
    @Getter
    private Double totalBurn;
    @Getter
    private Double dailyBurn;
    @Getter
    private Timestamp lastModified;

    public Offer(int id,String guid,short status,String name,String advertiserGuid,String landingUrl,int creativeId,
                 String creativeGuid,String domain,Integer[] targetedManufacturers,Integer[] targetedCountries,
                 Map<String,String> targetedOperatingSystemMap,Map<String,Double> goalBidMap,
                 String[] customIpFileList,Integer[] publisherList,Timestamp startDate,
                 Timestamp endDate,Double totalBudget,Double dailyBudget,Double totalBurn,Double dailyBurn,
                 Timestamp lastModified)
    {
        this.id = id;
        this.guid = guid;
        this.status = fetchStatus(status);
        this.name = name;
        this.advertiserGuid = advertiserGuid;
        this.landingUrl = landingUrl;
        this.creativeId = creativeId;
        this.creativeGuid = creativeGuid;
        this.domain = domain;
        this.targetedManufacturers = targetedManufacturers;
        this.targetedCountries = targetedCountries;
        this.targetedOperatingSystemMap = targetedOperatingSystemMap;
        this.goalBidMap = goalBidMap;
        this.customIpFileList = customIpFileList;
        this.publisherList = publisherList;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalBudget = totalBudget;
        this.dailyBudget = dailyBudget;
        this.totalBurn = totalBurn;
        this.dailyBurn = dailyBurn;
        this.lastModified = lastModified;
    }

    private StatusIdEnum fetchStatus(short status)
    {
        if(status == StatusIdEnum.Active.getCode())
            return StatusIdEnum.Active;
        if(status == StatusIdEnum.Paused.getCode())
            return StatusIdEnum.Paused;
        return StatusIdEnum.Paused;
    }

    @Override
    public Long getModificationTime()
    {
        return this.lastModified.getTime();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return false;
    }

    @Override
    public Integer getId()
    {
        return this.id;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (this.id) + (this.guid.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        Offer externalObject = (Offer) obj;

        if (this.id == externalObject.id)
            return true;

        return false;
    }
}
