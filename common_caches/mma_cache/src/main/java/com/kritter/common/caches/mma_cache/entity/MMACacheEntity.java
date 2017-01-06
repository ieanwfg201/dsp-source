package com.kritter.common.caches.mma_cache.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@ToString
public class MMACacheEntity implements IUpdatableEntity<String>{
    private static final String CTRL_A = String.valueOf((char)1);

    @Setter
    private boolean markedForDeletion = false;
    @Getter@Setter
    private int supplyid;
    @Getter@Setter
    private String supplycode;
    @Getter@Setter
    private Set<Integer> ui_id;
    @Getter@Setter
    private long lastModified;
    
    public MMACacheEntity(int id, String supplycode, Set<Integer> ui_id, long lastModified, boolean isMarkedForDeletion){
        this.markedForDeletion = isMarkedForDeletion;
        this.supplyid = id;
        this.supplycode=supplycode;
        this.ui_id=ui_id;
        this.lastModified = lastModified;
    }
    
    @Override
    public String getId() {
    	return  this.supplyid+CTRL_A+this.supplycode;
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
