package com.kritter.api.entity.tracking_event;

import java.io.IOException;
import java.util.LinkedList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.Frequency;
import com.kritter.constants.PageConstants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class TrackingEvent {
    @Getter@Setter
    private String start_time_str = "";
    @Getter@Setter
    private String end_time_str = "";
    @Getter@Setter
    private int startindex=PageConstants.start_index;
    @Getter@Setter
    private int pagesize=PageConstants.page_size;

    /** mandatory - default null - null means not required, empty list means all required, integer - adid element in list signifies filters */
    @Getter@Setter
    private LinkedList<Integer> pubId = null; 
    @Getter@Setter
    private LinkedList<Integer> siteId = null;
    @Getter@Setter
    private LinkedList<Integer> extsiteId = null;
    @Getter@Setter
    private LinkedList<Integer> advId = null;
    @Getter@Setter
    private LinkedList<Integer> campaignId = null;
    @Getter@Setter
    private LinkedList<Integer> adId = null;
    @Getter@Setter
    private LinkedList<Integer> countryId = null;
    @Getter@Setter
    private LinkedList<Integer> countryCarrierId = null;
    @Getter@Setter
    private LinkedList<Integer> deviceManufacturerId = null;
    @Getter@Setter
    private LinkedList<Integer> deviceOsId = null;
    @Getter@Setter
    private boolean terminationReason = false;
    @Getter@Setter
    private boolean tevent = false;
    @Getter@Setter
    private boolean teventtype = false;
    @Getter@Setter
    private boolean total_event = true;
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
    private boolean date_as_dimension = false;
    /** mandatory - @see com.kritter.constants.Frequency */
    @Getter@Setter
    private Frequency frequency = Frequency.YESTERDAY;
    @Getter@Setter
    private String csvDelimiter = ",";
    /** set true when Total is required in downloads */
    @Getter@Setter
    private boolean rollup = false;

    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static TrackingEvent getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        TrackingEvent entity = objectMapper.readValue(str, TrackingEvent.class);
        return entity;

    }
}
