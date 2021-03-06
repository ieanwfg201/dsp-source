package models.entities.fraudreport;

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
import com.kritter.api.entity.fraud.FraudReportEntity;
import com.kritter.kritterui.api.utils.TimeManipulation.TimeManipulator;

public class FraudReportBaseEntity {
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

    protected FraudReportEntity fraudReportEntity = new FraudReportEntity();

    public FraudReportBaseEntity(FraudReportEntity fraudReportEntity) {
        this.fraudReportEntity = fraudReportEntity;
        if (fraudReportEntity == null
                || fraudReportEntity.getStart_time_str() == null
                || "".equals(fraudReportEntity.getStart_time_str())) {
            Date date = DateUtils.addDays(new Date(), -1);
            start_date = TimeManipulator.convertDate(date,
                    "yyyy-MM-dd 00:00:00", timezoneid);
        } else {
            this.start_date = fraudReportEntity.getStart_time_str();
        }
        if (fraudReportEntity == null
                || fraudReportEntity.getEnd_time_str() == null
                || "".equals(fraudReportEntity.getEnd_time_str())) {
            Date date = DateUtils.addDays(new Date(), -1);
            end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                    timezoneid);
        } else {
            this.end_date = fraudReportEntity.getEnd_time_str();
        }

    }

    public FraudReportBaseEntity() {
        this.fraudReportEntity = new FraudReportEntity();
        Date date = DateUtils.addDays(new Date(), -1);
        start_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                timezoneid);
        end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 23:59:59",
                timezoneid);
    }

    public void setStart_date(String start_date) {
        fraudReportEntity.setStart_time_str(start_date);
    }

    public String getStart_date() {
        String str = fraudReportEntity.getStart_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", timezoneid);
        }
        return str;
    }

    public void setEnd_date(String end_date) {
        fraudReportEntity.setEnd_time_str(end_date);
    }

    public String getEnd_date() {
        String str = fraudReportEntity.getEnd_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", timezoneid);
        }
        return str;
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
