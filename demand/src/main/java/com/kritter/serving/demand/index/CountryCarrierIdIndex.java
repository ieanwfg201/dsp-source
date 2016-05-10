package com.kritter.serving.demand.index;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class CountryCarrierIdIndex implements ISecondaryIndex
{
    private final Integer countryCarrierId;
}
