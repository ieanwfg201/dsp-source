package com.kritter.device.index;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * This class is used for creating secondary index as manufacturer id
 * in combination with model id.
 * Used in an ad exchange request's handset identification.
 * Manufacturer cache is looked up with key as manufacturer name and
 * id is retrieved,same with model cache, once ids are retrieved
 * both are used in combination to lookup the actual handset internal
 * id for usage in the application flow.
 */

@AllArgsConstructor
@EqualsAndHashCode
public class HandsetMakeModelSecondaryIndex implements ISecondaryIndex
{
    private int manufacturerId;
    private int modelId;
}
