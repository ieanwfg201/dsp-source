package com.kritter.serving.demand.index;

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
public class AdGuidIndex implements ISecondaryIndex
{
    private final String adGuid;
}
