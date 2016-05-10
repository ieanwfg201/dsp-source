package com.kritter.abstraction.cache.entities;

import com.kritter.abstraction.cache.interfaces.ICache;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CachePool {
    private Map<String, ICache> cacheMap;

    public CachePool(List<ICache> caches) {
        cacheMap = new HashMap<String, ICache>();
        for(ICache cache : caches) {
            cacheMap.put(cache.getName(), cache);
        }
    }

    public ICache getCache(String cacheName) {
        return cacheMap.get(cacheName);
    }
}
