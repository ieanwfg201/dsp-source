package com.kritter.bidrequest.entity.common.openrtbversion2_3;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This object contains any legal, governmental, or industry regulations that apply to the request. The
 * coppa flag signals whether or not the request falls under the United States Federal Trade Commission’s
 * regulations for the United States Children’s Online Privacy Protection Act (“COPPA”). Refer to Section
 * 7.1 for more information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestRegsDTO
{
    /**
     * Flag indicating if this request is subject to the COPPA
     * regulations established by the USA FTC, where 0 = no, 1 = yes.
     */
    @JsonProperty("coppa")
    @Setter
    private Integer coppa;

    @JsonIgnore
    public Integer getCoppa(){
        return coppa;
    }

    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return coppa;
    }
}
