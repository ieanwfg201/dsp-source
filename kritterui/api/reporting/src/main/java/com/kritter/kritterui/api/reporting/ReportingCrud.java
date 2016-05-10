package com.kritter.kritterui.api.reporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.constants.ChartType;
import com.kritter.constants.Naming;
import com.kritter.kritterui.api.utils.TimeManipulation.TimeManipulator;
import com.kritter.kumbaya.libraries.data_structs.common.KumbayaReportingConfiguration;
import com.kritter.kumbaya.libraries.data_structs.query_planner.ColumnType;
import com.kritter.kumbaya.libraries.data_structs.query_planner.Header;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KumbayaQueryPlanner;
import com.kritter.kumbaya.libraries.query_planner.db.DBQueryPlanner;

public class ReportingCrud {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingCrud.class);
    
    public static ArrayNode createColumnNode(ObjectMapper mapper, List<Header> headerList){
        ArrayNode columnNode = mapper.createArrayNode();
        for(Header header: headerList){
            ObjectNode columnObjNode = mapper.createObjectNode();
            columnObjNode.put("title", header.ui_name);
            columnObjNode.put("field", header.name);
            columnObjNode.put("visible", header.isVisible());
            if(header.isClickable()){
                columnObjNode.put("clickable", true);
            }
            columnNode.add(columnObjNode);
        }
        return columnNode;
    }
    public static String createCsvHeader(List<Header> headerList, String csvDelimiter, ArrayList<SumEntity> csvTotalSum, boolean docsvTotalSsm){
        StringBuffer sbuff = new StringBuffer();
        for(Header header: headerList){
            sbuff.append(csvDelimiter);
            sbuff.append(header.ui_name);
            if(docsvTotalSsm){
                if(header.getColumnType() == ColumnType.METRIC){
                    switch(header.headerType){
                    case INT: csvTotalSum.add(new SumEntity(0)); break;
                    case LONG: csvTotalSum.add(new SumEntity(1)); break;
                    case DOUBLE: csvTotalSum.add(new SumEntity(2)); break;
                    case STRING: csvTotalSum.add(null);
                    default: break;
                    }
                }else{
                    csvTotalSum.add(null);
                }
            }
        }
        return sbuff.toString();
    }

    public static JsonNode get_data(Connection con,ReportingEntity reportingEntity, 
            boolean returnWithId, boolean exportAsCSV, String absoluteFileName){
        if(con == null || reportingEntity == null){
            return null;
        }
        PreparedStatement pstmt = null;
        PreparedStatement foundRowPstmt = null;
        try{

            DBQueryPlanner dbQueryPlanner = new DBQueryPlanner();
            KumbayaQueryPlanner kQueryPlanner = new KumbayaQueryPlanner();
            KumbayaReportingConfiguration kReportingConfiguration = new KumbayaReportingConfiguration();
            dbQueryPlanner.convert(kQueryPlanner, kReportingConfiguration, reportingEntity, returnWithId);
            dbQueryPlanner.plan(kQueryPlanner, kReportingConfiguration, reportingEntity);
            //System.out.println(kQueryPlanner.getQueryString());
            LOG.debug(kQueryPlanner.getQueryString());
            String queryString = kQueryPlanner.getQueryString();
            if(queryString == null){
                return null;
            }
            pstmt = con.prepareStatement(queryString);
            ResultSet rset = pstmt.executeQuery();
            ObjectMapper mapper = new ObjectMapper();
            switch(reportingEntity.getChartType()){
            case PIE:
                if(exportAsCSV){
                    return null;
                }
                ArrayNode arrayNode = mapper.createArrayNode();
                int sum = 0;
                while(rset.next()){
                    ObjectNode dataObjNode = mapper.createObjectNode();
                    for(Header header: kQueryPlanner.getHeaderList()){
                        switch(header.headerType){
                        case INT: 
                            int percent = (int)(rset.getDouble(header.getName())*100);
                            sum = sum + percent;
                            dataObjNode.put("value",percent);break;
                        case DOUBLE: 
                            percent = (int)(rset.getDouble(header.getName())*100);
                            sum = sum + percent;
                            dataObjNode.put("value",percent);break;
                        case LONG: 
                            percent = (int)(rset.getDouble(header.getName())*100);
                            sum = sum + percent;
                            dataObjNode.put("value",percent);break;
                        case STRING: dataObjNode.put("label",rset.getString(header.getName()));break;
                        default: break;
                        }
                    }
                    arrayNode.add(dataObjNode);
                }
                ObjectNode otherNode = mapper.createObjectNode();
                otherNode.put("label",Naming.report_dashboard_others);
                otherNode.put("value",100-sum);
                arrayNode.add(otherNode);
                return arrayNode;
                /*case BAR:
                ObjectNode barRootNode = mapper.createObjectNode();
                int size = kQueryPlanner.getHeaderList().size();
                LinkedHashMap<String, ArrayNode> hashMap = new LinkedHashMap<String, ArrayNode>();
                ArrayNode headerArrayNode = mapper.createArrayNode();
                String xaxis = null;
                int i = 1;
                for(Header header: kQueryPlanner.getHeaderList()){
                    if(header.headerType==HeaderType.STRING){
                        xaxis  = header.getName();
                    }else{
                        ObjectNode headerObjectNode = mapper.createObjectNode();
                        headerObjectNode.put("label", header.getUi_name());
                        headerObjectNode.put("data", "barChart.data"+i);
                        headerArrayNode.add(headerObjectNode);
                        hashMap.put(header.getName(), mapper.createArrayNode());
                        i++;
                    }
                }
                barRootNode.put("barChart.data", headerArrayNode);
                String str = null;
                while(rset.next()){
                    str = rset.getString(xaxis);
                    for(Header header: kQueryPlanner.getHeaderList()){
                        ArrayNode metricArrayNode = mapper.createArrayNode();
                        switch(header.headerType){
                        case INT: 
                            metricArrayNode.add(str);
                            metricArrayNode.add(rset.getInt(header.getName()));
                            hashMap.get(header.getName()).add(metricArrayNode);break;
                        case DOUBLE: 
                            metricArrayNode.add(str);
                            metricArrayNode.add(rset.getDouble(header.getName()));
                            hashMap.get(header.getName()).add(metricArrayNode);break;
                        case LONG: 
                            metricArrayNode.add(str);
                            metricArrayNode.add(rset.getLong(header.getName()));
                            hashMap.get(header.getName()).add(metricArrayNode);break;
                        default: break;
                        }
                    }
                    i =1;
                    for(Header header: kQueryPlanner.getHeaderList()){
                        if(HeaderType.STRING != header.headerType){
                            barRootNode.put("barChart.data"+i, hashMap.get(header.getName()));
                            i++;
                        }
                    }
                }
                return barRootNode;*/
            default:
                String stringformat = null;
                if(reportingEntity.isRoundoffmetric()){
                    stringformat = "%."+reportingEntity.getRoundoffmetriclength()+"f";
                }
                ObjectNode rootNode = mapper.createObjectNode();
                ArrayNode dataNode = null;
                BufferedWriter bw = null;
                FileWriter fw =  null;
                try{
                    ArrayList<SumEntity> csvTotalSum = null;
                    if(exportAsCSV){
                        File f = new File(absoluteFileName);
                        f.getParentFile().mkdirs();
                        fw = new FileWriter(f);
                        bw = new BufferedWriter(fw);
                        csvTotalSum = new ArrayList<SumEntity>();
                        bw.write(createCsvHeader(kQueryPlanner.getHeaderList(), reportingEntity.getCsvDelimiter(), csvTotalSum, reportingEntity.isRollup()));
                        bw.write("\n");
                    }else{
                        dataNode = mapper.createArrayNode();
                        rootNode.put("column", createColumnNode(mapper, kQueryPlanner.getHeaderList()));
                    }
                    while(rset.next()){
                        ObjectNode dataObjNode = null;
                        StringBuffer sbuff = null;
                        if(exportAsCSV){
                            sbuff = new StringBuffer();
                        }else{
                            dataObjNode = mapper.createObjectNode();
                        }
                        int count = 0;
                        for(Header header: kQueryPlanner.getHeaderList()){
                            switch(header.headerType){
                            case INT:
                                int i = rset.getInt(header.getName());
                                if(exportAsCSV){
                                    sbuff.append(reportingEntity.getCsvDelimiter());
                                    sbuff.append(i);
                                    if(reportingEntity.isRollup() && csvTotalSum.get(count) != null){
                                        csvTotalSum.get(count).setSumInt(csvTotalSum.get(count).getSumInt()+i);
                                    }
                                }else{
                                    dataObjNode.put(header.name,i);
                                }
                                break;
                            case DOUBLE:
                                double d  =  rset.getDouble(header.getName());
                                if(exportAsCSV){
                                    sbuff.append(reportingEntity.getCsvDelimiter());
                                    if(stringformat != null){
                                        sbuff.append(String.format(stringformat, d));
                                    }else{
                                        sbuff.append(d);
                                    }
                                    if(reportingEntity.isRollup() && csvTotalSum.get(count) != null){
                                        csvTotalSum.get(count).setSumDouble(csvTotalSum.get(count).getSumDouble()+d);
                                    }
                                }else{
                                    if(stringformat != null){
                                        dataObjNode.put(header.name,String.format(stringformat, d)); 
                                    }else{
                                        dataObjNode.put(header.name,d);
                                    }
                                }
                                break;
                            case LONG:
                                long l = rset.getLong(header.getName());
                                if(exportAsCSV){
                                    sbuff.append(reportingEntity.getCsvDelimiter());
                                    sbuff.append(l);
                                    if(reportingEntity.isRollup() && csvTotalSum.get(count) != null){
                                        csvTotalSum.get(count).setSumLong(csvTotalSum.get(count).getSumLong()+l);
                                    }
                                }else{
                                    dataObjNode.put(header.name,l);
                                }
                                break;
                            case STRING:
                                String s = rset.getString(header.getName());
                                if(exportAsCSV){
                                    sbuff.append(reportingEntity.getCsvDelimiter());
                                    if("time".equals(header.getName())){
                                        if(s != null){
                                            sbuff.append(s.split("[.]")[0]);
                                        }else{
                                            sbuff.append(s);
                                        }
                                    }else{
                                        sbuff.append(s);
                                    }
                                }else{
                                    dataObjNode.put(header.name,s);
                                }
                                break;
                            default: break;
                            }
                            count++;
                        }
                        if(exportAsCSV){
                            bw.write(sbuff.toString());
                            bw.write("\n");
                        }else{
                            dataNode.add(dataObjNode);
                        }
                    }
                    if(exportAsCSV && reportingEntity.isRollup()){
                        bw.write("TOTAL");
                        for(SumEntity sumentity:csvTotalSum){
                            bw.write(reportingEntity.getCsvDelimiter());
                            if(sumentity != null){
                                if(sumentity.getType() == 0 ){
                                    bw.write(sumentity.getSumInt()+"");
                                }else if(sumentity.getType() == 1 ){
                                    bw.write(sumentity.getSumLong()+"");
                                }else if(sumentity.getType() == 2 ){
                                    bw.write(sumentity.getSumDouble()+"");
                                }
                            }
                        }
                        bw.write("\n");
                    }
                    if(!exportAsCSV){
                        rootNode.put("data", dataNode);
                    }
                    foundRowPstmt = con.prepareStatement("SELECT FOUND_ROWS() as count");
                    ResultSet foundRowRset = foundRowPstmt.executeQuery();
                    int count = 0;
                    if(foundRowRset.next()){
                        count = foundRowRset.getInt("count");
                    }
                    if(!exportAsCSV){
                        rootNode.put("count", count);
                    }
                    return rootNode;
                }catch(Exception e){
                    LOG.error(e.getMessage(),e);
                    return null;
                }finally{
                    if(bw != null){
                        bw.close();
                    }
                    if(fw != null){
                        fw.close();
                    }
                }
            }
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(foundRowPstmt != null){
                try {
                    foundRowPstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }

    
    
    public static JsonNode get_top_n(Connection con, ReportingEntity reportingEntity){
        if(con == null || reportingEntity == null){
            return null;
        }
        Date enddate = new Date();
        Date startdate = new Date();
        if(reportingEntity.getTop_n_for_last_x_hours() > 0){
            startdate = DateUtils.addHours(startdate, 0-reportingEntity.getTop_n_for_last_x_hours());
        }else{
            startdate = DateUtils.addHours(startdate, reportingEntity.getTop_n_for_last_x_hours());
        }
        reportingEntity.setStart_time_str(TimeManipulator.convertDate(startdate, reportingEntity.getDate_format(), reportingEntity.getTimezone()));
        reportingEntity.setDate_as_dimension(false);
        reportingEntity.setEnd_time_str(TimeManipulator.convertDate(enddate, reportingEntity.getDate_format(), reportingEntity.getTimezone()));
        reportingEntity.setStartindex(0);
        reportingEntity.setPagesize(reportingEntity.getTop_n());
        return get_data(con, reportingEntity, false, false, null);
    }
    public static JsonNode get_pie(Connection con, ReportingEntity reportingEntity){
        if(con == null || reportingEntity == null){
            return null;
        }
        Date enddate = new Date();
        Date startdate = new Date();
        if(reportingEntity.getTop_n_for_last_x_hours() > 0){
            startdate = DateUtils.addHours(startdate, 0-reportingEntity.getTop_n_for_last_x_hours());
        }else{
            startdate = DateUtils.addHours(startdate, reportingEntity.getTop_n_for_last_x_hours());
        }
        reportingEntity.setStart_time_str(TimeManipulator.convertDate(startdate, reportingEntity.getDate_format(), reportingEntity.getTimezone()));
        reportingEntity.setDate_as_dimension(false);
        reportingEntity.setEnd_time_str(TimeManipulator.convertDate(enddate, reportingEntity.getDate_format(), reportingEntity.getTimezone()));
        reportingEntity.setChartType(ChartType.PIE);
        reportingEntity.setStartindex(0);
        return get_data(con, reportingEntity, false, false, null);
    }
    public static JsonNode get_bar(Connection con, ReportingEntity reportingEntity){
        if(con == null || reportingEntity == null){
            return null;
        }
        Date enddate = new Date();
        Date startdate = new Date();
        if(reportingEntity.getTop_n_for_last_x_hours() > 0){
            if(reportingEntity.getPagesize() >= reportingEntity.getTop_n_for_last_x_hours()){
                startdate = DateUtils.addHours(startdate, 0-reportingEntity.getTop_n_for_last_x_hours());
            }else{
                startdate = DateUtils.addHours(startdate, 0-reportingEntity.getPagesize());
            }
        }else{
            if(reportingEntity.getPagesize() >= Math.abs(reportingEntity.getTop_n_for_last_x_hours())){
                startdate = DateUtils.addHours(startdate, reportingEntity.getTop_n_for_last_x_hours());
            }else{
                startdate = DateUtils.addHours(startdate, 0-reportingEntity.getPagesize());
            }
        }
        reportingEntity.setStart_time_str(TimeManipulator.convertDate(startdate, reportingEntity.getDate_format(), reportingEntity.getTimezone()));
        reportingEntity.setDate_as_dimension(true);
        reportingEntity.setEnd_time_str(TimeManipulator.convertDate(enddate, reportingEntity.getDate_format(), reportingEntity.getTimezone()));
        reportingEntity.setChartType(ChartType.BAR);
        reportingEntity.setStartindex(0);
        return get_data(con, reportingEntity, false, false, null);
    }
    /*public static void main(String args[]){
        ReportingEntity re = new ReportingEntity();
        re.setTotal_request(true);
        re.setTotal_impression(true);
        re.setTop_n_for_last_x_hours(-756);
        re.setFrequency(Frequency.HOURLY);
        re.setChartType(ChartType.BAR);
        Connection con = null ;
        try{
            con = DBConnector.getConnection("MYSQL","localhost", "3306", "kritter", "root", "password");
            System.out.println(ReportingCrud.get_bar(con, re).toString());
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(con!= null){
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }*/
}
