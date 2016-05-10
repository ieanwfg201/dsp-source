package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.IEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * Date: 9/6/13
 * Class:
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class SampleQueryableEntity implements IEntity<Integer>
{
    private final Integer id;
    private final Long version;
    private final List<Integer> valuesList;
    private final int secIndexId;
}
