package com.kritter.utils.entity;

import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class VersionRange
{
    private Version minorVersion;
    private Version majorVersion;

    public boolean checkIfVersionIsWithin(Version value)
    {
        if(!passMinorVerCheck(value))
            return false;
        return passMajorVerCheck(value);
    }

    private boolean passMajorVerCheck(Version value)
    {
        // Case: campaign targeting does not care about the major version
        // So nothing to check. Major version check is a pass
        if(!majorVersion.isBounded())
            return true;

        // campaign cares about major version. But the major version
        // could not be extracted in the request. Unfortunate situation
        // but cannot do much. Major version check is a fail
        if(!value.isBounded())
            return false;

        // both the request and campaign major versions are bounded
        List<Integer> valueList = value.getVerList();
        List<Integer> majorVersionList = majorVersion.getVerList();
        int pos = 0;
        for(; pos < valueList.size() && pos < majorVersionList.size(); pos++)
        {
            if(majorVersionList.get(pos) < valueList.get(pos))
                return false;
            else
                return true;
        }
        if(pos < majorVersionList.size())
        {
            // this means majorVersionList had more items
            return true;
        }
        return false;
    }

    private boolean passMinorVerCheck(Version value)
    {
        // campaign targeting does not care about the minor version
        // So nothing to check. Minor version check is a pass
        if(!minorVersion.isBounded())
            return true;

        // campaign cares about minor version. But the minor version
        // could not be extracted in the request.Unfortunate situation
        // but cannot do much. minor version check is a fail
        if(!value.isBounded())
            return false;

        // both the request and campaign minor versions are bounded
        List<Integer> valueList = value.getVerList();
        List<Integer> minorVerList = minorVersion.getVerList();
        int pos = 0;
        for(; pos < valueList.size() && pos < minorVerList.size(); pos++)
        {
            if(minorVerList.get(pos) <= valueList.get(pos))
                return true;
            else
                return false;
        }
        if(pos < minorVerList.size())
            return false;
        return true;
    }
}