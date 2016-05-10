package com.kritter.serving.demand.index;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * The model id index class, keeps campaigns indexed against model id.
 */
@AllArgsConstructor
@EqualsAndHashCode
public class ModelIdIndex implements ISecondaryIndex {
    private final Integer modelId;
}
