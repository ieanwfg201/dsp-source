package com.kritter.entity.pmp;

import lombok.Getter;

/**
 * This class keeps a third party DSP and its corresponding advertiser/agency id data.
 */
public class DSPAndItsAdvertiserAgencyMapping
{
    @Getter
    private Integer id;
    @Getter
    private String dspId;
    @Getter
    private String advertiserId;

    public DSPAndItsAdvertiserAgencyMapping(Integer id,String dspId,String advertiserId)
    {
        this.id = id;
        this.dspId = dspId;
        this.advertiserId = advertiserId;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + this.id.intValue() +  (this.dspId.hashCode()) + (this.advertiserId.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        DSPAndItsAdvertiserAgencyMapping externalObject = (DSPAndItsAdvertiserAgencyMapping) obj;

        if (
            this.id.intValue() == externalObject.id.intValue()    &&
            this.dspId.equalsIgnoreCase(externalObject.dspId)     &&
            this.advertiserId.equals(externalObject.advertiserId)
           )
            return true;

        return false;
    }
}
