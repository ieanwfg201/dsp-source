package com.kritter.device.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.device.util.DeviceUtils;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ToString
public class HandsetOperatingSystemData implements IUpdatableEntity<String>
{
    private Integer operatingSystemId;
    private String operatingSystemName;
    @Getter
    private Set<String> operatingSystemVersionSet;
    private String modifiedBy;
    private Timestamp modifiedOn;
    private static final Timestamp MODIFIED_ON_NOW = new Timestamp(
            System.currentTimeMillis());
    private final static String COMMA = ",";
    private final static String APOSTROPHE = "'";

    // used for data insertion into database.
    public HandsetOperatingSystemData(String operatingSystemName, String modifiedBy) throws Exception
    {
        if (null == operatingSystemName)
            throw new Exception(
                                "HandsetOperatingSystemData cannot be instantiated " +
                                "as one of the operatingsystem id or operatingsystem name is null"
                               );

        this.operatingSystemName = operatingSystemName;
        this.operatingSystemVersionSet = new HashSet<String>();
        this.modifiedBy = modifiedBy;
        this.modifiedOn = MODIFIED_ON_NOW;
    }

    public HandsetOperatingSystemData(
                                      Integer operatingSystemId,
                                      String operatingSystemName,
                                      String[] operatingSystemVersions,
                                      String modifiedBy,
                                      Timestamp modifiedOn
                                     ) throws Exception
    {
        if (null == operatingSystemId || null == operatingSystemName)
            throw new Exception(
                                "HandsetOperatingSystemData cannot be instantiated " +
                                "as one of the operatingsystem id or operatingsystem name is null"
                               );

        this.operatingSystemId = operatingSystemId;
        this.operatingSystemName = operatingSystemName;
        if(operatingSystemVersions != null) {
            this.operatingSystemVersionSet = new HashSet<String>(Arrays.asList(operatingSystemVersions));
        } else {
            this.operatingSystemVersionSet = new HashSet<String>();
        }
        this.modifiedBy = modifiedBy;
        this.modifiedOn = modifiedOn;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23
                * hash
                + (this.operatingSystemName != null ? this.operatingSystemName
                .hashCode() : 0)
                + (this.operatingSystemId != null ? this.operatingSystemId : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        HandsetOperatingSystemData externalObject = (HandsetOperatingSystemData) obj;

        if (this.operatingSystemId.equals(externalObject.operatingSystemId))
            return true;

        return false;
    }

    public String prepareSQLRowForOperatingSystemData()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.operatingSystemName));
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

    public Integer getOperatingSystemId()
    {
        return operatingSystemId;
    }

    public String getOperatingSystemName()
    {
        return operatingSystemName;
    }

    public String[] getOperatingSystemVersions()
    {
        return operatingSystemVersionSet.toArray(new String[operatingSystemVersionSet.size()]);
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
    public Long getModificationTime() {
        return modifiedOn.getTime();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return false;
    }

    @Override
    public String getId() {
        return operatingSystemName.toLowerCase();
    }
}

