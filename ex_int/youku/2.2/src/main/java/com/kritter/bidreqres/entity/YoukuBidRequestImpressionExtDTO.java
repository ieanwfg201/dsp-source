package com.kritter.bidreqres.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestImpressionExtDTO {
    @JsonProperty("repeat")
    @Setter
    private Integer repeat;
    @JsonIgnore
    public Integer getRepeat(){
        return repeat;
    }
}
