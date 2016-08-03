package com.kritter.common.caches.iab.index;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This class is adGuid for adEntity, serves as the secondary index to lookup
 * for adEntity.
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class IABIDIndex implements ISecondaryIndex
{
    private final short inetrnalId;
}
