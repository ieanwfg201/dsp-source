package com.kritter.common.caches.adxbasedexchanges_metadata_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AdxBasedMetadataCacheEntity implements IUpdatableEntity<String>{
        @Setter
    private boolean markedForDeletion = false;
    @Getter@Setter
    private long lastModified;
    @Getter@Setter
    private AdxBasedExchangesMetadata abem;
    
    public AdxBasedMetadataCacheEntity(AdxBasedExchangesMetadata abem, long lastModified, boolean isMarkedForDeletion){
        this.markedForDeletion = isMarkedForDeletion;
        this.abem = abem;
        this.lastModified = lastModified;
    }
    
    @Override
    public String getId() {
    	return  this.abem.getPubIncId()+"";
    }
    @Override
    public Long getModificationTime() {
        return this.lastModified;
    }

    @Override
    public boolean isMarkedForDeletion() {
        return this.markedForDeletion;
    }
}
