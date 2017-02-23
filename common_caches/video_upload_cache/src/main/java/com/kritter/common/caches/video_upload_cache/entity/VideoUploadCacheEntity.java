package com.kritter.common.caches.video_upload_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadVideo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class VideoUploadCacheEntity implements IUpdatableEntity<String>{
    private static final String CTRL_A = String.valueOf((char)1);

    @Setter
    private boolean markedForDeletion = false;
    @Getter@Setter
    private long lastModified;
    @Getter@Setter
    private MaterialUploadVideo muv;

    public VideoUploadCacheEntity(MaterialUploadVideo muv, long lastModified, boolean isMarkedForDeletion){
        this.markedForDeletion = isMarkedForDeletion;
        this.muv = muv;
        this.lastModified = lastModified;
    }

    @Override
    public String getId() {
    	return  this.muv.getPubIncId()+CTRL_A+this.muv.getAdId()+CTRL_A+this.muv.getVideoInfoId();
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
