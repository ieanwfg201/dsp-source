package com.kritter.kritterui.api.algo_models;

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

import com.kritter.entity.algomodel.AlgoModelEntity;


public class AlgoModelCrud {
    private static final Logger LOG = LoggerFactory.getLogger(AlgoModelCrud.class);

    public static JsonNode get_data(Connection con,AlgoModelEntity algoModelEntity){
        if(con == null || algoModelEntity == null){
            return null;
        }
        PreparedStatement pstmt = null;
        try{
            AlgoModelHelper leh = new AlgoModelHelper();
            String queryString = leh.createQueryString(algoModelEntity);
            if(queryString == null){
                return null;
            }
            pstmt = con.prepareStatement(queryString);
            ResultSet rset = pstmt.executeQuery();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode dataNode = null;
            dataNode = mapper.createArrayNode();
            if(rset.next()){
                String data = rset.getString("data");
                leh.createData(data, algoModelEntity, dataNode, mapper);
            }
            rootNode.put("column", leh.createColumnNode(mapper));
            rootNode.put("data", dataNode);
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
        }
    }
}
