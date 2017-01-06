package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *This object contains any legal, governmental, or industry regulations that apply to the request. The coppa flag 
 *signals whether or not the request falls under the United States Federal Trade Commission’s regulations for the 
 *United States Children’s Online Privacy Protection Act (“COPPA”).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestRegsDTO
{
    /**
     *Flag indicating if this request is subject to the COPPA regulations established by the USA FTC, 
     *where 0 = no, 1 = yes. Refer to Section 7.5 for more information.
     */
    @Setter@Getter
    private Integer coppa;

    @Setter@Getter
    private Object ext;

}
