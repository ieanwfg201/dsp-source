package com.kritter.entity.ad_stats;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.kritter.constants.ChartType;
import com.kritter.constants.Frequency;
import com.kritter.constants.PageConstants;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AdStats
{

	@Getter@Setter
	private List<Integer> advId = null; 
	@Getter@Setter
	private List<Integer> campaignId = null; 
	@Getter@Setter
	private List<Integer> adId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    @Getter@Setter
    private boolean adId_clickable = false;
    /** metrics set to true if required */
    @Getter@Setter
    private boolean no_fill_reason_name = true;
    @Getter@Setter
    private boolean count = true;
    @Getter@Setter
    private String csvDelimiter = ",";
    @Getter@Setter
    private int startindex = PageConstants.start_index;
    /** Number of records to return @ see com.kritter.constants.PageConstants*/
    @Getter@Setter
    private int pagesize = PageConstants.page_size;
    /** mandatory - YYYY-MM-DD HH:00:00 format */
    @Getter@Setter
    private String start_time_str = null; 
    /** mandatory - YYYY-MM-DD HH:00:00 format */
    @Getter@Setter
    private String end_time_str = null; 
    /** mandatory - default TimeZone */
    @Getter@Setter
    private String timezone = "UTC";
    /** mandatory - default */
    @Getter@Setter
    private String date_format = "yyyy-MM-dd HH:00:0";
    /** default */
    @Getter@Setter
    private String end_date_format = "yyyy-MM-dd 23:59:59";
    /** mandatory - true if report to be split by date*/
    @Getter@Setter
    private boolean date_as_dimension = true;
    /** mandatory - @see com.kritter.constants.Frequency */
    @Getter@Setter
    private Frequency frequency = Frequency.YESTERDAY;
    /** mandatory - default TABLE - @see com.kritter.constants.ChartType */
    @Getter@Setter
    private ChartType chartType = ChartType.TABLE;



    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AdStats getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static AdStats getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	AdStats entity = objectMapper.readValue(str, AdStats.class);
        return entity;

    }
}
