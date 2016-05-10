package com.kritter.geo.common.entity;

import lombok.Getter;

import java.util.Set;

/**
 * This class contains detected information required for a zipcode.
 * Fields could be zipcode, accuracy,detected lat-long and distance
 * the detected latlong.
 */
public class ZipCodeInfoDetected
{
    @Getter
    private Set<String> zipCodeSet;
    @Getter
    private double latitude;
    @Getter
    private double longitude;
    @Getter
    private String countryCode;
    @Getter
    private short accuracy;

    public ZipCodeInfoDetected(Set<String> zipCodeSet,
                               double latitude,
                               double longitude,
                               String countryCode,
                               short accuracy)
    {
        this.zipCodeSet = zipCodeSet;
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = countryCode;
        this.accuracy = accuracy;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("zipCodeSet: ");
        sb.append(zipCodeSet);
        sb.append(", lat: ");
        sb.append(latitude);
        sb.append(",lon: ");
        sb.append(longitude);
        sb.append(", countryCode: ");
        sb.append(countryCode);

        return sb.toString();
    }
}