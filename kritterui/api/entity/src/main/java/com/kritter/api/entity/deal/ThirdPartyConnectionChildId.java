package com.kritter.api.entity.deal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Used for dsp id and advertiser id capturing , which are properties of third party connection
 * which are marketplace of dsps or standalone dsp.
 */
@ToString
public class ThirdPartyConnectionChildId
{
    public static final String FETCH_DSP_DATA = "select * from third_party_conn_dsp_mapping where " +
                                                "third_party_conn_id = ?";
    public static final String FETCH_ADV_DATA = "select * from dsp_adv_agency_mapping where " +
                                                "dsp_id = ?";

    @Getter @Setter
    private Boolean fetchDSPData;

    @Getter @Setter
    private Integer id;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private String thirdPartyConnectionGuid;

    public JsonNode toJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
}
