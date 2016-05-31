package com.kritter.kumbaya.countdistinct.set;

import java.util.HashSet;

import com.kritter.kumbaya.countdistinct.common.IKritterCountDistinct;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class SetCount implements IKritterCountDistinct {

    @Getter@Setter
    private HashSet<String> internalSet = null;

    public SetCount() {
        init();
    }
    
    @Override
    public void init() {
        if(internalSet != null){
            internalSet = new HashSet<String>();
        }
        
    }

    @Override
    public void addElement(String element) {
        if(!internalSet.contains(element)){
            internalSet.add(element);
        }
        
    }

    @Override
    public long getCount() {
        return internalSet.size();
    }


}
