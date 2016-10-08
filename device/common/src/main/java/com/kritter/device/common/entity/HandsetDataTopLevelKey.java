package com.kritter.device.common.entity;

import lombok.ToString;

/**
 * This class keeps the top level key for data in handset_detection_data table.
 */
@ToString
public class HandsetDataTopLevelKey
{

    private String externalId;
    private String source;
    private Integer version;

    public HandsetDataTopLevelKey(String externalId, String source,
                                  Integer version) throws Exception
    {
        if (null == externalId || null == source || null == version)
            throw new Exception(
                    "HandsetDataTopLevelKey cannot be instantiated, as"
                            + " one or more of the external id or source or version is null");

        this.externalId = externalId;
        this.source = source;
        this.version = version;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash
                + (this.externalId != null ? this.externalId.hashCode() : 0)
                + (this.source != null ? this.source.hashCode() : 0)
                + (this.version != null ? this.version : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        HandsetDataTopLevelKey externalObject = (HandsetDataTopLevelKey) obj;

        if (this.externalId.equalsIgnoreCase(externalObject.externalId)
                && this.source.equalsIgnoreCase(externalObject.source)
                && this.version.equals(externalObject.version))
        {
            return true;
        }
        return false;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public String getSource()
    {
        return source;
    }

    public Integer getVersion()
    {
        return version;
    }
}