package com.kritter.abstraction.cache.abstractions.examples.site;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Date: 13-06-2013
 * Class:
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UrlNameSecondaryIndex implements ISecondaryIndex
{
    private final String name;
    private final String url;
}
