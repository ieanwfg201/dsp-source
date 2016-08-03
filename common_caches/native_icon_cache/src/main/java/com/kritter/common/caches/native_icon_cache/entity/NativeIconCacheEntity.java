package com.kritter.common.caches.native_icon_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.native_props.demand.NativeIcon;

import lombok.Setter;
import lombok.ToString;

@ToString
public class NativeIconCacheEntity implements IUpdatableEntity<Integer>{
    @Setter
    private boolean markedForDeletion = false;
    private NativeIcon nativeIcon = new NativeIcon();
    
    public NativeIconCacheEntity(int id, String guid, String account_guid, int icon_size,
            String resourceUri, long lastModified, boolean isMarkedForDeletion){
        nativeIcon.setId(id);
        nativeIcon.setAccount_guid(account_guid);
        nativeIcon.setGuid(guid);
        nativeIcon.setIcon_size(icon_size);
        nativeIcon.setResource_uri(resourceUri);
        nativeIcon.setLast_modified(lastModified);
        this.markedForDeletion = isMarkedForDeletion;
        
    }
    
    @Override
    public Integer getId() {
        return nativeIcon.getId();
    }
    @Override
    public Long getModificationTime() {
        return nativeIcon.getLast_modified();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return this.markedForDeletion;
    }
    public NativeIcon getNativeIcon() {
        return this.nativeIcon;
    }
}
