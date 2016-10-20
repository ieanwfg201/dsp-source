package com.kritter.common.caches.advinfo_upload_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadAdvInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AdvInfoUploadCacheEntity implements IUpdatableEntity<String>{
    private static final String CTRL_A = String.valueOf((char)1);

    @Setter
    private boolean markedForDeletion = false;
    @Getter@Setter
    private long lastModified;
    @Getter@Setter
    private MaterialUploadAdvInfo mua;
    
    public AdvInfoUploadCacheEntity(MaterialUploadAdvInfo mua, long lastModified, boolean isMarkedForDeletion){
        this.markedForDeletion = isMarkedForDeletion;
        this.mua = mua;
        this.lastModified = lastModified;
    }
    
    @Override
    public String getId() {
    	return  this.mua.getPubIncId()+CTRL_A+this.mua.getAdvIncId();
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
