package com.kritter.constants;

import lombok.Getter;

/**
 * This class keeps identifiers to define the type of third party demand channel.
 * Example: Bidswitch is a marketplace of DSPs, mediamath,criteo,powerlinks are
 * standalone DSP/Bidder.
 * Add if something new emerges in the market.
 */
public enum ThirdPartyDemandChannel
{
    MARKETPLACE_OF_DSP(1,"This value is to identify third party demand channels which are aggregators or mediatiors " +
                         "of standalone DSPs or bidders"),
    STANDALONE_DSP_BIDDER(2,"This value is to identify third party demand channels which are standalone DSP " +
                            "or Bidders");

    @Getter
    private int code;
    @Getter
    private String description;

    ThirdPartyDemandChannel(int code,String description)
    {
        this.code = code;
        this.description = description;
    }
}
