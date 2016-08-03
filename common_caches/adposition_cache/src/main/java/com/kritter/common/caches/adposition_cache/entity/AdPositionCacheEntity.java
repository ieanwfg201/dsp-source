package com.kritter.common.caches.adposition_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AdPositionCacheEntity implements IUpdatableEntity<String>{
    private static final String CTRL_A = String.valueOf((char)1);

    @Setter
    private boolean markedForDeletion = false;
    @Getter@Setter
    private int pubIncId;
    @Getter@Setter
    private String adposid;
    @Getter@Setter
    private int internalid;
    @Getter@Setter
    private long lastModified;
    
    public AdPositionCacheEntity(int pubIncId, String adposid, int internalid, long lastModified, boolean isMarkedForDeletion){
        this.markedForDeletion = isMarkedForDeletion;
        this.pubIncId = pubIncId;
        this.adposid=adposid;
        this.internalid=internalid;
        this.lastModified = lastModified;
    }
    
    @Override
    public String getId() {
    	return  this.pubIncId+CTRL_A+this.adposid;
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
