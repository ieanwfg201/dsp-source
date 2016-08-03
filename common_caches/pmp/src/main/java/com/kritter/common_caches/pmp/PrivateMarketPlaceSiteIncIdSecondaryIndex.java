package com.kritter.common_caches.pmp;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PrivateMarketPlaceSiteIncIdSecondaryIndex implements ISecondaryIndex
{
    private final Integer siteIncId;
}
