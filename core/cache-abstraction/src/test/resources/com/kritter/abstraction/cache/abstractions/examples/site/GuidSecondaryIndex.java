package com.kritter.abstraction.cache.abstractions.examples.site;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Date: 13-06-2013
 * Class:
 */
@AllArgsConstructor
@EqualsAndHashCode
public class GuidSecondaryIndex implements ISecondaryIndex
{
    private final String guid;
}
