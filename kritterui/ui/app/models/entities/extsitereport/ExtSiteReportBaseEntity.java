package models.entities.extsitereport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import play.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kritter.api.entity.extsitereport.ExtSiteReportEntity;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.kritterui.api.utils.TimeManipulation.TimeManipulator;

public class ExtSiteReportBaseEntity {

    protected static String ALL = "[\"all\"]";
    protected static String NONE = "[\"none\"]";
    protected String start_date = null;
    protected String end_date = null;

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

    protected ExtSiteReportEntity extsitereportEntity = new ExtSiteReportEntity();

    public ExtSiteReportBaseEntity(ExtSiteReportEntity extsitereportEntity) {
        this.extsitereportEntity = extsitereportEntity;
        if (extsitereportEntity == null
                || extsitereportEntity.getStart_time_str() == null
                || "".equals(extsitereportEntity.getStart_time_str())) {
            Date date = DateUtils.addDays(new Date(), -1);
            start_date = TimeManipulator.convertDate(date,
                    "yyyy-MM-dd 00:00:00", "UTC");
        } else {
            this.start_date = extsitereportEntity.getStart_time_str();
        }
        if (extsitereportEntity == null
                || extsitereportEntity.getEnd_time_str() == null
                || "".equals(extsitereportEntity.getEnd_time_str())) {
            Date date = DateUtils.addDays(new Date(), -1);
            end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                    "UTC");
        } else {
            this.end_date = extsitereportEntity.getEnd_time_str();
        }

    }

    public ExtSiteReportBaseEntity() {
        this.extsitereportEntity = new ExtSiteReportEntity();
        Date date = DateUtils.addDays(new Date(), -1);
        start_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                "UTC");
        end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 23:59:59",
                "UTC");
    }

    public void setStart_date(String start_date) {
        extsitereportEntity.setStart_time_str(start_date);
    }

    public String getStart_date() {
        String str = extsitereportEntity.getStart_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", "UTC");
        }
        return str;
    }

    public void setEnd_date(String end_date) {
        extsitereportEntity.setEnd_time_str(end_date);
    }

    public String getEnd_date() {
        String str = extsitereportEntity.getEnd_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", "UTC");
        }
        return str;
    }

    public boolean isTotal_request() {
        return extsitereportEntity.isTotal_request();
    }

    public void setTotal_request(boolean total_request) {
        extsitereportEntity.setTotal_request(total_request);
    }

    public boolean isTotal_impression() {
        return extsitereportEntity.isTotal_impression();
    }

    public void setTotal_impression(boolean total_impression) {
        extsitereportEntity.setTotal_impression(total_impression);
    }

    public boolean isTotal_bidValue() {
        return extsitereportEntity.isTotal_bidValue();
    }

    public void setTotal_bidValue(boolean total_bidValue) {
        extsitereportEntity.setTotal_bidValue(total_bidValue);
    }

    public boolean isTotal_click() {
        return extsitereportEntity.isTotal_click();
    }

    public void setTotal_click(boolean total_click) {
        extsitereportEntity.setTotal_click(total_click);
    }
    
    public boolean isBilledclicks() {
        return extsitereportEntity.isBilledclicks();
    }

    public void setBilledclicks(boolean billedclicks) {
        extsitereportEntity.setBilledclicks(billedclicks);
    }

    public boolean isTotal_win() {
        return extsitereportEntity.isTotal_win();
    }

    public void setTotal_win(boolean total_win) {
        extsitereportEntity.setTotal_win(total_win);
    }

    public boolean isTotal_win_bidValue() {
        return extsitereportEntity.isTotal_win_bidValue();
    }

    public void setTotal_win_bidValue(boolean total_win_bidValue) {
        extsitereportEntity.setTotal_win_bidValue(total_win_bidValue);
    }

    public boolean isTotal_csc() {
        return extsitereportEntity.isTotal_csc();
    }

    public void setTotal_csc(boolean total_csc) {
        extsitereportEntity.setTotal_csc(total_csc);
    }

    public boolean isDemandCharges() {
        return extsitereportEntity.isDemandCharges();
    }

    public void setDemandCharges(boolean demandCharges) {
        extsitereportEntity.setDemandCharges(demandCharges);
    }

    public boolean isSupplyCost() {
        return extsitereportEntity.isSupplyCost();
    }

    public void setSupplyCost(boolean supplyCost) {
        extsitereportEntity.setSupplyCost(supplyCost);
    }

    public boolean isEarning() {
        return extsitereportEntity.isEarning();
    }

    public void setEarning(boolean earning) {
        extsitereportEntity.setEarning(earning);
    }

    public boolean isConversion() {
        return extsitereportEntity.isConversion();
    }

    public void setConversion(boolean conversion) {
        extsitereportEntity.setConversion(conversion);
    }

    public boolean isCpa_goal() {
        return extsitereportEntity.isCpa_goal();
    }
    public void setCpa_goal(boolean cpa_goal) {
        extsitereportEntity.setCpa_goal(cpa_goal);
    }

    public boolean isExchangepayout() {
        return extsitereportEntity.isExchangepayout();
    }
    public void setExchangepayout(boolean exchangepayout) {
        extsitereportEntity.setExchangepayout(exchangepayout);
    }
    public boolean isExchangerevenue() {
        return extsitereportEntity.isExchangerevenue();
    }
    public void setExchangerevenue(boolean exchangerevenue) {
        extsitereportEntity.setExchangerevenue(exchangerevenue);
    }
    public boolean isNetworkpayout() {
        return extsitereportEntity.isNetworkpayout();
    }
    public void setNetworkpayout(boolean networkpayout) {
        extsitereportEntity.setNetworkpayout(networkpayout);
    }
    public boolean isNetworkrevenue() {
        return extsitereportEntity.isNetworkrevenue();
    }
    public void setNetworkrevenue(boolean networkrevenue) {
        extsitereportEntity.setNetworkrevenue(networkrevenue);
    }
    private ObjectMapper objectMapper = new ObjectMapper();

    protected LinkedList<Integer> stringToIdList(String valArrayStr) {
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

    protected List<String> stringToStringIdList(String valArrayStr) {
        List<String> idList = new ArrayList<String>();
        try {
            JsonNode valueNode = objectMapper.readTree(valArrayStr);
            if (valueNode instanceof ArrayNode) {
                ArrayNode valArray = (ArrayNode) valueNode;
                String valString = valArray.toString();
                if (NONE.equalsIgnoreCase(valString)) {
                    idList = null;
                } else if (ALL.equalsIgnoreCase(valString)) {
                    idList = new ArrayList<String>();
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

}
