package models.entities.logpages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import play.Logger;
import play.Play;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kritter.api.entity.log.LogEntity;
import com.kritter.kritterui.api.utils.TimeManipulation.TimeManipulator;

public class LogPagesFormEntity {
	private static String timezoneid = Play.application().configuration().getString("timezoneid");
    protected static String ALL = "[\"all\"]";
    protected static String NONE = "[\"none\"]";

    
    private LogEntity logEntity = null;
    private String event="";
    private String start_date = null;
    private String end_date = null;
    private String start_time_str="";
    private String end_time_str="";
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Integer> pubId = new LinkedList<Integer>();
    private List<String> advertiserId = new LinkedList<String>();
    
    
    public LogPagesFormEntity(){
        this.logEntity = new LogEntity();
        Date date = DateUtils.addDays(new Date(), -1);
        start_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                timezoneid);
        end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 23:59:59",
                timezoneid);
    }
    protected List<Integer> stringToIdList(String valArrayStr) {
        List<Integer> idList = new ArrayList<Integer>();
        try {
            JsonNode valueNode = objectMapper.readTree(valArrayStr);
            if (valueNode instanceof ArrayNode) {
                ArrayNode valArray = (ArrayNode) valueNode;
                String valString = valArray.toString();
                if (NONE.equalsIgnoreCase(valString)) {
                    idList = null;
                } else if (ALL.equalsIgnoreCase(valString)) {
                    idList = new ArrayList<Integer>();
                } else {
                    Iterator<JsonNode> values = valArray.iterator();
                    JsonNode tmp = null;
                    while (values.hasNext()) {
                        tmp = values.next();
                        idList.add(tmp.asInt());
                    }
                }
            }
        } catch (IOException e) {
            Logger.error(e.getMessage(), e);
        }
        return idList;
    }
    protected LinkedList<Integer> stringToIdLinkedList(String valArrayStr) {
        LinkedList<Integer> idList = new LinkedList<Integer>();
        try {
            JsonNode valueNode = objectMapper.readTree(valArrayStr);
            if (valueNode instanceof ArrayNode) {
                ArrayNode valArray = (ArrayNode) valueNode;
                String valString = valArray.toString();
                if (NONE.equalsIgnoreCase(valString)) {
                    idList = null;
                } else if (ALL.equalsIgnoreCase(valString)) {
                    idList = new LinkedList<Integer>();
                } else {
                    Iterator<JsonNode> values = valArray.iterator();
                    JsonNode tmp = null;
                    while (values.hasNext()) {
                        tmp = values.next();
                        idList.add(tmp.asInt());
                    }
                }
            }
        } catch (IOException e) {
            Logger.error(e.getMessage(), e);
        }
        return idList;
    }

    protected String idListToString(List<Integer> idList) {
        ArrayNode result = new ArrayNode(JsonNodeFactory.instance);
        if (idList == null)
            result.add("none");
        else if (idList.size() == 0)
            result.add("all");
        else {
            for (Integer val : idList) {
                result.add(val);
            }
        }
        return result.toString();
    }

    protected String stringIdListToString(List<String> idList) {
        ArrayNode result = new ArrayNode(JsonNodeFactory.instance);
        if (idList == null)
            result.add("none");
        else if (idList.size() == 0)
            result.add("all");
        else {
            for (String val : idList) {
                result.add(val);
            }
        }
        return result.toString();
    }

    protected String stringIdListToString(LinkedList<String> idList) {
        ArrayNode result = new ArrayNode(JsonNodeFactory.instance);
        if (idList == null)
            result.add("none");
        else if (idList.size() == 0)
            result.add("all");
        else {
            for (String val : idList) {
                result.add(val);
            }
        }
        return result.toString();
    }
    protected LinkedList<String> stringToStringIdList(String valArrayStr) {
        LinkedList<String> idList = new LinkedList<String>();
        try {
            JsonNode valueNode = objectMapper.readTree(valArrayStr);
            if (valueNode instanceof ArrayNode) {
                ArrayNode valArray = (ArrayNode) valueNode;
                String valString = valArray.toString();
                if (NONE.equalsIgnoreCase(valString)) {
                    idList = null;
                } else if (ALL.equalsIgnoreCase(valString)) {
                    idList = new LinkedList<String>();
                } else {
                    Iterator<JsonNode> values = valArray.iterator();
                    JsonNode tmp = null;
                    while (values.hasNext()) {
                        tmp = values.next();
                        idList.add("'" + tmp.asText() + "'");
                    }
                }
            }
        } catch (IOException e) {
            Logger.error(e.getMessage(), e);
        }
        return idList;
    }

    public String getEvent() {
        LinkedList<String> l = logEntity.getEvent();
        if(l.size()<1){
            return "";
        }
        return l.get(0);
    }
    public void setEvent(String event) {
        if(event == null || "".equals(event)){
            return;
        }
        LinkedList<String> l = new LinkedList<String>();
        l.add(event);
        logEntity.setEvent(l);
    }
    public String getStart_time_str() {
        return start_time_str;
    }
    public void setStart_time_str(String start_time_str) {
        this.start_time_str = start_time_str;
    }
    public String getEnd_time_str() {
        return end_time_str;
    }
    public void setEnd_time_str(String end_time_str) {
        this.end_time_str = end_time_str;
    }
    public LogEntity getLogEntity() {
        return logEntity;
    }
    public void setLogEntity(LogEntity logEntity) {
        this.logEntity = logEntity;
    }
    public int getPagesize() {
        return logEntity.getPagesize();
    }

    public void setPagesize(int pagesize) {
        this.logEntity.setPagesize(pagesize);
    } 
    
    public int getStartindex() {
        return this.logEntity.getStartindex();
    }
 
    public void setStartindex(int startindex) {
        if(startindex > 0){
            this.logEntity.setStartindex( startindex -1);
        }else{
            this.logEntity.setStartindex(0);
        }
    }
    private static String padZero(int a) {
        if (a < 10) {
            return "0" + a;
        } else {
            return "" + a;
        }
    }

    private static String localeStringConversion(String str) {
        /* format yyyy-MM-dd hh:mm:ss from mm/dd/yyyy hh:mm:ss */
        if (str == null || "".equals(str)) {
            return null;
        }
        String strSplit[] = str.split(" ");
        String dateStr[] = strSplit[0].split("/");
        int m = Integer.parseInt(dateStr[0]);
        int d = Integer.parseInt(dateStr[1]);
        int y = Integer.parseInt(dateStr[2]);
        String timeStr[] = strSplit[1].split(":");
        int offset = 0;
        if (strSplit[2].equals("PM")) {
            offset = 12;
        }
        int h = Integer.parseInt(timeStr[0]);
        if (offset == 0 && h == 12) {
            h = 0;
        }
        h = h + offset;
        int min = Integer.parseInt(timeStr[1]);
        int s = Integer.parseInt(timeStr[2]);
        return y + "-" + padZero(m) + "-" + padZero(d) + " " + padZero(h) + ":"
                + padZero(min) + ":" + padZero(s);
    }

    public void setStart_date(String start_date) {
        logEntity.setStart_time_str(start_date);
    }

    public String getStart_date() {
        String str = logEntity.getStart_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", timezoneid);
        }
        return str;
    }

    public void setEnd_date(String end_date) {
        logEntity.setEnd_time_str(end_date);
    }

    public String getEnd_date() {
        String str = logEntity.getEnd_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", timezoneid);
        }
        return str;
    }
    public String getPublishers() {
        return idListToString(this.getPubId());
    }

    public void setPublishers(String publishers) { 
        this.setPubId(stringToIdList(publishers));
    }

    public String getSites() {
        return idListToString(logEntity.getSiteId());
    }

    public void setSites(String sites) { 
        logEntity.setSiteId(stringToIdLinkedList(sites));
    } 
    public String getExtsites() {
        return idListToString(logEntity.getExt_supply_attr_internal_id());
    }

    public void setExtsites(String extsites) { 
        logEntity.setExt_supply_attr_internal_id(stringToIdLinkedList(extsites));
    } 
    public String getSupply_source_type() {
        return idListToString(logEntity.getSupply_source_type());
    }
    public List<Integer> getPubId() {
        return pubId;
    }
    public void setPubId(List<Integer> pubId) {
        this.pubId = pubId;
    }
    public String getAdvertisers() {
        return stringIdListToString(this.getAdvertiserId());
    }

    public void setAdvertisers(String advertisers) { 
        this.setAdvertiserId(stringToStringIdList(advertisers));
    }

    public String getCampaigns() {
        return idListToString(logEntity.getCampaignId());
    }

    public void setCampaigns(String campaigns) { 
        logEntity.setCampaignId(stringToIdLinkedList(campaigns));
    }

    public String getAds() {
        return idListToString(logEntity.getAdId());
    }

    public void setAds(String ads) { 
        logEntity.setAdId(stringToIdLinkedList(ads));
    }

    public String getCountry() {
        return idListToString(logEntity.getCountryId());
    }
    
    public void setCountry(String country) { 
        logEntity.setCountryId(stringToIdLinkedList(country));
    }

    public String getCarrier() {
        return idListToString(logEntity.getCountryCarrierId());
    }

    public void setCarrier(String carrier) { 
        logEntity.setCountryCarrierId(stringToIdLinkedList(carrier));
    }

    public String getOs() {
        return idListToString(logEntity.getDeviceOsId());
    }

    public void setOs(String os) { 
        logEntity.setDeviceOsId(stringToIdLinkedList(os));
    }

    public String getBrowser() {
        return idListToString(logEntity.getDeviceBrowserId());
    }

    public void setBrowser(String browser) { 
        logEntity.setDeviceBrowserId(stringToIdLinkedList(browser));
    }

    public String getBrand() {
        return idListToString(logEntity.getDeviceManufacturerId());
    }

    public void setBrand(String brand) { 
        logEntity.setDeviceManufacturerId(stringToIdLinkedList(brand));
    }

    public String getModel() {
        return idListToString(logEntity.getDeviceModelId());
    }

    public void setModel(String model) { 
        logEntity.setDeviceModelId(stringToIdLinkedList(model));
    }
    public List<String> getAdvertiserId() {
        return advertiserId;
    }
    public void setAdvertiserId(List<String> advertiserId) {
        this.advertiserId = advertiserId;
    }


}
