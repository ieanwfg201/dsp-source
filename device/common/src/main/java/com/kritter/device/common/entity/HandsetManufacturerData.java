package com.kritter.device.common.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.device.common.util.DeviceUtils;
import lombok.ToString;

import java.sql.Timestamp;

@ToString
public class HandsetManufacturerData implements IUpdatableEntity<String>
{
    private Integer manufacturerId;
    private String manufacturerName;
    private String modifiedBy;
    private Timestamp modifiedOn;
    private static final Timestamp MODIFIED_ON_NOW = new Timestamp(
            System.currentTimeMillis());
    private final static String COMMA = ",";
    private final static String APOSTROPHE = "'";

    // used for data insertion in database.
    public HandsetManufacturerData(String manufacturerName, String modifiedBy) throws Exception
    {
        if (null == manufacturerName)
            throw new Exception(
                    "HandsetManufacturerData cannot be instantiated"
                            + " as one of the manufacturerid or manufacturername is null");

        this.manufacturerName = manufacturerName;
        this.modifiedBy = modifiedBy;
        this.modifiedOn = MODIFIED_ON_NOW;
    }

    public HandsetManufacturerData(Integer manufacturerId,String manufacturerName,
                                   String modifiedBy, Timestamp modifiedOn) throws Exception
    {
        if (null == manufacturerId || null == manufacturerName)
            throw new Exception(
                    "HandsetManufacturerData cannot be instantiated"
                            + " as one of the manufacturerid or manufacturername is null");

        this.manufacturerId = manufacturerId;
        this.manufacturerName = manufacturerName;
        this.modifiedBy = modifiedBy;
        this.modifiedOn = modifiedOn;
    }

    @Override
    public int hashCode() {

        int hash = 7;
        hash = 23
                * hash
                + (this.manufacturerName != null ? this.manufacturerName
                .hashCode() : 0)
                + (this.manufacturerId != null ? this.manufacturerId : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || getClass() != obj.getClass())
            return false;

        HandsetManufacturerData externalObject = (HandsetManufacturerData) obj;

        if (this.manufacturerId.equals(externalObject.manufacturerId))
            return true;

        return false;
    }

    public String prepareSQLRowForManufacturerData() {

        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.manufacturerName));
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

    public Integer getManufacturerId() {
        return manufacturerId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Timestamp getModifiedOn() {
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
        return manufacturerName.toLowerCase();
    }
}