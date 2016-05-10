package com.kritter.serving.demand.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class has attributes for external supply received in bidrequest from an ad-exchange.
 * The matching criteria should be on checking these attributes for equality without
 * worrying about the case.
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalSupplyAttributes
{
    @JsonProperty("intid")
    @Setter
    private Integer internalSupplyId;
    @JsonProperty("id")
    @Setter
    private String externalSupplyId;
    @JsonProperty("name")
    @Setter
    private String externalSupplyName;
    @JsonProperty("domain")
    @Setter
    private String externalSupplyDomain;

    @JsonIgnore
    public Integer getInternalSupplyId()
    {
        return internalSupplyId;
    }

    @JsonIgnore
    public String getExternalSupplyId()
    {
        return externalSupplyId;
    }

    @JsonIgnore
    public String getExternalSupplyName()
    {
        return externalSupplyName;
    }

    @JsonIgnore
    public String getExternalSupplyDomain()
    {
        return externalSupplyDomain;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + this.internalSupplyId.intValue() + this.externalSupplyId.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        ExternalSupplyAttributes externalObject = (ExternalSupplyAttributes) obj;

        if (this.internalSupplyId.equals(externalObject.internalSupplyId))
            return true;

        return false;
    }
}