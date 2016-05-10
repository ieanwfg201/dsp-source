package models.reporting;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Frequency;
import com.kritter.constants.error.ErrorEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OverallReportingEntity {
    
    @Getter@Setter
    private String start_date = null;
    @Getter@Setter
    private String end_date = null;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static OverallReportingEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        OverallReportingEntity entity = objectMapper.readValue(str, OverallReportingEntity.class);
        return entity;

    }
    public Message validate(){
        Message msg = new Message();
            if(this.start_date == null || "".equals(this.start_date.trim())){
                msg.setError_code(ErrorEnum.START_DATE_INVALID.getId());
                msg.setMsg(ErrorEnum.START_DATE_INVALID.getName());
                return msg;
            }
            if(this.end_date == null || "".equals(this.end_date.trim())){
                msg.setError_code(ErrorEnum.END_DATE_INVALID.getId());
                msg.setMsg(ErrorEnum.END_DATE_INVALID.getName());
                return msg;
            }
        msg.setError_code(ErrorEnum.NO_ERROR.getId());
        msg.setMsg(ErrorEnum.NO_ERROR.getName());
        return msg;
    }
    public ReportingEntity getReportingEntity(){
        ReportingEntity reportingEntity = new ReportingEntity();
        reportingEntity.setStart_time_str(this.getStart_date());
        reportingEntity.setEnd_time_str(this.getEnd_date());
        reportingEntity.setDate_as_dimension(true);
        reportingEntity.setFrequency(Frequency.DATERANGE);
        reportingEntity.setTotal_request(true);
        reportingEntity.setTotal_impression(true);
        reportingEntity.setTotal_click(true);
        reportingEntity.setConversion(true);
        reportingEntity.setSupplyCost(true);
        reportingEntity.setDemandCharges(true);
        reportingEntity.setCtr(true);
        reportingEntity.setClicksr(true);
        reportingEntity.seteCPC(true);
        reportingEntity.setEcpm(true);
        reportingEntity.seteIPC(true);
        reportingEntity.setPagesize(Integer.MAX_VALUE);
        return reportingEntity;
    }
}
