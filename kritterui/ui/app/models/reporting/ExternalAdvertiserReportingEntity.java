package models.reporting;

import java.io.IOException;
import java.util.LinkedList;

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
public class ExternalAdvertiserReportingEntity {
    
    @Getter@Setter
    private String api_key = null;
    @Getter@Setter
    private String type = null; /*account_stat|campaign_stat|ad_stat*/
    @Getter@Setter
    private String advertiserId = null;
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
    public static ExternalAdvertiserReportingEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ExternalAdvertiserReportingEntity entity = objectMapper.readValue(str, ExternalAdvertiserReportingEntity.class);
        return entity;

    }
    public Message validate(){
        Message msg = new Message();
        if(this.api_key == null || "".equals(this.api_key.trim())){
            msg.setError_code(ErrorEnum.API_KEY_INVALID.getId());
            msg.setMsg(ErrorEnum.API_KEY_INVALID.getName());
            return msg;
        }
        if(this.advertiserId == null || "".equals(this.advertiserId.trim())){
            msg.setError_code(ErrorEnum.ADVERTISER_INVALID.getId());
            msg.setMsg(ErrorEnum.ADVERTISER_INVALID.getName());
            return msg;
        }
        if(this.type == null || "".equals(this.type.trim())){
            msg.setError_code(ErrorEnum.TYPE_INVALID.getId());
            msg.setMsg(ErrorEnum.TYPE_INVALID.getName());
            return msg;
        }
        String tmptype = this.type.trim(); 
        if(!("account_stat".equals(tmptype) || "campaign_stat".equals(tmptype) || "ad_stat".equals(tmptype))){
            msg.setError_code(ErrorEnum.TYPE_INVALID.getId());
            msg.setMsg(ErrorEnum.TYPE_INVALID.getName());
            return msg;
        }
        if("account_stat".equals(tmptype) || "campaign_stat".equals(tmptype) || "ad_stat".equals(tmptype)){
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
        LinkedList<String> advertiserId = new LinkedList<String>();
        advertiserId.add("'"+this.advertiserId+"'");
        reportingEntity.setAdvertiserId(advertiserId);
        reportingEntity.setTotal_impression(true);
        reportingEntity.setTotal_click(true);
        reportingEntity.setCtr(true);
        reportingEntity.setConversion(true);
        reportingEntity.setClicksr(true);
        reportingEntity.seteCPC(true);
        reportingEntity.setDemandCharges(true);
        reportingEntity.setEcpm(true);
        reportingEntity.setPagesize(Integer.MAX_VALUE);
        if("campaign_stat".equals(this.type)){
            reportingEntity.setCampaignId(new LinkedList<Integer>());
            reportingEntity.setTotal_impression_order_sequence(1);
        }
        if("ad_stat".equals(this.type)){
            reportingEntity.setAdId(new LinkedList<Integer>());
            reportingEntity.setTotal_impression_order_sequence(1);
        }
        return reportingEntity;
    }
}
