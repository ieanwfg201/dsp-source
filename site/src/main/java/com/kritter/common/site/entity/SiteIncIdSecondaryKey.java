package com.kritter.common.site.entity;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class SiteIncIdSecondaryKey implements ISecondaryIndex
{
	private final Integer siteIncId;
}
