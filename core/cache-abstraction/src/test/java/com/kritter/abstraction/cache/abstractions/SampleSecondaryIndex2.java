package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Date: 13-06-2013
 * Class:
 */
@AllArgsConstructor
@EqualsAndHashCode
public class SampleSecondaryIndex2 implements ISecondaryIndex
{
    @Getter private final Integer mockId;
}
