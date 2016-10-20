package com.kritter.common.caches.banner_upload_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadBanner;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class BannerUploadCacheEntity implements IUpdatableEntity<String>{
    private static final String CTRL_A = String.valueOf((char)1);

    @Setter
    private boolean markedForDeletion = false;
    @Getter@Setter
    private long lastModified;
    @Getter@Setter
    private MaterialUploadBanner mub;
    
    public BannerUploadCacheEntity(MaterialUploadBanner mub, long lastModified, boolean isMarkedForDeletion){
        this.markedForDeletion = isMarkedForDeletion;
        this.mub = mub;
        this.lastModified = lastModified;
    }
    
    @Override
    public String getId() {
    	return  this.mub.getPubIncId()+CTRL_A+this.mub.getBannerId();
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
