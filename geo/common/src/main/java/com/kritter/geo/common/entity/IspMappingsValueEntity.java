package com.kritter.geo.common.entity;

import lombok.Getter;

/**
 * This class utilized to make use of isp_mappings data for
 * updation, deletion, insertion etc. into ui_targeting_isp
 * table with source as isp_mappings table for isp entries.
 */
public class IspMappingsValueEntity
{
    @Getter
    private String ispUiNameWithCountryValue;

    /*1 means marked for deletion, 0 means new entry.*/
    @Getter
    private short isMarkedForDeletion;

    public IspMappingsValueEntity(String ispUiNameWithCountryValue,short isMarkedForDeletion)
    {
        this.ispUiNameWithCountryValue = ispUiNameWithCountryValue;
        this.isMarkedForDeletion = isMarkedForDeletion;
    }
}
