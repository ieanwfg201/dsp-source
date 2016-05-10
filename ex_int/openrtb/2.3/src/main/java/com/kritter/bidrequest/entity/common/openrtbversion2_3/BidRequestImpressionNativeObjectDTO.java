package com.kritter.bidrequest.entity.common.openrtbversion2_3;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestImpressionNativeObjectDTO {

	@JsonProperty("request")
    @Setter
	private String request;

    @JsonProperty("ver")
    @Setter
    private String ver;

    @JsonProperty("api")
    @Setter
    private Integer[] api;

    @JsonProperty("battr")
    @Setter
    private Integer[] battr;

    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
	public String getRequest() {
		return request;
	}
    @JsonIgnore
    public String getVer() {
        return ver;
    }
    @JsonIgnore
    public Integer[] getApi() {
        return api;
    }
    @JsonIgnore
    public Integer[] getBattr() {
        return battr;
    }
    @JsonIgnore

    public Object getExtensionObject(){
        return extensionObject;
    }
}
