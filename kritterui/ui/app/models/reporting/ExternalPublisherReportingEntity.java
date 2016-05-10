package models.reporting;

import java.io.IOException;
import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Frequency;
import com.kritter.constants.error.ErrorEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalPublisherReportingEntity {
    
    @Getter@Setter
    private String api_key = null;
    @Getter@Setter
    private String type = null; /*account_stat|site_stat|site_country_stat*/
    @Getter@Setter@JsonIgnore
    private int pubId = 0;
    
    @Getter@Setter
    private String publisherId = null;
    @Getter@Setter
    private String start_date = null;
    @Getter@Setter
    private String end_date = null;
    
    @Getter@Setter
    private String freq = null;
    

    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ExternalPublisherReportingEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ExternalPublisherReportingEntity entity = objectMapper.readValue(str, ExternalPublisherReportingEntity.class);
        return entity;

    }
    public Message validate(){
        Message msg = new Message();
        if(this.api_key == null || "".equals(this.api_key.trim())){
            msg.setError_code(ErrorEnum.API_KEY_INVALID.getId());
            msg.setMsg(ErrorEnum.API_KEY_INVALID.getName());
            return msg;
        }
        if(this.publisherId == null || "".equals(this.publisherId.trim())){
            msg.setError_code(ErrorEnum.PUBLISHER_INVALID.getId());
            msg.setMsg(ErrorEnum.PUBLISHER_INVALID.getName());
            return msg;
        }
        if(this.type == null || "".equals(this.type.trim())){
            msg.setError_code(ErrorEnum.TYPE_INVALID.getId());
            msg.setMsg(ErrorEnum.TYPE_INVALID.getName());
            return msg;
        }
        String tmptype = this.type.trim(); 
        if(!("account_stat".equals(tmptype) || "site_stat".equals(tmptype) || "site_country_stat".equals(tmptype))){
            msg.setError_code(ErrorEnum.TYPE_INVALID.getId());
            msg.setMsg(ErrorEnum.TYPE_INVALID.getName());
            return msg;
        }
        if("account_stat".equals(tmptype) || "site_stat".equals(tmptype)
                || "site_country_stat".equals(tmptype)){
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
        }
        if(this.freq != null && !"HOURLY".equals(this.freq.trim())){
            msg.setError_code(ErrorEnum.INCORRECTFREQUENCY.getId());
            msg.setMsg(ErrorEnum.INCORRECTFREQUENCY.getName());
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
        reportingEntity.setDate_as_dimension(false);
        reportingEntity.setFrequency(Frequency.DATERANGE);
        if(this.freq != null && "HOURLY".equals(this.freq.trim())){
            reportingEntity.setFrequency(Frequency.ADMIN_INTERNAL_HOURLY);
        }
        LinkedList<Integer> pubId = new LinkedList<Integer>();
        pubId.add(this.pubId);
        reportingEntity.setPubId(pubId);
        reportingEntity.setTotal_request(true);
        reportingEntity.setTotal_request_order_sequence(1);
        reportingEntity.setTotal_impression(true);
        reportingEntity.setFr(true);
        reportingEntity.setTotal_click(true);
        reportingEntity.setCtr(true);
        reportingEntity.seteIPC(true);
        reportingEntity.setSupplyCost(true);
        reportingEntity.seteIPM(true);
        reportingEntity.setPagesize(Integer.MAX_VALUE);
        if("site_stat".equals(this.type)){
            reportingEntity.setSiteId(new LinkedList<Integer>());
            reportingEntity.setReturnGuid(true);
        }
        if("site_country_stat".equals(this.type)){
            reportingEntity.setSiteId(new LinkedList<Integer>());
            reportingEntity.setCountryId(new LinkedList<Integer>());
            reportingEntity.setReturnGuid(true);
        }
        return reportingEntity;
    }
}
