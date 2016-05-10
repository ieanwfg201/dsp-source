package com.kritter.common.site.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;

import java.sql.Timestamp;

/**
 * This class keeps a row for site_metadata table.
 */
public class SiteMetaDataEntity implements IUpdatableEntity<String>
{
    @Getter
    private String siteGuid;
    @Getter
    private String passbackURL;
    @Getter
    private String responseContentType;
    @Getter
    private String responseContent;
    private final long updateTime;

    public SiteMetaDataEntity(
                              String siteGuid,
                              String passbackURL,
                              String responseContentType,
                              String responseContent,
                              long updateTime
                             )
    {
        this.siteGuid = siteGuid;
        this.passbackURL = passbackURL;
        this.responseContentType = responseContentType;
        this.responseContent = responseContent;
        this.updateTime = updateTime;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (this.siteGuid != null ? this.siteGuid.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        SiteMetaDataEntity externalObject = (SiteMetaDataEntity) obj;

        if (this.siteGuid.equals(externalObject.siteGuid))
            return true;

        return false;
    }

    @Override
    public Long getModificationTime()
    {
        return this.updateTime;
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public String getId()
    {
        return siteGuid;
    }
}
