package com.kritter.geo.common.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class LatLonRadius {
    @Getter @Setter
    @JsonProperty("lat")
    private double latitude;
    @Getter @Setter
    @JsonProperty("lon")
    private double longitude;
    @Getter @Setter
    @JsonProperty("r")
    private double radius;

}
