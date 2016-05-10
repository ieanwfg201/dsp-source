package models.entities.tracking_event;

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
import com.kritter.api.entity.tracking_event.TrackingEvent;
import com.kritter.kritterui.api.utils.TimeManipulation.TimeManipulator;

public class TrackingEventBaseEntity {

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

    protected TrackingEvent trackingEvent = new TrackingEvent();

    public TrackingEventBaseEntity(TrackingEvent trackingEvent) {
        this.trackingEvent = trackingEvent;
        if (trackingEvent == null
                || trackingEvent.getStart_time_str() == null
                || "".equals(trackingEvent.getStart_time_str())) {
            Date date = DateUtils.addDays(new Date(), -1);
            start_date = TimeManipulator.convertDate(date,
                    "yyyy-MM-dd 00:00:00", "UTC");
        } else {
            this.start_date = trackingEvent.getStart_time_str();
        }
        if (trackingEvent == null
                || trackingEvent.getEnd_time_str() == null
                || "".equals(trackingEvent.getEnd_time_str())) {
            Date date = DateUtils.addDays(new Date(), -1);
            end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                    "UTC");
        } else {
            this.end_date = trackingEvent.getEnd_time_str();
        }

    }

    public TrackingEventBaseEntity() {
        this.trackingEvent = new TrackingEvent();
        Date date = DateUtils.addDays(new Date(), -1);
        start_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 00:00:00",
                "UTC");
        end_date = TimeManipulator.convertDate(date, "yyyy-MM-dd 23:59:59",
                "UTC");
    }

    public void setStart_date(String start_date) {
        trackingEvent.setStart_time_str(start_date);
    }

    public String getStart_date() {
        String str = trackingEvent.getStart_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", "UTC");
        }
        return str;
    }

    public void setEnd_date(String end_date) {
        trackingEvent.setEnd_time_str(end_date);
    }

    public String getEnd_date() {
        String str = trackingEvent.getEnd_time_str();
        if (str == null || "".equals(str)) {
            return TimeManipulator.convertDate(DateUtils.addDays(new Date(), -1),
                    "yyyy-MM-dd 00:00:00", "UTC");
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
