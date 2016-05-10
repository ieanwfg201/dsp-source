package com.kritter.common.caches.native_screenshot_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.native_props.demand.NativeScreenshot;

import lombok.Setter;

public class NativeScreenshotCacheEntity implements IUpdatableEntity<Integer>{
    @Setter
    private boolean markedForDeletion = false;
    private NativeScreenshot nativeScreenshot = new NativeScreenshot();
    
    public NativeScreenshotCacheEntity(int id, String guid, String account_guid, int ss_size,
            String resourceUri, long lastModified, boolean isMarkedForDeletion){
        nativeScreenshot.setId(id);
        nativeScreenshot.setAccount_guid(account_guid);
        nativeScreenshot.setGuid(guid);
        nativeScreenshot.setSs_size(ss_size);
        nativeScreenshot.setResource_uri(resourceUri);
        nativeScreenshot.setLast_modified(lastModified);
        this.markedForDeletion = isMarkedForDeletion;
        
    }
    
    @Override
    public Integer getId() {
        return nativeScreenshot.getId();
    }
    @Override
    public Long getModificationTime() {
        return nativeScreenshot.getLast_modified();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return this.markedForDeletion;
    }
    
    public NativeScreenshot getNativeScreenshot(){
        return this.nativeScreenshot;
    }
}
