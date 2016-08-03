package com.kritter.postimpression.cache.index;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This class is secondary index for url alias cache,
 * url_alias to id.
 */

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UrlAliasSecondaryIndex implements ISecondaryIndex {

    private final String urlAlias;

}

