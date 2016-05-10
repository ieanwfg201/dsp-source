package com.kritter.abstraction.cache.interfaces;

import com.kritter.abstraction.cache.utils.exceptions.RefreshException;

/**
 * Date: 8-June-2013<br></br>
 * Class: Every reloadable ICache should implement this interface<br></br>
 * &nbsp;&nbsp; **Note that the framework takes care of creating the timer for refresh intervals for Caches**
 */
public interface IRefreshable extends IStats, ICache
{
    // Derived class to implement its own refresh
    public void refresh() throws RefreshException;

    // Derived class to specify the refresh interval
    // Example: Abstract class implements this and provides a configurable option
    public long getRefreshInterval();
}
