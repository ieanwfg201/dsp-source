package com.kritter.utils.entity;

import lombok.Getter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.*;

/**
 * This class keeps a map of location entity id as used by
 * user interface versus the entity id set which are ids
 * of parent location entity table.So that they can be
 * saved in targeting profile and used for logging so as
 * to cater for both the user interface and reporting.
 */
public class TargetingProfileLocationEntity
{
    private Map<Integer,Set<Integer>> uiIdVsEntityIdSetMap;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String EMPTY_STRING = "";

    public void addToDataMap(Integer uiEntityId,Set<Integer> parentEntityIdSet)
    {
        if(null == this.uiIdVsEntityIdSetMap)
            uiIdVsEntityIdSetMap = new HashMap<Integer, Set<Integer>>();

        this.uiIdVsEntityIdSetMap.put(uiEntityId,parentEntityIdSet);
    }

    public Integer findUIEntityIdIfExist(Integer parentLocationEntityId)
    {
        if(null == uiIdVsEntityIdSetMap || uiIdVsEntityIdSetMap.size() == 0)
            return null;

        Iterator<Map.Entry<Integer,Set<Integer>>> entryIterator = this.uiIdVsEntityIdSetMap.entrySet().iterator();

        while(entryIterator.hasNext())
        {
            Map.Entry<Integer,Set<Integer>> entry = entryIterator.next();
            if(entry.getValue().contains(parentLocationEntityId));
            return entry.getKey();
        }
        return null;
    }

    public String prepareJsonMapForTargetingProfilePopulation()
    {
        if(null == uiIdVsEntityIdSetMap || uiIdVsEntityIdSetMap.size() == 0)
            return null;

        String result = null;

        try
        {
            result = objectMapper.writeValueAsString(uiIdVsEntityIdSetMap);
        }
        catch (IOException ioe)
        {
            return null;
        }

        return result;
    }

    public void readTargetingProfileLocationJsonIntoDataMap(String locationEntityJson) throws IOException
    {
        if(null != locationEntityJson && !EMPTY_STRING.equals(locationEntityJson))
            this.uiIdVsEntityIdSetMap = objectMapper.readValue(
                                                               locationEntityJson,
                                                               new TypeReference<Map<Integer,Set<Integer>>>(){}
                                                              );
    }

    public Integer[] fetchAllValueArrayFromDataMap()
    {
        if(null == this.uiIdVsEntityIdSetMap || this.uiIdVsEntityIdSetMap.size() == 0)
            return null;

        Set<Integer> entireValueSet = new HashSet<Integer>();

        Iterator<Map.Entry<Integer,Set<Integer>>> entryIterator = this.uiIdVsEntityIdSetMap.entrySet().iterator();

        while(entryIterator.hasNext())
        {
            Map.Entry<Integer,Set<Integer>> entry = entryIterator.next();
            entireValueSet.addAll(entry.getValue());
        }

        return entireValueSet.toArray(new Integer[entireValueSet.size()]);
    }

    public Integer[] fetchAllKeyArrayFromDataMap()
    {
        if(null == this.uiIdVsEntityIdSetMap || this.uiIdVsEntityIdSetMap.size() == 0)
            return null;

        return this.uiIdVsEntityIdSetMap.keySet().toArray(new Integer[this.uiIdVsEntityIdSetMap.keySet().size()]);
    }
}