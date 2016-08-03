package com.kritter.postimpression.cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * This class represents url alias entity in system.
 */

@ToString
public class UrlAliasEntity implements IUpdatableEntity<Integer>{

    @Getter
    private Integer id;
    @Getter
    private String actualUrl;
    @Getter
    private String aliasUrl;
    @Getter
    private boolean isMarkedForDeletion;
    @Getter
    private Long modificationTime;
    @Getter
    private Timestamp createdOn;

    public UrlAliasEntity(UrlAliasBuilder urlAliasBuilder){

        this.id = urlAliasBuilder.id;
        this.actualUrl = urlAliasBuilder.actualUrl;
        this.aliasUrl = urlAliasBuilder.aliasUrl;
        this.isMarkedForDeletion = urlAliasBuilder.isMarkedForDeletion;
        this.modificationTime = urlAliasBuilder.modificationTime;
        this.createdOn = urlAliasBuilder.createdOn;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id
                + (this.actualUrl != null ? this.actualUrl.hashCode() : 0)
                + (this.aliasUrl != null ? this.aliasUrl.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || getClass() != obj.getClass())
            return false;

        UrlAliasEntity externalObject = (UrlAliasEntity) obj;

        if (this.id.equals(externalObject.id)
                && this.aliasUrl.equals(externalObject.aliasUrl))
            return true;

        return false;
    }

    @Override
    public boolean isMarkedForDeletion() {
        return this.isMarkedForDeletion;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public static class UrlAliasBuilder{

        private final Integer id;
        private final String actualUrl;
        private final String aliasUrl;
        private final boolean isMarkedForDeletion;
        private final Long modificationTime;
        private final Timestamp createdOn;

        public UrlAliasBuilder(Integer id,String actualUrl,String aliasUrl,
                boolean isMarkedForDeletion,Long modificationTime,Timestamp createdOn){

            this.id = id;
            this.actualUrl = actualUrl;
            this.aliasUrl = aliasUrl;
            this.isMarkedForDeletion = isMarkedForDeletion;
            this.modificationTime = modificationTime;
            this.createdOn = createdOn;
        }

        public UrlAliasEntity build(){
            return new UrlAliasEntity(this);
        }

    }


}
