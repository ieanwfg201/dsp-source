package com.kritter.kritterui.api.metadata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.kritter.api.entity.metadata.MetaField;
import com.kritter.api.entity.metadata.MetaInput;
import com.kritter.api.entity.metadata.MetaList;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.MetadataType;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.db_query_def.Metadata;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MetadataCrud {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataCrud.class);


    public static void populate_with_parentName(MetaField metaField, String jsonData) throws SQLException {
        metaField.setName(jsonData);
    }

    public static void audienceType(MetaField metaField, ResultSet rset, List<AudienceSource> sourceList) throws SQLException {
        while (rset.next()) {
            // 判断size==0，==0，new AudienceSource，！=0 通过sourceid获取AudienceSource对象，再封装其内部的数据
            if (sourceList.size() == 0) {

                buildAudienceSource(rset, sourceList);

            } else {

                boolean isExits = false;
                for (AudienceSource audienceSource : sourceList) {
                    if (rset.getString("sCode").equalsIgnoreCase(audienceSource.getSourceId())) {
                        AudienceData parentAudienceData = audienceSource.getParentMap().get(rset.getString("pName"));
                        if (parentAudienceData == null) {
                            audienceSource.getParentMap().put(rset.getString("pName"), buildParentData(rset));
                        } else {
                            Map<String, AudienceData> childrenMap = (Map<String, AudienceData>)parentAudienceData.getAdditionalParameters().get("children");
                            childrenMap.put(rset.getString("cName"), buildChildrenData(rset));
                        }
                        isExits = true;
                    }
                }
                if (isExits == false) {
                    buildAudienceSource(rset, sourceList);
                }

            }
        }
        String jsonText = JSON.toJSONString(sourceList);
        populate_with_parentName(metaField, jsonText);
    }


    private static void buildAudienceSource(ResultSet rset, List<AudienceSource> sourceList) throws SQLException {
        Map<String, AudienceData> parentMap = buildParentMap(rset);
        AudienceSource source = new AudienceSource();
        source.setSourceName(rset.getString("sName"));
        source.setSourceId(rset.getString("sCode"));
        source.setParentMap(parentMap);
        sourceList.add(source);
    }

    private static Map<String, AudienceData> buildParentMap(ResultSet rset) throws SQLException {
        AudienceData parentAudienceData = buildParentData(rset);
        Map<String, AudienceData> parentMap = new HashMap<String, AudienceData>();
        parentMap.put(rset.getString("pName"), parentAudienceData);
        return parentMap;
    }

    private static AudienceData buildParentData(ResultSet rset) throws SQLException {
        Map<String, AudienceData> childrenMap = new HashMap<String, AudienceData>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("item-selected",false);
        AudienceData cAudienceData = new AudienceData(rset.getString("cName"), "item", rset.getString("pCode"), rset.getString("cCode"), map);
        childrenMap.put(rset.getString("cName"), cAudienceData);
        Map<String, Object> twoMap = new HashMap<String, Object>();
        twoMap.put("children", childrenMap);
        return new AudienceData(rset.getString("pName"), "folder", "", rset.getString("pCode"), twoMap);
    }

    private static AudienceData buildChildrenData(ResultSet rset) throws SQLException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("item-selected",false);
        AudienceData cAudienceData = new AudienceData(rset.getString("cName"), "item", rset.getString("pCode"), rset.getString("cCode"), map);
        return cAudienceData;
    }


    public static void populate(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setId(rset.getInt("id"));
            metaField.setName(rset.getString("name"));
        }
    }

    public static void populate_with_id_name(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setId(rset.getInt("id"));
            metaField.setName(rset.getString("name"));
        }
    }
    public static void populate_with_id_name_audience(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setId(rset.getInt("id"));
            metaField.setName(rset.getString("name"));
            metaField.setDescription(rset.getString("id"));
        }
    }


    public static void populate_with_description(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setId(rset.getInt("id"));
            metaField.setName(rset.getString("name"));
            metaField.setDescription(rset.getString("description"));
        }
    }

    public static void populate_with_version(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setId(rset.getInt("id"));
            metaField.setName(rset.getString("name"));
            metaField.setDescription(rset.getString("version"));
        }
    }

    public static void populate_only_name(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setName(rset.getString("name"));
        }
    }

    public static void populateWithEntityId(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setId(rset.getInt("id"));
            metaField.setName(rset.getString("name"));
            metaField.setEntity_id_set(rset.getString("entity_id_set"));
        }
    }

    public static void populateCountry(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setId(rset.getInt("id"));
            metaField.setName(rset.getString("name"));
            metaField.setCountry_code(rset.getString("country_code"));
        }
    }

    public static void populateisp(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setName(rset.getString("name"));
        }
    }

    public static void populateispdatafetchforinsertion(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setCountry_name(rset.getString("country_name"));
            metaField.setIsp_name(rset.getString("isp_name"));
            metaField.setData_source_name(rset.getString("data_source_name"));
        }
    }

    public static void populateispmappingsdataforedit(MetaField metaField, ResultSet rset) throws SQLException {
        if (metaField != null && rset != null) {
            metaField.setCountry_name(rset.getString("country_name"));
            metaField.setIsp_name(rset.getString("isp_name"));
            metaField.setIsp_ui_name(rset.getString("isp_ui_name"));
        }
    }

    public static void populateEmptyMetalist(MetaList metaList) {
        if (metaList != null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.EMPTY_RESULT_DUE_TO_INPUT.getId());
            msg.setMsg(ErrorEnum.EMPTY_RESULT_DUE_TO_INPUT.getName());
            metaList.setMetaFieldList(new LinkedList<MetaField>());
            metaList.setMsg(msg);
        }
    }

    public static JsonNode get_status(Connection con) {
        return get_metalist(con, MetadataType.STATUS, null).toJson();
    }

    public static JsonNode get_status_by_id(Connection con) {
        return get_metalist(con, MetadataType.STATUS_BY_ID, null).toJson();
    }

    public static JsonNode get_tier_1_category(Connection con) {
        return get_metalist(con, MetadataType.CATEGORY_TIER_1, null).toJson();
    }

    public static JsonNode get_tier_2_category(Connection con) {
        return get_metalist(con, MetadataType.CATEGORY_TIER_2, null).toJson();
    }

    public static JsonNode get_category_by_id(Connection con) {
        return get_metalist(con, MetadataType.CATEGORY_BY_ID, null).toJson();
    }

    public static Set<String> get_iab_categories_by_id(Connection con, Integer[] ids) {
        MetaInput metaInput = new MetaInput();
        StringBuffer sb = new StringBuffer();
        for (Integer id : ids) {
            sb.append(id);
            sb.append(",");
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        metaInput.setQuery_id_list(sb.toString());

        MetaList metaList = get_metalist(con, MetadataType.CATEGORY_BY_ID, metaInput);
        Set<String> idSet = new HashSet<String>();

        if (null != metaList) {
            List<MetaField> fields = metaList.getMetaFieldList();

            for (MetaField metaField : fields) {
                idSet.add(metaField.getName());
            }

            return idSet;
        }

        return idSet;
    }

    public static JsonNode get_category_by_id(Connection con, Integer[] ids) {
        MetaInput metaInput = new MetaInput();
        StringBuffer sb = new StringBuffer();
        for (Integer id : ids) {
            sb.append(id);
            sb.append(",");
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        metaInput.setQuery_id_list(sb.toString());

        return get_metalist(con, MetadataType.CATEGORY_BY_ID, metaInput).toJson();
    }

    public static JsonNode get_app_store_id(Connection con) {
        return get_metalist(con, MetadataType.APP_STORE_ID, null).toJson();
    }

    public static JsonNode get_app_store_id_by_id(Connection con) {
        return get_metalist(con, MetadataType.APP_STORE_ID_BY_ID, null).toJson();
    }

    public static JsonNode get_country(Connection con) {
        return get_metalist(con, MetadataType.COUNTRY, null).toJson();
    }

    public static JsonNode get_country_by_id(Connection con) {
        return get_metalist(con, MetadataType.COUNTRY_BY_ID, null).toJson();
    }

    public static JsonNode get_all_isp_by_id(Connection con) {
        return get_metalist(con, MetadataType.ALLISP_BY_ID, null).toJson();
    }

    public static JsonNode get_isp_by_country(Connection con, JsonNode jsonNode) {
        if (con == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg.toJson();
        }
        if (jsonNode == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.META_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.META_INPUT_NULL.getName());
            return msg.toJson();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MetaInput metaInput = objectMapper.treeToValue(jsonNode, MetaInput.class);
            return get_metalist(con, MetadataType.ISP_BY_COUNTRY, metaInput).toJson();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static JsonNode get_creative_slots(Connection con) {
        return get_metalist(con, MetadataType.CREATIVE_SLOTS, null).toJson();
    }

    public static JsonNode get_creative_slots_by_id(Connection con) {
        return get_metalist(con, MetadataType.CREATIVE_SLOTS_BY_ID, null).toJson();
    }

    public static JsonNode get_creative_attributes(Connection con) {
        return get_metalist(con, MetadataType.CREATIVE_ATTRIBUTES, null).toJson();
    }

    public static JsonNode get_creative_attributes_by_id(Connection con) {
        return get_metalist(con, MetadataType.CREATIVE_ATTRIBUTES_BY_ID, null).toJson();
    }

    public static JsonNode get_creative_attributes_by_format_id(Connection con) {
        return get_metalist(con, MetadataType.CREATIVE_ATTRIBUTES_BY_FORMAT_ID, null).toJson();
    }

    public static JsonNode get_creative_formats(Connection con) {
        return get_metalist(con, MetadataType.CREATIVE_FORMATS, null).toJson();
    }

    public static JsonNode get_creative_formats_by_id(Connection con) {
        return get_metalist(con, MetadataType.CREATIVE_FORMATS_BY_ID, null).toJson();
    }

    public static JsonNode get_handset_manufacturer(Connection con) {
        return get_metalist(con, MetadataType.HANDSET_MANUFACTURER, null).toJson();
    }

    public static JsonNode get_handset_manufacturer_by_id(Connection con) {
        return get_metalist(con, MetadataType.HANDSET_MANUFACTURER_BY_ID, null).toJson();
    }

    public static JsonNode get_handset_manufacturer_by_os(Connection con, JsonNode jsonNode) {
        if (con == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg.toJson();
        }
        if (jsonNode == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.META_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.META_INPUT_NULL.getName());
            return msg.toJson();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MetaInput metaInput = objectMapper.treeToValue(jsonNode, MetaInput.class);
            return get_metalist(con, MetadataType.HANDSET_MANUFACTURER_BY_OS, metaInput).toJson();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static JsonNode get_handset_model_by_manufacturer(Connection con, JsonNode jsonNode) {
        if (con == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg.toJson();
        }
        if (jsonNode == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.META_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.META_INPUT_NULL.getName());
            return msg.toJson();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MetaInput metaInput = objectMapper.treeToValue(jsonNode, MetaInput.class);
            return get_metalist(con, MetadataType.HANDSET_MODEL, metaInput).toJson();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static JsonNode get_handset_os(Connection con) {
        return get_metalist(con, MetadataType.HANDSET_OS, null).toJson();
    }

    public static JsonNode get_handset_os_by_id(Connection con) {
        return get_metalist(con, MetadataType.HANDSET_OS_BY_ID, null).toJson();
    }

    public static JsonNode get_handset_browser(Connection con) {
        return get_metalist(con, MetadataType.HANDSET_BROWSER, null).toJson();
    }

    public static JsonNode get_handset_browser_by_id(Connection con) {
        return get_metalist(con, MetadataType.HANDSET_BROWSER_BY_ID, null).toJson();
    }

    public static JsonNode get_site_by_id(Connection con) {
        return get_metalist(con, MetadataType.SITE_BY_ID, null).toJson();
    }

    public static JsonNode get_ext_site_by_id(Connection con) {
        return get_metalist(con, MetadataType.EXT_SITE_BY_ID, null).toJson();
    }

    public static JsonNode get_pub_by_id(Connection con) {
        return get_metalist(con, MetadataType.PUB_BY_ID, null).toJson();
    }

    public static JsonNode get_pub_by_guid(Connection con) {
        return get_metalist(con, MetadataType.PUB_BY_GUID, null).toJson();
    }

    public static JsonNode get_active_advertiser_list(Connection con) {
        return get_metalist(con, MetadataType.ACTIVE_ADVERTISER_LIST, null).toJson();
    }

    public static JsonNode get_account_as_meta_by_id(Connection con) {
        return get_metalist(con, MetadataType.ACCOUNT_AS_META_BY_ID, null).toJson();
    }

    public static JsonNode get_campaign_as_meta_by_id(Connection con) {
        return get_metalist(con, MetadataType.CAMPAIGN_AS_META_BY_ID, null).toJson();
    }

    public static JsonNode get_state_by_country_ui_id(Connection con) {
        return get_metalist(con, MetadataType.STATE_BY_COUNTRY_UI_IDS, null).toJson();
    }

    public static JsonNode get_city_by_state_ui_id(Connection con) {
        return get_metalist(con, MetadataType.CITY_BY_STATE_UI_IDS, null).toJson();
    }

    public static JsonNode get_state_by_ui_ids(Connection con) {
        return get_metalist(con, MetadataType.STATE_BY_UI_IDS, null).toJson();
    }

    public static JsonNode get_city_by_ui_ids(Connection con) {
        return get_metalist(con, MetadataType.CITY_BY_UI_IDS, null).toJson();
    }

    public static JsonNode get_mma_category_tier1_all(Connection con) {
        return get_metalist(con, MetadataType.MMA_CATEGORY_TIER1_ALL, null).toJson();
    }

    public static JsonNode get_mma_category_tier2_by_tier1(Connection con) {
        return get_metalist(con, MetadataType.MMA_CATEGORY_TIER2_BY_TIER1, null).toJson();
    }

    public static JsonNode get_mma_category_by_ids(Connection con) {
        return get_metalist(con, MetadataType.MMA_CATEGORY_BY_IDS, null).toJson();
    }

    public static JsonNode get_mma_industry_tier1_all(Connection con) {
        return get_metalist(con, MetadataType.MMA_INDUSTRY_TIER1_ALL, null).toJson();
    }

    public static JsonNode get_mma_industry_tier2_all(Connection con) {
        return get_metalist(con, MetadataType.MMA_INDUSTRY_TIER2_ALL, null).toJson();
    }

    public static JsonNode get_mma_industry_tier2_by_tier1(Connection con) {
        return get_metalist(con, MetadataType.MMA_INDUSTRY_TIER2_BY_TIER1, null).toJson();
    }

    public static JsonNode get_mma_industry_by_ids(Connection con) {
        return get_metalist(con, MetadataType.MMA_INDUSTRY_BY_IDS, null).toJson();
    }

    public static JsonNode get_all_adpos(Connection con) {
        return get_metalist(con, MetadataType.ADPOS_ALL, null).toJson();
    }

    public static JsonNode get_adpos_by_ids(Connection con) {
        return get_metalist(con, MetadataType.ADPOS_BY_IDS, null).toJson();
    }

    public static JsonNode get_adpos_by_pubids(Connection con) {
        return get_metalist(con, MetadataType.ADPOS_BY_PUBIDS, null).toJson();
    }

    public static JsonNode get_channel_tier1_all(Connection con) {
        return get_metalist(con, MetadataType.CHANNEL_TIER1_ALL, null).toJson();
    }

    public static JsonNode get_channel_tier2_by_tier1(Connection con) {
        return get_metalist(con, MetadataType.CHANNEL_TIER2_BY_TIER1, null).toJson();
    }

    public static JsonNode get_channel_by_ids(Connection con) {
        return get_metalist(con, MetadataType.CHANNEL_BY_IDS, null).toJson();
    }

    public static JsonNode get_channel_by_pubids(Connection con) {
        return get_metalist(con, MetadataType.CHANNEL_BY_PUBIDS, null).toJson();
    }

    public static JsonNode get_adx_based_exchabges_metadata(Connection con) {
        return get_metalist(con, MetadataType.ADX_BASED_EXCHANGES_METATADATA, null).toJson();
    }

    public static JsonNode adpositionget_adxbasedexchanges(Connection con) {
        return get_metalist(con, MetadataType.ADPOSITIONGET_ADXBASEDEXCHNAGES, null).toJson();
    }

    public static JsonNode advertiserupload_adxbasedexchanges(Connection con) {
        return get_metalist(con, MetadataType.ADVERTISERUPLOAD_ADXBASEDEXCHNAGES, null).toJson();
    }

    public static JsonNode bannerupload_adxbasedexchanges(Connection con) {
        return get_metalist(con, MetadataType.BANNERUPLOAD_ADXBASEDEXCHNAGES, null).toJson();
    }

    public static JsonNode videoupload_adxbasedexchanges(Connection con) {
        return get_metalist(con, MetadataType.VIDEOUPLOAD_ADXBASEDEXCHNAGES, null).toJson();
    }

    public static MetaList get_metalist(Connection con, MetadataType metadataType, MetaInput metaInput) {

        if (con == null) {
            MetaList metaList = new MetaList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            metaList.setMsg(msg);
            return metaList;
        }
        PreparedStatement pstmt = null;
        try {
            MetaList metaList = new MetaList();
            switch (metadataType) {
                case STATUS:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_status);
                    break;
                case STATUS_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_status_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CATEGORY_TIER_1:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_tier_1_category);
                    break;
                case CATEGORY_TIER_2:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_tier_2_category);
                    break;
                case CATEGORY_BY_ID:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_category_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case ADX_BASED_EXCHANGES_METATADATA:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.adxbasedexchanges_metadata);
                    break;
                case ADPOSITIONGET_ADXBASEDEXCHNAGES:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.adpositionget_adxbasedexchanges);
                    break;
                case ADVERTISERUPLOAD_ADXBASEDEXCHNAGES:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.advertiserupload_adxbasedexchanges);
                    break;
                case BANNERUPLOAD_ADXBASEDEXCHNAGES:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.bannerupload_adxbasedexchanges);
                    break;
                case VIDEOUPLOAD_ADXBASEDEXCHNAGES:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.videoupload_adxbasedexchanges);
                    break;
                case APP_STORE_ID:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_app_store_id);
                    break;
                case APP_STORE_ID_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_app_store_id_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case COUNTRY:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_ui_country);
                    break;
                case COUNTRY_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_ui_country_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case ALLISP_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_all_isp_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case ISP_BY_COUNTRY:
                    if ("ALL".equalsIgnoreCase(metaInput.getCountry_id_list())) {
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_isp_all_country);
                    } else {
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Metadata.get_isp_country, "<id>", metaInput.getCountry_id_list(),
                                ",", false));
                    }
                    break;
                case CREATIVE_SLOTS:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_creative_slots);
                    break;
                case CREATIVE_SLOTS_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_creative_slots_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CREATIVE_ATTRIBUTES:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_creative_attributes);
                    break;
                case CREATIVE_ATTRIBUTES_BY_ID:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_creative_attributes_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CREATIVE_ATTRIBUTES_BY_FORMAT_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_creative_attributes_by_format_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CREATIVE_FORMATS:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_creative_formats);
                    break;
                case CREATIVE_FORMATS_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_creative_formats_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case HANDSET_MANUFACTURER:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_handset_manufacturer);
                    break;
                case HANDSET_MANUFACTURER_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_handset_manufacturer_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case HANDSET_MANUFACTURER_BY_OS:
                    if ("ALL".equalsIgnoreCase(metaInput.getQuery_id_list())) {
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_handset_manufacturer_by_all_os);
                    } else {
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Metadata.get_handset_manufacturer_by_os, "<id>", metaInput.getQuery_id_list(),
                                ",", false));
                    }
                    break;
                case HANDSET_MODEL:
                    if ("ALL".equalsIgnoreCase(metaInput.getHandset_manufacturer_list())) {
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_handset_model_by_all_manufacturer);
                    } else {
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.Metadata.get_handset_model_by_manufacturer, "<id>", metaInput.getHandset_manufacturer_list(),
                                ",", false));
                    }
                    break;
                case HANDSET_MODEL_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_handset_model_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case HANDSET_OS:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_handset_os);
                    break;
                case HANDSET_OS_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_handset_os_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case HANDSET_BROWSER:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_handset_browser);
                    break;
                case HANDSET_BROWSER_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_handset_browser_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case SITE_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_site_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case EXT_SITE_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_ext_site_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case PUB_BY_ID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_pub_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case PUB_BY_GUID:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_pub_by_guid, "<id>", metaInput.getQuery_id_list(),
                            ",", true));
                    break;
                case ISP_MAPPINGS_DATA_FETCH_FOR_INSERTION:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_data_for_isp_mappings_insertion);
                    pstmt.setInt(1, metaInput.getPageno() * metaInput.getPagesize());
                    pstmt.setInt(2, metaInput.getPagesize());
                    break;
                case ISP_MAPPINGS_ALREADY_PRESENT_DATA_FETCH:
                    pstmt = con.prepareStatement(Metadata.get_isp_mappings_data_for_edit);
                    pstmt.setInt(1, metaInput.getPageno() * metaInput.getPagesize());
                    pstmt.setInt(2, metaInput.getPagesize());
                    break;
                case ACTIVE_ADVERTISER_LIST:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_active_advertiser_list);
                    break;
                case ACCOUNT_AS_META_BY_ID:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_account_as_meta_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CAMPAIGN_AS_META_BY_ID:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_campaign_as_meta_by_id, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case STATE_BY_COUNTRY_UI_IDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_state_by_country_ui_ids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CITY_BY_STATE_UI_IDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_city_by_state_ui_ids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case STATE_BY_UI_IDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_state_by_ui_ids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CITY_BY_UI_IDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.get_city_by_ui_ids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case MMA_CATEGORY_TIER1_ALL:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.mma_category_tier1_all);
                    break;
                case MMA_CATEGORY_TIER2_BY_TIER1:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.mma_category_tier2_by_tier1, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case MMA_CATEGORY_BY_IDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.mma_category_by_ids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case MMA_INDUSTRY_TIER1_ALL:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.mma_industry_tier1_all);
                    break;
                case MMA_INDUSTRY_TIER2_ALL:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.mma_industry_tier2_all);
                    break;
                case MMA_INDUSTRY_TIER2_BY_TIER1:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.mma_industry_tier2_by_tier1, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case MMA_INDUSTRY_BY_IDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.mma_industry_by_ids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case ADPOS_ALL:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.adpos_all);
                    break;
                case ADPOS_BY_IDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.adpos_by_ids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case ADPOS_BY_PUBIDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.adpos_by_pubids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CHANNEL_TIER1_ALL:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.channel_tier1_all);
                    break;
                case CHANNEL_TIER2_BY_TIER1:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.channel_tier2_by_tier1, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CHANNEL_BY_IDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.channel_by_ids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case CHANNEL_BY_PUBIDS:
                    if (metaInput.getQuery_id_list() == null || "[]".equals(metaInput.getQuery_id_list()) || "".equals(metaInput.getQuery_id_list())
                            || "none".equalsIgnoreCase(metaInput.getQuery_id_list())
                            || "all".equals(metaInput.getQuery_id_list())
                            || "[all]".equals(metaInput.getQuery_id_list())
                            || "[none]".equals(metaInput.getQuery_id_list())) {
                        populateEmptyMetalist(metaList);
                        return metaList;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Metadata.channel_by_pubids, "<id>", metaInput.getQuery_id_list(),
                            ",", false));
                    break;
                case AUDIENCE_TAGS:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.aduience_by_deleted);
                    break;
                case AUDIENCE:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_aduiences);
                    pstmt.setString(1,metaInput.getAccount_guid());
                    break;
                case AUDIENCE_PACKAGE:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.get_aduience_package);
                    pstmt.setString(1,metaInput.getAccount_guid());
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            List<MetaField> metaFields = new LinkedList<MetaField>();
            List<AudienceSource> sourceList = new ArrayList<AudienceSource>();
            if (metadataType.getName().equals("AUDIENCE_TAGS")) {
                MetaField metaField = new MetaField();
                audienceType(metaField, rset, sourceList);
                metaFields.add(metaField);
            } else {
                while (rset.next()) {
                    MetaField metaField = new MetaField();
                    switch (metadataType) {
                        case COUNTRY:
                            populateWithEntityId(metaField, rset);
                            break;
                        case COUNTRY_BY_ID:
                            populateWithEntityId(metaField, rset);
                            break;
                        case ALLISP_BY_ID:
                            populateWithEntityId(metaField, rset);
                            break;
                        case ISP_BY_COUNTRY:
                            populateWithEntityId(metaField, rset);
                            break;
                        case CREATIVE_SLOTS:
                            populate_with_description(metaField, rset);
                            break;
                        case CREATIVE_SLOTS_BY_ID:
                            populate_with_description(metaField, rset);
                            break;
                        case HANDSET_OS:
                            populate_with_version(metaField, rset);
                            break;
                        case HANDSET_OS_BY_ID:
                            populate_with_version(metaField, rset);
                            break;
                        case HANDSET_BROWSER:
                            populate_with_version(metaField, rset);
                            break;
                        case HANDSET_BROWSER_BY_ID:
                            populate_with_version(metaField, rset);
                            break;
                        case ISP_MAPPINGS_DATA_FETCH_FOR_INSERTION:
                            populateispdatafetchforinsertion(metaField, rset);
                            break;
                        case ISP_MAPPINGS_ALREADY_PRESENT_DATA_FETCH:
                            populateispmappingsdataforedit(metaField, rset);
                            break;
                        case CATEGORY_TIER_1:
                            populate_with_description(metaField, rset);
                            break;
                        case CATEGORY_TIER_2:
                            populate_with_description(metaField, rset);
                            break;
                        case CATEGORY_BY_ID:
                            populate_with_description(metaField, rset);
                            break;
                        case ADPOS_ALL:
                            populate_with_description(metaField, rset);
                            break;
                        case ADPOS_BY_IDS:
                            populate_with_description(metaField, rset);
                            break;
                        case ADPOS_BY_PUBIDS:
                            populate_with_description(metaField, rset);
                            break;
                        case AUDIENCE:
                            populate_with_id_name_audience(metaField, rset);
                            break;
                        case AUDIENCE_PACKAGE:
                            populate_with_id_name_audience(metaField, rset);
                            break;
                        default:
                            populate(metaField, rset);
                            break;
                    }

                    metaFields.add(metaField);
                }
            }
            Message msg = new Message();
            if (metaFields.size() > 0) {
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            } else {
                msg.setError_code(ErrorEnum.METADATA_EMPTY.getId());
                msg.setMsg(ErrorEnum.METADATA_EMPTY.getName());
            }
            metaList.setMsg(msg);
            metaList.setMetaFieldList(metaFields);
            metaList.setListName("STATUS");
            return metaList;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            MetaList metaList = new MetaList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            metaList.setMsg(msg);
            return metaList;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }

    }


    public static Message insert_update_meta(Connection con, MetaInput metaInput, MetadataType metadataType, boolean createTransaction) {
        if (con == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if (metaInput == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.META_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.META_INPUT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try {
            if (createTransaction) {
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            switch (metadataType) {
                case INSERT_INTO_ISP_MAPPINGS:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.insert_into_isp_mappings);
                    pstmt.setString(1, metaInput.getCountry_name());
                    pstmt.setString(2, metaInput.getIsp_ui_name());
                    pstmt.setString(3, metaInput.getIsp_name());
                    break;
                case UPDATE_ISP_MAPPINGS_ROW:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Metadata.update_isp_mappings_row);
                    pstmt.setString(1, metaInput.getIsp_ui_name());
                    pstmt.setString(2, metaInput.getCountry_name());
                    pstmt.setString(3, metaInput.getIsp_name());
                default:
                    break;
            }

            int returnCode = pstmt.executeUpdate();
            if (createTransaction) {
                con.commit();
            }
            switch (metadataType) {
                case INSERT_INTO_ISP_MAPPINGS:
                    if (returnCode == 0) {
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.INSERT_ISP_MAPPING_FAILED.getId());
                        msg.setMsg(ErrorEnum.INSERT_ISP_MAPPING_FAILED.getName());
                        return msg;
                    }
                    break;
                case UPDATE_ISP_MAPPINGS_ROW:
                    if (returnCode == 0) {
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.UPDATE_ISP_MAPPING_FAILED.getId());
                        msg.setMsg(ErrorEnum.UPDATE_ISP_MAPPING_FAILED.getName());
                        return msg;
                    }
                    break;
                default:
                    break;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            if (createTransaction) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (createTransaction) {
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            }
        }
    }

    public static Map<String, Integer> fetchUiCountryIdAgainstCountryCode(Connection connection) {
        String query = "select id,country_code from ui_targeting_country";

        Map<String, Integer> uiCountryMap = new HashMap<String, Integer>();

        PreparedStatement pstmt = null;
        try {

            pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                uiCountryMap.put(rs.getString("country_code"), rs.getInt("id"));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }

        }

        return uiCountryMap;
    }
}

class AudienceData {
    private String name = null;
    private String type = null;
    private String code = null;
    private String pCode = null;

    private Map<String, Object> additionalParameters = new IdentityHashMap<String, Object>();

    public AudienceData(String name, String type, String pCode, String code, Map<String, Object> additionalParameters) {
        this.pCode = pCode;
        this.name = name;
        this.type = type;
        this.code = code;
        this.additionalParameters = additionalParameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Map<String, Object> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

}

class AudienceSource {
    private String sourceName = null;

    private String sourceId = null;
    Map<String, AudienceData> parentMap = new HashMap<String, AudienceData>();

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Map<String, AudienceData> getParentMap() {
        return parentMap;
    }

    public void setParentMap(Map<String, AudienceData> parentMap) {
        this.parentMap = parentMap;
    }
}