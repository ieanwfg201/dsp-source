package com.kritter.geo.common.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class keeps three letter country code to two letter country code mapping.
 */
@ToString
@EqualsAndHashCode(of = {"threeLetterCountryCode"})
public class CountryCodesEntity implements IUpdatableEntity<String>{

    @Getter
    private String twoLetterCountryCode;
    @Getter
    private String threeLetterCountryCode;

    private Long modificationTime;
    private boolean isMarkedForDeletion;

    public CountryCodesEntity(
                              String twoLetterCountryCode,
                              String threeLetterCountryCode,
                              Long modificationTime,
                              boolean isMarkedForDeletion
                             )
    {
        this.twoLetterCountryCode = twoLetterCountryCode;
        this.threeLetterCountryCode = threeLetterCountryCode;
        this.modificationTime = modificationTime;
        this.isMarkedForDeletion = isMarkedForDeletion;
    }

    @Override
    public Long getModificationTime() {
        return modificationTime;
    }

    @Override
    public boolean isMarkedForDeletion() {
        return isMarkedForDeletion;
    }

    @Override
    public String getId() {
        return threeLetterCountryCode;
    }
}
