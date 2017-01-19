package models.entities.reporting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import play.Logger;
import play.Play;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.kritterui.api.utils.TimeManipulation.TimeManipulator;

public class BaseReportEntity {
	private static String timezoneid = Play.application().configuration().getString("timezoneid");

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

    protected ReportingEntity reportingEntity = new ReportingEntity();

    public BaseReportEntity(ReportingEntity reportingEntity) {
        this.reportingEntity = reportingEntity;
        if (reportingEntity == null
                || reportingEntity.getStart_time_str() == null
                || "".equals(reportingEntity.getStart_time_str())) {
            Date date = DateUtils.addDays(new Date(), -1);
            start_date = TimeManipulator.convertDate(date,
                    "yyyy-MM-dd 00:00:00", timezoneid);
        } else {
            this.start_date = reportingEntity.getStart_time_str();
        }
        if (reportingEntity == null
                || reportingEntity.getEnd_time_str() == null
                || "".equals(reportingEntity.getEnd_time_str())) {
            Date date = DateUtils.addDays(new Date(), -1);
            end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                    timezoneid);
        } else {
            this.end_date = reportingEntity.getEnd_time_str();
        }

    }

    public BaseReportEntity() {
        this.reportingEntity = new ReportingEntity();
        Date date = DateUtils.addDays(new Date(), -1);
        start_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                timezoneid);
        end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 23:59:59",
                timezoneid);
    }

    public void setStart_date(String start_date) {
        reportingEntity.setStart_time_str(start_date);
    }

    public String getStart_date() {
        String str = reportingEntity.getStart_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", timezoneid);
        }
        return str;
    }

    public void setEnd_date(String end_date) {
        reportingEntity.setEnd_time_str(end_date);
    }

    public String getEnd_date() {
        String str = reportingEntity.getEnd_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", timezoneid);
        }
        return str;
    }
    public boolean isSelectallmetric() {
        return reportingEntity.isSelectallmetric();
    }
    public void setSelectallmetric(boolean selectallmetric) {
        reportingEntity.setSelectallmetric(selectallmetric);
    }
    public boolean isTotal_request() {
        return reportingEntity.isTotal_request();
    }

    public void setTotal_request(boolean total_request) {
        reportingEntity.setTotal_request(total_request);
    }
    public boolean isTotal_request_to_dsp() {
        return reportingEntity.isTotal_request_to_dsp();
    }

    public void setTotal_request_to_dsp(boolean total_request_to_dsp) {
        reportingEntity.setTotal_request_to_dsp(total_request_to_dsp);
    }
    public boolean isTotal_event() {
        return reportingEntity.isTotal_event();
    }

    public void setTotal_event(boolean total_event) {
        reportingEntity.setTotal_event(total_event);
    }
    public boolean isTotal_count() {
        return reportingEntity.isTotal_count();
    }

    public void setTotal_count(boolean total_count) {
        reportingEntity.setTotal_count(total_count);
    }
    public boolean isTotal_floor() {
        return reportingEntity.isTotal_floor();
    }

    public void setTotal_floor(boolean total_floor) {
        reportingEntity.setTotal_floor(total_floor);
    }

    public boolean isTotal_impression() {
        return reportingEntity.isTotal_impression();
    }

    public void setTotal_impression(boolean total_impression) {
        reportingEntity.setTotal_impression(total_impression);
    }

    public boolean isTotal_bidValue() {
        return reportingEntity.isTotal_bidValue();
    }

    public void setTotal_bidValue(boolean total_bidValue) {
        reportingEntity.setTotal_bidValue(total_bidValue);
    }

    public boolean isTotal_click() {
        return reportingEntity.isTotal_click();
    }

    public void setTotal_click(boolean total_click) {
        reportingEntity.setTotal_click(total_click);
    }
    
    public boolean isBilledclicks() {
        return reportingEntity.isBilledclicks();
    }

    public void setBilledclicks(boolean billedclicks) {
        reportingEntity.setBilledclicks(billedclicks);
    }

    public boolean isTotal_win() {
        return reportingEntity.isTotal_win();
    }

    public void setTotal_win(boolean total_win) {
        reportingEntity.setTotal_win(total_win);
    }

    public boolean isTotal_win_bidValue() {
        return reportingEntity.isTotal_win_bidValue();
    }

    public void setTotal_win_bidValue(boolean total_win_bidValue) {
        reportingEntity.setTotal_win_bidValue(total_win_bidValue);
    }

    public boolean isTotal_csc() {
        return reportingEntity.isTotal_csc();
    }

    public void setTotal_csc(boolean total_csc) {
        reportingEntity.setTotal_csc(total_csc);
    }

    public boolean isTotal_event_type() {
        return reportingEntity.isTotal_event_type();
    }

    public void setTotal_event_type(boolean total_event_type) {
        reportingEntity.setTotal_event_type(total_event_type);
    }

    public boolean isDemandCharges() {
        return reportingEntity.isDemandCharges();
    }

    public void setDemandCharges(boolean demandCharges) {
        reportingEntity.setDemandCharges(demandCharges);
    }

    public boolean isSupplyCost() {
        return reportingEntity.isSupplyCost();
    }

    public void setSupplyCost(boolean supplyCost) {
        reportingEntity.setSupplyCost(supplyCost);
    }

    public boolean isEarning() {
        return reportingEntity.isEarning();
    }

    public void setEarning(boolean earning) {
        reportingEntity.setEarning(earning);
    }

    public boolean isCtr() {
        return reportingEntity.isCtr();
    }

    public void setCtr(boolean ctr) {
        reportingEntity.setCtr(ctr);
    }

    public boolean isEcpm() {
        return reportingEntity.isEcpm();
    }

    public void setEcpm(boolean ecpm) {
        reportingEntity.setEcpm(ecpm);
    }

    public boolean iseIPM() {
        return reportingEntity.iseIPM();
    }

    public void seteIPM(boolean eIPM) {
        reportingEntity.seteIPM(eIPM);
    }

    public boolean isFr() {
        return reportingEntity.isFr();
    }

    public void setFr(boolean fr) {
        reportingEntity.setFr(fr);
    }

    public boolean isAvgBidFloor() {
		return reportingEntity.isAvgBidFloor();
	}

	public void setAvgBidFloor(boolean avgBidFloor) {
		reportingEntity.setAvgBidFloor(avgBidFloor);
	}

    public boolean iseCPC() {
        return reportingEntity.iseCPC();
    }

    public void seteCPC(boolean eCPC) {
        reportingEntity.seteCPC(eCPC);
    }
    public boolean isBilledECPC() {
        return reportingEntity.isBilledECPC();
    }

    public void setBilledECPC(boolean billedECPC) {
        reportingEntity.setBilledECPC(billedECPC);
    }
    public void setBilledcsc(boolean billedcsc) {
        reportingEntity.setBilledcsc(billedcsc);
    }
    public boolean isBilledECPM() {
        return reportingEntity.isBilledECPM();
    }
    public void setBilledECPM(boolean billedECPM) {
        reportingEntity.setBilledECPM(billedECPM);
    }
    public boolean isBilledEIPM() {
        return reportingEntity.isBilledEIPM();
    }
    public void setBilledEIPM(boolean billedEIPM) {
        reportingEntity.setBilledEIPM(billedEIPM);
    }

    public boolean iseIPC() {
        return reportingEntity.iseIPC();
    }

    public void seteIPC(boolean eIPC) {
        reportingEntity.seteIPC(eIPC);
    }

    public boolean isConversion() {
        return reportingEntity.isConversion();
    }

    public void setConversion(boolean conversion) {
        reportingEntity.setConversion(conversion);
    }

    public boolean isClicksr() {
        return reportingEntity.isClicksr();
    }

    public void setClicksr(boolean clicksr) {
        reportingEntity.setClicksr(clicksr);
    }

    public boolean isRtr() {
        return reportingEntity.isRtr();
    }

    public void setRtr(boolean rtr) {
        reportingEntity.setRtr(rtr);
    }

    public boolean isWtr() {
        return reportingEntity.isWtr();
    }

    public void setWtr(boolean wtr) {
        reportingEntity.setWtr(wtr);
    }

    public boolean isProfitmargin() {
        return reportingEntity.isProfitmargin();
    }

    public void setProfitmargin(boolean profitmargin) {
        reportingEntity.setProfitmargin(profitmargin);
    }

    public boolean iseIPW() {
        return reportingEntity.iseIPW();
    }

    public void seteIPW(boolean eIPW) {
        reportingEntity.seteIPW(eIPW);
    }

    public boolean iseCPW() {
        return reportingEntity.iseCPW();
    }

    public void seteCPW(boolean eCPW) {
        reportingEntity.seteCPW(eCPW);
    }
    
    public boolean iseIPA() {
        return reportingEntity.iseIPA();
    }
    public void seteIPA(boolean eIPA) {
        reportingEntity.seteIPA(eIPA);
    }
    public boolean iseCPA() {
        return reportingEntity.iseCPA();
    }
    public void seteCPA(boolean eCPA) {
        reportingEntity.seteCPA(eCPA);
    }
    public boolean isCpa_goal() {
        return reportingEntity.isCpa_goal();
    }
    public void setCpa_goal(boolean cpa_goal) {
        reportingEntity.setCpa_goal(cpa_goal);
    }

    public boolean isExchangepayout() {
        return reportingEntity.isExchangepayout();
    }
    public void setExchangepayout(boolean exchangepayout) {
        reportingEntity.setExchangepayout(exchangepayout);
    }
    public boolean isExchangerevenue() {
        return reportingEntity.isExchangerevenue();
    }
    public void setExchangerevenue(boolean exchangerevenue) {
        reportingEntity.setExchangerevenue(exchangerevenue);
    }
    public boolean isNetworkpayout() {
        return reportingEntity.isNetworkpayout();
    }
    public void setNetworkpayout(boolean networkpayout) {
        reportingEntity.setNetworkpayout(networkpayout);
    }
    public boolean isNetworkrevenue() {
        return reportingEntity.isNetworkrevenue();
    }
    public void setNetworkrevenue(boolean networkrevenue) {
        reportingEntity.setNetworkrevenue(networkrevenue);
    }
    private ObjectMapper objectMapper = new ObjectMapper();

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
