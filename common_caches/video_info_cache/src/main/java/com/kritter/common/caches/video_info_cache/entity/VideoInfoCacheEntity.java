package com.kritter.common.caches.video_info_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.entity.video_props.VideoInfoExt;

import lombok.Setter;
import lombok.ToString;

@ToString
public class VideoInfoCacheEntity implements IUpdatableEntity<Integer>{
    @Setter
    private boolean markedForDeletion = false;
    private VideoInfo videoInfo = new VideoInfo();
    
    public VideoInfoCacheEntity(int id, String guid, String account_guid, int video_size,
            String resourceUri, long lastModified, boolean isMarkedForDeletion,VideoInfoExt vie){
    	videoInfo.setId(id);
    	videoInfo.setGuid(guid);
    	videoInfo.setAccount_guid(account_guid);
    	videoInfo.setLast_modified(lastModified);
    	videoInfo.setResource_uri(resourceUri);
    	videoInfo.setVideo_size(video_size);
        this.markedForDeletion = isMarkedForDeletion;
        videoInfo.setExt(vie);
        
    }
    
    @Override
    public Integer getId() {
        return videoInfo.getId();
    }
    @Override
    public Long getModificationTime() {
        return videoInfo.getLast_modified();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return this.markedForDeletion;
    }
    public VideoInfo getVideoInfo() {
        return this.videoInfo;
    }
}
