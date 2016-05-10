package com.kritter.utils.uuid.mac;

import com.kritter.utils.uuid.IUUIDGenerator;


public class SingletonUUIDGenerator extends UUIDGenerator implements IUUIDGenerator{
    
    private static SingletonUUIDGenerator _instance = null;

    private SingletonUUIDGenerator(){

    }
    
    public static synchronized SingletonUUIDGenerator getSingletonUUIDGenerator(){
        if(_instance == null){
            _instance =  new SingletonUUIDGenerator();
        } 
        return _instance;
    }
}
