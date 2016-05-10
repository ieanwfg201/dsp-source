package com.kritter.kritterui.api.iddefinition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.api.entity.iddefinition.Iddefinition;
import com.kritter.api.entity.iddefinition.IddefinitionInput;
import com.kritter.api.entity.iddefinition.IddefinitionList;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.IddefinitionType;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class IddefinitionCrud {

    private static final Logger LOG = LoggerFactory.getLogger(IddefinitionCrud.class);
    
    public static void populate(Iddefinition iddefinition, ResultSet rset) throws SQLException {
        if(iddefinition != null && rset != null){
            iddefinition.setId(rset.getInt("id"));
            iddefinition.setGuid(rset.getString("guid"));
            iddefinition.setName(rset.getString("name"));
        }
    }
    public static void populateEmptyIddefinitionlist(IddefinitionList iddefinitionList){
        if(iddefinitionList != null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.EMPTY_RESULT_DUE_TO_INPUT.getId());
            msg.setMsg(ErrorEnum.EMPTY_RESULT_DUE_TO_INPUT.getName());
            iddefinitionList.setIddefinition_list(new LinkedList<Iddefinition>());
            iddefinitionList.setMsg(msg);
        }
    }
    public static JsonNode get_iddefinition_list(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            IddefinitionList iddefinitionList = new IddefinitionList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IDDEFINITION_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.IDDEFINITION_INPUT_NULL.getName());
            iddefinitionList.setMsg(msg);
            return iddefinitionList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            IddefinitionInput iddefinitionInput = objectMapper.treeToValue(jsonNode, IddefinitionInput.class);
            return get_iddefinition_list(con, iddefinitionInput).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            IddefinitionList iddefinitionList = new IddefinitionList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            iddefinitionList.setMsg(msg);
            return iddefinitionList.toJson();
        }
    }
    
    public static IddefinitionList get_iddefinition_list(Connection con,IddefinitionInput iddefinitionInput){

        if(con == null){
            IddefinitionList iddefinitionList = new IddefinitionList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            iddefinitionList.setMsg(msg);
            return iddefinitionList;
        }
        if(iddefinitionInput == null){
            IddefinitionList iddefinitionList = new IddefinitionList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IDDEFINITION_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.IDDEFINITION_INPUT_NULL.getName());
            iddefinitionList.setMsg(msg);
            return iddefinitionList;
        }
        PreparedStatement pstmt = null;
        IddefinitionList iddefinitionList = new IddefinitionList();
        try{
            if(iddefinitionInput.getIds() == null || "".equals(iddefinitionInput.getIds().trim())){
                populateEmptyIddefinitionlist(iddefinitionList); 
                return iddefinitionList;
            }
            switch(iddefinitionInput.getIddefinitionEnum()){
                case NONE:
                    populateEmptyIddefinitionlist(iddefinitionList); 
                    return iddefinitionList;
                case GET_PUB:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_PUB_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Iddefinition.GET_PUB_BY_GUID, "<id>", iddefinitionInput.getIds(), 
                                ",", true));
                    }
                    break;
                case GET_SITE:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_SITE_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Iddefinition.GET_SITE_BY_GUID, "<id>", iddefinitionInput.getIds(), 
                                ",", true));
                    }
                    break;
                case GET_EXT_SITE:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_EXT_SITE_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        populateEmptyIddefinitionlist(iddefinitionList);
                        return iddefinitionList;
                    }
                    break;
                case GET_ADV:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_ADV_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Iddefinition.GET_ADV_BY_GUID, "<id>", iddefinitionInput.getIds(), 
                                ",", true));
                    }
                    break;
                case GET_CAMPAIGN:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_CAMPAIGN_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Iddefinition.GET_CAMPAIGN_BY_GUID, "<id>", iddefinitionInput.getIds(), 
                                ",", true));
                    }
                    break;
                case GET_AD:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_AD_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Iddefinition.GET_AD_BY_GUID, "<id>", iddefinitionInput.getIds(), 
                                ",", true));
                    }
                    break;
                case GET_TP:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.GUID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_TP_BY_GUID, "<id>", iddefinitionInput.getIds(), 
                            ",", true));
                    }else{
                        populateEmptyIddefinitionlist(iddefinitionList);
                        return iddefinitionList;
                    }
                    break;
                case GET_CREATIVE:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_CREATIVE_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Iddefinition.GET_CREATIVE_BY_GUID, "<id>", iddefinitionInput.getIds(), 
                                ",", true));
                    }
                    break;
                case GET_BRAND:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_BRAND_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        populateEmptyIddefinitionlist(iddefinitionList);
                        return iddefinitionList;
                    }
                    break;
                case GET_OS:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_OS_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        populateEmptyIddefinitionlist(iddefinitionList);
                        return iddefinitionList;
                    }
                    break;
                case GET_BROWSER:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_BROWSER_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        populateEmptyIddefinitionlist(iddefinitionList);
                        return iddefinitionList;
                    }
                    break;
                case GET_MODEL:
                    if(iddefinitionInput.getIddefinitionType() == IddefinitionType.ID){
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Iddefinition.GET_MODEL_BY_ID, "<id>", iddefinitionInput.getIds(), 
                            ",", false));
                    }else{
                        populateEmptyIddefinitionlist(iddefinitionList);
                        return iddefinitionList;
                    }
                    break;
                default:
                    populateEmptyIddefinitionlist(iddefinitionList);
                    return iddefinitionList;
            }
            ResultSet rset = pstmt.executeQuery();
            List<Iddefinition> iddefinitions = new LinkedList<Iddefinition>(); 
            while(rset.next()){
                Iddefinition iddefinition = new Iddefinition();
                populate(iddefinition, rset);
                iddefinitions.add(iddefinition);
            }
            
            Message msg = new Message();
            if(iddefinitions.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.IDDEFINITION_EMPTY.getId());
                msg.setMsg(ErrorEnum.IDDEFINITION_EMPTY.getName());
            }
            iddefinitionList.setMsg(msg);
            iddefinitionList.setIddefinition_list(iddefinitions);
            return iddefinitionList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            iddefinitionList.setMsg(msg);
            return iddefinitionList;
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
