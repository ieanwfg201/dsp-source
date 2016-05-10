package com.kritter.abstraction.cache.abstractions.examples.site;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

/**
 * Date: 13-June-2013
 * Class:
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class UrlNameSecondaryIndexWrapper implements ISecondaryIndexWrapper
{
    private final Set<ISecondaryIndex> secondaryIndexSet;
    private final boolean allTargeted;
}
