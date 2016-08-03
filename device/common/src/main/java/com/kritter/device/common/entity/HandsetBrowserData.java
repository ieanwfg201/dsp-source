package com.kritter.device.common.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.device.common.util.DeviceUtils;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ToString
public class HandsetBrowserData implements IUpdatableEntity<String>
{
    private Integer browserId;
    private String browserName;
    @Getter
    private Set<String> browserVersionSet;
    private String modifiedBy;
    private Timestamp modifiedOn;
    private static final Timestamp MODIFIED_ON_NOW = new Timestamp(
            System.currentTimeMillis());
    private final static String COMMA = ",";
    private final static String APOSTROPHE = "'";

    // used for insertion of data into database.
    public HandsetBrowserData(String browserName,String modifiedBy) throws Exception
    {
        if (null == browserName)
            throw new Exception(
                                "HandsetBrowserData cannot be instantiated " +
                                "as one of the browserid or browsername is null"
                               );

        this.browserName = browserName;
        this.browserVersionSet = new HashSet<String>();
        this.modifiedBy = modifiedBy;
        this.modifiedOn = MODIFIED_ON_NOW;
    }

    public HandsetBrowserData(
                              Integer browserId,
                              String browserName,
                              String[] browserVersions,
                              String modifiedBy,
                              Timestamp modifiedOn
                             ) throws Exception
    {
        if (null == browserId || null == browserName)
            throw new Exception("HandsetBrowserData cannot be instantiated " +
                                "as one of the browserid or browsername is null");

        this.browserId = browserId;
        this.browserName = browserName;
        if(browserVersions != null) {
            this.browserVersionSet = new HashSet<String>(Arrays.asList(browserVersions));
        } else {
            this.browserVersionSet = new HashSet<String>();
        }
        this.modifiedBy = modifiedBy;
        this.modifiedOn = modifiedOn;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash
                + (this.browserName != null ? this.browserName.hashCode() : 0)
                + (this.browserId != null ? this.browserId : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        HandsetBrowserData externalObject = (HandsetBrowserData) obj;

        if (this.browserId.equals(externalObject.browserId))
            return true;

        return false;
    }

    public String prepareSQLRowForBrowserData()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.browserName));
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(this.modifiedBy);
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(this.modifiedOn);
        sb.append(APOSTROPHE);
        sb.append(")");
        return sb.toString();
    }

    public Integer getBrowserId()
    {
        return browserId;
    }

    public String getBrowserName()
    {
        return browserName;
    }

    public String[] getBrowserVersions()
    {
        return browserVersionSet.toArray(new String[browserVersionSet.size()]);
    }

    public String getModifiedBy()
    {
        return modifiedBy;
    }

    public Timestamp getModifiedOn()
    {
        return modifiedOn;
    }

    @Override
    public String getId() {
        return browserName.toLowerCase();
    }

    @Override
    public Long getModificationTime() {
        return modifiedOn.getTime();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return false;
    }
}
