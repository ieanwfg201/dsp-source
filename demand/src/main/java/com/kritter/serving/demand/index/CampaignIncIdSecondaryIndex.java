package com.kritter.serving.demand.index;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Campaign id index to lookup ad ids and hence ad units.
 */

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CampaignIncIdSecondaryIndex implements ISecondaryIndex{

    private final Integer campaignId;

}
