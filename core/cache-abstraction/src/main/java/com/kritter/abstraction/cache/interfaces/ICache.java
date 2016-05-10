package com.kritter.abstraction.cache.interfaces;

import java.util.Properties;

/**
 * The basic cache interface. The cache is supposed to store data for the application.
 * Data source could be anything ranging from file, database, network service, etc.
 * This is the stateful part of the application. Caches contain all the state related information.
 */
public interface ICache {
    public String getName();

    /**
     * Relinquishes any resources being held by the cache. Is called when the application is
     * shutting down.
     */
    public void destroy();
}
