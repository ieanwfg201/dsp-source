package com.kritter.abstraction.cache.abstractions.examples.site;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.List;

/**
 * Date: 13-06-2013
 * Class:
 */

public class SiteEntity implements IUpdatableEntity<Integer>
{
    @Getter private final Integer id;
    @Getter private final String siteName;
    @Getter private final String siteUrl;
    @Getter private final String siteGuid;
    @Getter private final List<Integer> categoriesList;
    private final boolean isMarkedForDeletion;
    @Getter private final Long modificationTime;

    private SiteEntity(SiteEntityBuilder builder)
    {
        id = builder.id;
        siteName = builder.siteName;
        siteUrl = builder.siteUrl;
        siteGuid = builder.siteGuid;
        categoriesList = builder.categoriesList;
        isMarkedForDeletion = builder.isMarkedForDeletion;
        modificationTime = builder.updateTime;
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return isMarkedForDeletion;
    }

    public static class SiteEntityBuilder
    {
        @NonNull private final Integer id;
        @NonNull private final String siteName;
        @NonNull private final String siteUrl;
        @NonNull private final String siteGuid;
        private List<Integer> categoriesList;
        @NonNull private final boolean isMarkedForDeletion;
        @NonNull private final Long updateTime;

        public SiteEntityBuilder(Integer idX, String name, String url, String guidX, boolean delete, Long modifiedTime)
        {
            this.id = idX;
            this.siteName = name;
            this.siteUrl = url;
            this.siteGuid = guidX;
            isMarkedForDeletion = delete;
            updateTime = modifiedTime;
        }

        public SiteEntityBuilder setCatList(List<Integer> catList)
        {
            this.categoriesList = catList;
            return this;
        }

        public SiteEntity build()
        {
            return new SiteEntity(this);
        }
    }
}
