package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

/**
 * Date: 9/6/13
 * Class:
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class SampleSecondaryIndexWrapper implements ISecondaryIndexWrapper
{
    private final Set<ISecondaryIndex> secondaryIndexSet;
    private final boolean allTargeted;
}
