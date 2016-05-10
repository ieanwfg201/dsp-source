package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class CountryUserInterfaceIdSecondaryIndex implements ISecondaryIndex
{
    private final Integer dataSourceCountryId;
}
