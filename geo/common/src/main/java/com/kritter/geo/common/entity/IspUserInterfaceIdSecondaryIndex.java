package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class IspUserInterfaceIdSecondaryIndex implements ISecondaryIndex
{
    private final Integer dataSourceIspId;
}
