package com.kritter.utils.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VersionTargeting
{
    private List<Integer> entityIds = null;
    private Map<Integer, VersionRange> entityVersionMap = null;

    public VersionTargeting(String targetingJSON, String key)
    {
        if(StringUtils.isEmpty(targetingJSON))
            return;
        entityIds = new ArrayList<Integer>();
        entityVersionMap = new HashMap<Integer, VersionRange>();
        try
        {
            JSONObject jsonObject = new JSONObject(targetingJSON);
            final JSONArray jsonArray = jsonObject.getJSONArray(key);
            for(int count = 0; count < jsonArray.length(); count++)
            {
                final JSONObject currTargetingObject = jsonArray.getJSONObject(count);
                Integer entityId = currTargetingObject.getInt("id");
                String minVersion = currTargetingObject.getString("min");
                String maxVersion = currTargetingObject.getString("max");
                entityIds.add(entityId);
                entityVersionMap.put(entityId, new VersionRange(new Version(minVersion), new Version(maxVersion)));
            }
        }
        catch (JSONException jsonExcp)
        {
            throw new RuntimeException(jsonExcp);
        }
    }

    public boolean entityIdPassesTargeting(Integer entityId)
    {
        if(CollectionUtils.isEmpty(entityIds))
            return true;
        return entityIds.contains(entityId);
    }

    public boolean entityVersionPassesTargeting(Integer entityId, Version version)
    {
        if(entityVersionMap.size() == 0)
            return true;

        VersionRange range = entityVersionMap.get(entityId);
        if(range == null)
            return false;
        return range.checkIfVersionIsWithin(version);
    }
}
