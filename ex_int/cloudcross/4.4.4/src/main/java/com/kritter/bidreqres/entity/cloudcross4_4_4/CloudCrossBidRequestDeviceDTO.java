package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestGeoDTO;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudCrossBidRequestDeviceDTO extends BidRequestDeviceDTO {
    @JsonProperty("uuid_type")
    @Setter
    private String uuidType;

    @JsonProperty("uuid")
    @Setter
    private String uuid;
    @JsonProperty("geo")
    @Setter
    private CloudCrossBidRequestGeoDTO geoObject;

    public String getUuidType() {
        return uuidType;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public CloudCrossBidRequestGeoDTO getGeoObject() {
        return geoObject;
    }
}