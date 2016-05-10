package com.kritter.common.cache.dpa_site_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class keeps site's and publisher's metadata used in context of a demand partner api.
 */
public class DemandPartnerApiSiteMetadata implements IUpdatableEntity<String>
{
    @Getter
    private int internalId;
    @Getter
    private String siteId;
    @Getter
    private String publisherId;
    private String ruleJson;

    private final Timestamp updateTime;

    private static final TypeReference<Map<Integer,String>> typeReferencePriorityDemandPartnerApiMap =
                                                                    new TypeReference<Map<Integer, String>>() {};

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Map<Integer,String> priorityDemandPartnerApiMap;

    public DemandPartnerApiSiteMetadata(int internalId,
                                        String siteId,
                                        String publisherId,
                                        String ruleJson,
                                        Timestamp updateTime) throws IOException
    {
        this.internalId = internalId;
        this.siteId = siteId;
        this.publisherId = publisherId;
        this.ruleJson = ruleJson;
        this.updateTime = updateTime;

        if(null != this.ruleJson)
            this.priorityDemandPartnerApiMap =
                            objectMapper.readValue(this.ruleJson,typeReferencePriorityDemandPartnerApiMap);
    }

    @Override
    public Long getModificationTime()
    {
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public String getId()
    {
        if(null == this.siteId)
            return this.publisherId;

        return this.siteId;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + this.publisherId.hashCode() + ( (null == this.siteId) ? 0 : (this.siteId.hashCode()) );

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        DemandPartnerApiSiteMetadata externalObject = (DemandPartnerApiSiteMetadata) obj;

        if (
            this.publisherId.equals(externalObject.publisherId)                       &&
            (
             (null == this.siteId && null == externalObject.siteId) ?
              true : this.siteId.equals(externalObject.siteId)
            )
           )
            return true;

        return false;
    }

    public String[] fetchDemandPartnerIdentifierArrayInOrder()
    {
        String[] identifiers = null;
        List<Integer> priorityList = null;

        if(null != this.priorityDemandPartnerApiMap)
        {
            Set<Integer> prioritySet = this.priorityDemandPartnerApiMap.keySet();

            if(null != prioritySet && prioritySet.size() > 0)
            {
                priorityList = new ArrayList<Integer>(prioritySet);
                Collections.sort(priorityList);
            }
        }

        if(null != priorityList)
        {
            identifiers = new String[priorityList.size()];
            int counter = 0;
            for(Integer priority : priorityList)
            {
                identifiers[counter ++] = this.priorityDemandPartnerApiMap.get(priority);
            }
        }

        return identifiers;
    }
}