package com.kritter.utils.entity;

import com.kritter.utils.interfaces.Bounded;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Program: Version class which takes version string of the form (A.B.C) and converts them
 * into integer list for ease of comparison
 */
public class Version implements Bounded
{
    @Getter private List<Integer> verList;
    private static final String DOT = "\\.";
    public Version(String version)
    {
        if(StringUtils.isEmpty(version))
            verList = null;
        else
        {
            String[] tempArr = version.split(DOT);
            if(tempArr == null || tempArr.length == 0)
                verList = null;
            else
            {
                verList = new ArrayList<Integer>(tempArr.length);
                for(String currStr : tempArr)
                {
                    try
                    {
                        verList.add(Integer.parseInt(currStr));
                    }
                    catch (RuntimeException runExcp)
                    {
                        // otherwise we have to make sure version values in data
                        // comply with the format and we don't get a number format
                        // exception
                        verList = null;
                    }
                }
            }
        }
    }

    @Override
    public boolean isBounded()
    {
        return (verList != null);
    }
}
