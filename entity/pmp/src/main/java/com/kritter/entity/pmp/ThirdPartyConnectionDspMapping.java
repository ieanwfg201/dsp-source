package com.kritter.entity.pmp;

import lombok.Getter;

/**
 * This class keeps a third party connection id and one of the seats' dsp id.
 */
public class ThirdPartyConnectionDspMapping
{
    @Getter
    private Integer id;
    @Getter
    private String thirdPartyConnectionId;
    @Getter
    private String dspId;

    public ThirdPartyConnectionDspMapping(Integer id,String thirdPartyConnectionId,String dspId)
    {
        this.id = id;
        this.thirdPartyConnectionId = thirdPartyConnectionId;
        this.dspId = dspId;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + this.id.intValue() + (this.thirdPartyConnectionId.hashCode()) + (this.dspId.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        ThirdPartyConnectionDspMapping externalObject = (ThirdPartyConnectionDspMapping) obj;

        if (
            this.id.intValue() == externalObject.id.intValue() &&
            this.thirdPartyConnectionId.equalsIgnoreCase(externalObject.thirdPartyConnectionId) &&
            this.dspId.equalsIgnoreCase(externalObject.dspId)
           )
            return true;

        return false;
    }
}
