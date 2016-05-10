package com.kritter.kritterui.api.extsitereport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.extsitereport.ExtSiteReportEntity;


public class ExtSiteReportCrud {
    private static final Logger LOG = LoggerFactory.getLogger(ExtSiteReportCrud.class);

    public static JsonNode get_data(Connection con,ExtSiteReportEntity extsiteReportEntity, 
            boolean exportAsCSV, String absoluteFileName){
        if(con == null || extsiteReportEntity == null){
            return null;
        }
        PreparedStatement pstmt = null;
        BufferedWriter bw = null;
        FileWriter fw =  null;
        try{
            ExtSiteReportHelper leh = new ExtSiteReportHelper();
            String queryString = leh.createQueryString(extsiteReportEntity);
            if(queryString == null){
                return null;
            }
            pstmt = con.prepareStatement(queryString);
            ResultSet rset = pstmt.executeQuery();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode dataNode = null;
            if(exportAsCSV){
                File f = new File(absoluteFileName);
                f.getParentFile().mkdirs();
                fw = new FileWriter(f);
                bw = new BufferedWriter(fw);
                bw.write(leh.creatHeadereCsv(extsiteReportEntity.getCsvDelimiter()));
                bw.write("\n");
            }else{
                dataNode = mapper.createArrayNode();
                rootNode.put("column", leh.createColumnNode(mapper));
            }

            while(rset.next()){
                if(exportAsCSV){
                    bw.write("\n");
                    boolean isFirst = true;
                    for(String key : leh.projectionMap.keySet()){
                        if(isFirst){
                            isFirst=false;
                        }else{
                            bw.write(extsiteReportEntity.getCsvDelimiter());
                        }
                        bw.write(""+rset.getObject(leh.projectionMap.get(key)));
                    }
                }else{
                    ObjectNode dataObjNode = mapper.createObjectNode();
                    for(String key : leh.projectionMap.keySet()){
                        dataObjNode.put(leh.projectionMap.get(key),""+rset.getObject(leh.projectionMap.get(key)));
                    }
                    dataNode.add(dataObjNode);
                }
            }
            if(!exportAsCSV){
                rootNode.put("data", dataNode);
            }
            return rootNode;
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
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(),e);
                }
            }

        }
    }
}
