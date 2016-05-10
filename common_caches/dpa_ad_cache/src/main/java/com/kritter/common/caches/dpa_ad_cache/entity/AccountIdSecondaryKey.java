package com.kritter.common.caches.dpa_ad_cache.entity;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Secondary index as account id for demand partner api ad metadata.
 */
@AllArgsConstructor
@EqualsAndHashCode
public class AccountIdSecondaryKey implements ISecondaryIndex
{
    private final String accountGuid;
}