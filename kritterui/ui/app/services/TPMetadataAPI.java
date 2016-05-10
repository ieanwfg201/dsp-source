package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import models.formelements.SelectOption;
import play.Logger;
import play.db.DB;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.api.entity.metadata.MetaField;
import com.kritter.api.entity.metadata.MetaInput;
import com.kritter.api.entity.metadata.MetaList;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentInputEntity;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentList;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.constants.APP_STORE_ID;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.DeviceType;
import com.kritter.constants.MetadataType;
import com.kritter.constants.MidpValue;
import com.kritter.constants.PageConstants;
import com.kritter.constants.RetargetingSegmentEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.SupplySourceEnum;
import com.kritter.constants.SupplySourceTypeEnum;
import com.kritter.entity.retargeting_segment.RetargetingSegment;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;

public class TPMetadataAPI {



    public static List<SelectOption> emptyOptions(){
        List<SelectOption> emptyOptions = new ArrayList<SelectOption>();
        return emptyOptions;
    }

    private static List<MetaField> fetchCountryList(){
        Connection con = null;
        List<MetaField> mfields = new ArrayList<MetaField>();
        try{
            con = DB.getConnection(); 
            MetaList mlist = ApiDef.get_metalist(con, MetadataType.COUNTRY, null);
            mfields = mlist.getMetaFieldList();
        }catch(Exception e){
            Logger.error("Error in fetching country list", e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close db connection", e);
            } 
        }
        return mfields;
    }
    private static List<MetaField> fetchMetaListByIds(String ids, MetadataType metadataType){
        Connection con = null;
        List<MetaField> mfields = new ArrayList<MetaField>();
        if(ids == null || "".equals(ids) || "[]".equals(ids) || "[null]".equals(ids)){
            return mfields;
        }
        try{
            con = DB.getConnection(); 
            MetaInput mInput = new MetaInput();
            mInput.setQuery_id_list(ids.replaceAll("\\[", "").replaceAll("]", ""));
            mInput.setPagesize(PageConstants.page_size);
            mInput.setPageno(PageConstants.start_index);
            MetaList mlist = ApiDef.get_metalist(con, metadataType, mInput);
            mfields = mlist.getMetaFieldList();
        }catch(Exception e){
            Logger.error("Error in fetching meta list", e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close db connection", e);
            } 
        }
        return mfields;
    }
    private static List<MetaField> fetchCountryListByIds(String countryIds){
        return fetchMetaListByIds(countryIds,MetadataType.COUNTRY_BY_ID);
    }
    private static List<MetaField> fetchBrandListByIds(String brandIds){
        return fetchMetaListByIds(brandIds,MetadataType.HANDSET_MANUFACTURER_BY_ID);
    }
    private static List<MetaField> fetchModelListByIds(String modelIds){
        return fetchMetaListByIds(modelIds,MetadataType.HANDSET_MODEL_BY_ID);
    }
    private static List<MetaField> fetchSiteListByIds(String siteIds){
        return fetchMetaListByIds(siteIds,MetadataType.SITE_BY_ID);
    }
    private static List<MetaField> fetchExtSiteListByIds(String ext_supply_attributes){
        String str = ext_supply_attributes.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");
        String strSplit[] = str.split(",");
        boolean isFirst=true;
        StringBuffer sbuuf = new StringBuffer("");
        for(String element:strSplit){
            String elementSplit[] = element.split("\\|");
            if(elementSplit.length>2){
                if(isFirst){
                    isFirst=false;
                }else{
                    sbuuf.append(",");
                }
                sbuuf.append(elementSplit[2]);
            }
        }
        return fetchMetaListByIds(sbuuf.toString(),MetadataType.EXT_SITE_BY_ID);
    }
    private static List<MetaField> fetchPubListByIds(String pubIds){
        return fetchMetaListByIds(pubIds,MetadataType.PUB_BY_ID);
    }
    private static List<MetaField> fetchPubListByGUIds(String pubGUIds){
        return fetchMetaListByIds(pubGUIds,MetadataType.PUB_BY_GUID);
    }
    private static List<MetaField> fetchCarrierListByIds(String carrierIds){
        return fetchMetaListByIds(carrierIds,MetadataType.ALLISP_BY_ID);
    }
    private static List<MetaField> fetchOsListByIds(String osIds){
        return fetchMetaListByIds(osIds,MetadataType.HANDSET_OS_BY_ID);
    }
    private static List<MetaField> fetchBrowserListByIds(String browserIds){
        return fetchMetaListByIds(browserIds,MetadataType.HANDSET_BROWSER_BY_ID);
    }

    public static ArrayNode countryList(){		
        return metalistToArrayNode(fetchCountryList(), true);
    }

    public static List<SelectOption> countrySelectOptions(){		
        List<SelectOption> soptions = new ArrayList<SelectOption>();
        List<MetaField> mfields = fetchCountryList();
        for (MetaField metaField : mfields) {
            soptions.add(new SelectOption(metaField.getName(), metaField.getName()));
        } 
        return soptions;
    }
    public static List<SelectOption> countrySelectOptionsWithId(){        
        List<SelectOption> soptions = new ArrayList<SelectOption>();
        List<MetaField> mfields = fetchCountryList();
        for (MetaField metaField : mfields) {
            soptions.add(new SelectOption(metaField.getName(), metaField.getId()+""));
        } 
        return soptions;
    }
    public static List<String> getCountryValues(String countryIds){        
        List<String> soptions = new LinkedList<String>();
        List<MetaField> mfields = fetchCountryListByIds(countryIds);
        for (MetaField metaField : mfields) {
            soptions.add(metaField.getName());
        } 
        return soptions;
    }
    public static List<String> getConnectionTypeValues(String connectionType){        
        List<String> soptions = new LinkedList<String>();
        if(connectionType != null){
            String str = connectionType.replaceAll("\\[", "").replaceAll("]", "");
            String[] strSplit = str.split(",");
            for(String elem:strSplit){
                if(!elem.equals(""))
                    soptions.add(ConnectionType.getEnum(Short.parseShort(elem)).getName());
                }
            }
        return soptions;
    }

    public static List<String> getCarrierValues(String carrierIds){        
        List<String> soptions = new LinkedList<String>();
        List<MetaField> mfields = fetchCarrierListByIds(carrierIds);
        for (MetaField metaField : mfields) {
            soptions.add(metaField.getName());
        } 
        return soptions;
    }
    public static List<String> getBrandValues(String brandIds){        
        List<String> soptions = new LinkedList<String>();
        List<MetaField> mfields = fetchBrandListByIds(brandIds);
        for (MetaField metaField : mfields) {
            soptions.add(metaField.getName());
        } 
        return soptions;
    }
    public static List<String> getModelValues(String modelIds){        
        List<String> soptions = new LinkedList<String>();
        List<MetaField> mfields = fetchModelListByIds(modelIds);
        for (MetaField metaField : mfields) {
            soptions.add(metaField.getName());
        } 
        return soptions;
    }
    public static List<String> getSiteValues(String siteIds){        
        List<String> soptions = new LinkedList<String>();
        List<MetaField> mfields = fetchSiteListByIds(siteIds);
        for (MetaField metaField : mfields) {
            soptions.add(metaField.getName());
        } 
        return soptions;
    }
    public static List<String> getExtSiteValues(String ext_supply_attributes){        
        List<String> soptions = new LinkedList<String>();
        List<MetaField> mfields = fetchExtSiteListByIds(ext_supply_attributes);
        for (MetaField metaField : mfields) {
            soptions.add(metaField.getName());
        } 
        return soptions;
    }
    public static List<String> getPubValues(String pubIds){        
        List<String> soptions = new LinkedList<String>();
        List<MetaField> mfields = fetchPubListByIds(pubIds);
        for (MetaField metaField : mfields) {
            soptions.add(metaField.getName());
        } 
        return soptions;
    }
    public static List<String> getPubGuidValues(String pubGuIds){        
        List<String> soptions = new LinkedList<String>();
        List<MetaField> mfields = fetchPubListByGUIds(pubGuIds);
        for (MetaField metaField : mfields) {
            soptions.add(metaField.getName());
        } 
        return soptions;
    }
    /*For Browser and OS*/
    private static String createCSVListfromJson(String idJson, JsonNode node, ObjectMapper mapper){
        if(idJson == null || "".equals(idJson)){
            return null;
        }
        try {
            node = mapper.readValue(idJson, JsonNode.class);
            if(node == null){
                return "{}";
            }
            StringBuffer sBuff = new StringBuffer("");
            Iterator<Entry<String, JsonNode>> itr = node.getFields();
            boolean first = true;
            while(itr.hasNext()){
                Entry<String, JsonNode> entry = itr.next();
                if(!first){
                    sBuff.append(",");
                }else{
                    first = false;
                }
                sBuff.append(entry.getKey());
            }
            if(first){
                return null;
            }else{
                return sBuff.toString();
            }
        } catch (Exception e) {
            Logger.error(e.getMessage(),e);
            return null;
        }
    }
    /*For Browser and OS*/
    private static List<String> createNameListOfJson(List<MetaField> mfields, JsonNode node){
        List<String> list = new LinkedList<String>();
        for (MetaField metaField : mfields) {
            list.add(metaField.getName()+":"+node.get(""+metaField.getId()).getValueAsText());
        } 
        return list;
    }
    public static List<String> getOsValues(String osIds){        
        if(osIds == null || "".equals(osIds) || "[]".equals(osIds) || "[null]".equals(osIds) || "{}".equals(osIds)){
            return new LinkedList<String>();
        }
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode node = mapper.readValue(osIds, JsonNode.class);
            String csvString = createCSVListfromJson(osIds, node, mapper); 
            if(csvString != null){
                List<MetaField> mfields = fetchOsListByIds(csvString);
                return createNameListOfJson(mfields, node);
            }
        }catch(Exception e){
            Logger.error(e.getMessage(),e);
        }
        return new LinkedList<String>();
    }
    public static List<String> getDeviceTypeValues(String deviceTypeIds){
        LinkedList<String> ll = new LinkedList<String>();
        if(deviceTypeIds == null || "".equals(deviceTypeIds) || "[]".equals(deviceTypeIds) || "[null]".equals(deviceTypeIds) || "{}".equals(deviceTypeIds)){
            return ll;
        }
        String deviceTypeIdTrip = deviceTypeIds.trim().replaceAll("\\[", "").replaceAll("]", "");
        String strSplit[] = deviceTypeIdTrip.split(",");
        for(String str:strSplit){
            try{
                ll.add(DeviceType.getEnum(Integer.parseInt(str)).name());
            }catch(Exception e){
                Logger.error(e.getMessage(),e);
            }
        }
                
        return ll;
    }
    public static List<String> getBrowserValues(String browserIds){        
        if(browserIds == null || "".equals(browserIds) || "[]".equals(browserIds) || "[null]".equals(browserIds) || "{}".equals(browserIds)){
            return new LinkedList<String>();
        }
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode node = mapper.readValue(browserIds, JsonNode.class);
            String csvString = createCSVListfromJson(browserIds, node, mapper); 
            if(csvString != null){
                List<MetaField> mfields = fetchBrowserListByIds(csvString);
                return createNameListOfJson(mfields, node);
            }
        }catch(Exception e){
            Logger.error(e.getMessage(),e);
        }
        return new LinkedList<String>();
    }

    public static List<String> getRetargetingSegmentValues(String retargeting){
        List<String> list = new LinkedList<String>();
        if(retargeting ==null ){
            return list;
        }
        String tmp_retargeting = retargeting.replaceAll("\\[", "").replaceAll("]", "").trim();
        if(tmp_retargeting.equals("")){
            return list;
        }
        Connection con = null;
        try{
            con = DB.getConnection(true); 
            RetargetingSegmentInputEntity rsie = new RetargetingSegmentInputEntity();
            rsie.setRetargetingSegmentEnum(RetargetingSegmentEnum.get_retargeting_segments_by_ids);
            rsie.setId_list(tmp_retargeting);
            RetargetingSegmentList rsL = ApiDef.various_get_retargeting_segments(con, rsie);
            if(rsL != null){
                List<RetargetingSegment> rsList = rsL.getRetargeting_segment_list();
                for(RetargetingSegment rs : rsList){
                    list.add(rs.getName());
                }
            }
        }catch(Exception e){
            Logger.error("Error loading carrierList",e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close connection",e);
            } 
        }
        return list;
    }

    public static ArrayNode siteList(){
        Connection con = null;
        ArrayNode siteArray = new ArrayNode(JsonNodeFactory.instance);
        try{
            con = DB.getConnection();
            SiteListEntity siteListEntity = new SiteListEntity();
            siteListEntity.setPage_no(PageConstants.start_index); 
            siteListEntity.setStatus_id(StatusIdEnum.Active.getCode()); 
            siteListEntity.setPage_size(PageConstants.page_size);

            SiteList siteListStatus = ApiDef.list_site(con, siteListEntity);
            if(siteListStatus.getMsg().getError_code()==0){
                List<Site> siteList = siteListStatus.getSite_list();  
                for (Site site : siteList) { 
                    siteArray.add(new SelectOption( site.getName(), site.getId()+"").toJson());
                }
            }
        }catch(Exception e){
            Logger.error("Error loading site list",e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close connection",e);
            } 
        }
        return siteArray;
    }

    public static ArrayNode carrierList(String countryList){
        Connection con = null;
        List<MetaField> mfields = null;
        try{
            if(countryList == null || "none".equals(countryList)){
                return new ArrayNode(JsonNodeFactory.instance);
            }
            con = DB.getConnection(true); 
            MetaInput metaInput = new MetaInput();
            metaInput.setCountry_id_list(countryList);
            MetaList mlist = ApiDef.get_metalist(con, MetadataType.ISP_BY_COUNTRY, metaInput);
            if(mlist != null){
                mfields = mlist.getMetaFieldList();
            }
        }catch(Exception e){
            Logger.error("Error loading carrierList",e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close connection",e);
            } 
        }
        return metalistToArrayNode(mfields, true);
    }

    public static ArrayNode osList(){
        Connection con = null;
        List<MetaField> mfields = null;
        try{
            con = DB.getConnection(true);

            MetaList mlist = ApiDef.get_metalist(con, MetadataType.HANDSET_OS, null);
            mfields = mlist.getMetaFieldList();
        }catch(Exception e){
            Logger.error("Error loading osList",e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close connection",e);
            } 
        }
        return metalistToArrayNodeWithMetadata(mfields, true);
    }
    public static List<SelectOption> osSelectListOption(){
        Connection con = null;
        List<MetaField> mfields = null;
        try{
            con = DB.getConnection(true);

            MetaList mlist = ApiDef.get_metalist(con, MetadataType.HANDSET_OS, null);
            mfields = mlist.getMetaFieldList();
        }catch(Exception e){
            Logger.error("Error loading osList",e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close connection",e);
            } 
        }
        List<SelectOption> optionNodes = new ArrayList<SelectOption>();
        //      if(appendAllField)
        //          optionNodes.add(new SelectOption("All", "").toJson());
        optionNodes.add(new SelectOption("ALL", "ALL"));
        for (MetaField metaField : mfields) {
            optionNodes.add(new SelectOption(metaField.getName(), metaField.getId()+""));
        }
        return optionNodes;
    }

    public static ArrayNode browserList(){
        Connection con = null;
        List<MetaField> mfields = null;
        try{
            con = DB.getConnection(true);
            MetaList mlist = ApiDef.get_metalist(con, MetadataType.HANDSET_BROWSER, null);
            mfields = mlist.getMetaFieldList();
        }catch(Exception e){
            Logger.error("Error loading browserList",e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close connection",e);
            } 
        }
        return metalistToArrayNodeWithMetadata(mfields, true);
    }

    public static ArrayNode brandList(String osList){
        Connection con = null;
        List<MetaField> mfields = null;
        try{
            MetaInput metaInput = new MetaInput();
            metaInput.setQuery_id_list(osList);

            con = DB.getConnection();
            MetaList mlist = ApiDef.get_metalist(con, MetadataType.HANDSET_MANUFACTURER_BY_OS, metaInput);
            mfields = mlist.getMetaFieldList();
        }catch(Exception e){
            Logger.error("Error loading brandList",e);
        } 
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close connection",e);
            } 
        }
        return metalistToArrayNode(mfields, true);
    }

    public static ArrayNode modelList(String manufacturerList){
        Connection con = null;
        List<MetaField> mfields = new ArrayList<MetaField>();
        if(manufacturerList != null && !"".equals(manufacturerList) && !"_".equals(manufacturerList)
                && !"none".equals(manufacturerList)){
            try{
                con = DB.getConnection();
                MetaInput metaInput = new MetaInput();
                metaInput.setHandset_manufacturer_list(manufacturerList);
                MetaList mlist = ApiDef.get_metalist(con, MetadataType.HANDSET_MODEL, metaInput);
                mfields = mlist.getMetaFieldList();
            }catch(Exception e){
                Logger.error("Error loading modelList",e);
            } 
            finally{
                try {
                    if(con != null)
                        con.close();
                } catch (SQLException e) { 
                    Logger.error("Unable to close connection",e);
                } 
            }
        }
        return metalistToArrayNode(mfields, true);
    }

    public static List<SelectOption> creativeAttributesList(){
        Connection con = DB.getConnection();
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        try{
            MetaList mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_ATTRIBUTES, null);
            List<MetaField> mfields = mlist.getMetaFieldList();

            for (MetaField metaField : mfields) {
                selectOptions.add(new SelectOption(metaField.getName(), metaField.getId()+""));
            }
        }catch(Exception e){
            Logger.error("Error while fetching creative attributes",e);
        }


        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) { 
                Logger.error("Unable to close connection",e);
            }
        }

        return selectOptions;
    }



    public static ArrayNode appStoreOptionList(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);

        APP_STORE_ID[] types = APP_STORE_ID.values();
        for (APP_STORE_ID appStoreId : types) {
            optionNodes.add(new SelectOption(appStoreId.name(), appStoreId.ordinal()+"").toJson());
        }
        return optionNodes; 
    }


    public static List<SelectOption> appStores(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();

        SupplySourceTypeEnum[] types = SupplySourceTypeEnum.values();
        for (SupplySourceTypeEnum supplySourceType : types) {
            selectOptions.add(new SelectOption(supplySourceType.name(), supplySourceType.name()));
        }

        return selectOptions;
    }


    public static List<SelectOption> supplySourceTypeOptions(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        SupplySourceTypeEnum[] types = SupplySourceTypeEnum.values();
        for (SupplySourceTypeEnum supplySourceType : types) {
            selectOptions.add(new SelectOption(supplySourceType.name(), supplySourceType.name()));
        }
        return selectOptions; 
    }
    
    public static ArrayNode supplySourceTypeOptionsArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        SupplySourceTypeEnum[] types = SupplySourceTypeEnum.values();
        for (SupplySourceTypeEnum supplySourceType : types) {
            optionNodes.add(new SelectOption(supplySourceType.name(), supplySourceType.name()).toJson());
        }
        return optionNodes; 
    }

    public static List<SelectOption> supplySourceOptions(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        SupplySourceEnum[] types = SupplySourceEnum.values();
        for (SupplySourceEnum supplySource : types) {
            selectOptions.add(new SelectOption(supplySource.name(), supplySource.name()));
        }
        return selectOptions; 
    }
    public static ArrayNode supplySourceOptionsArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        SupplySourceEnum[] types = SupplySourceEnum.values();
        for (SupplySourceEnum supplySource : types) {
            optionNodes.add(new SelectOption(supplySource.name(), supplySource.name()).toJson());
        }
        return optionNodes; 
    }

    public static List<SelectOption> midpOptions(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();

        MidpValue[] types = MidpValue.values();
        for (MidpValue midp : types) {
            if("ALL".equals(midp.name())){
                selectOptions.add(new SelectOption("NONE", midp.name()+""));
            }else{
                selectOptions.add(new SelectOption(midp.name(), midp.name()+""));
            }
        }
        return selectOptions;
    }

    public static ArrayNode appStoreOptions(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);

        APP_STORE_ID[] types = APP_STORE_ID.values();
        for (APP_STORE_ID appStoreId : types) {
            optionNodes.add(new SelectOption(appStoreId.name(), appStoreId.ordinal()+"").toJson());
        }
        return optionNodes; 
    }

    public static ArrayNode metalistToArrayNode(List<MetaField> mfields, boolean appendAllField){

        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        //		if(appendAllField)
        //			optionNodes.add(new SelectOption("All", "").toJson());
        if(mfields != null){
            for (MetaField metaField : mfields) {
                optionNodes.add(new SelectOption(metaField.getName(), metaField.getId()+"").toJson());
            }
        }
        return optionNodes;
    }

    public static ArrayNode metalistToArrayNodeWithMetadata(List<MetaField> mfields, boolean appendAllField){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        //		if(appendAllField)
        //			optionNodes.add(new SelectOption("All", "").toJson());
        for (MetaField metaField : mfields) {
            optionNodes.add(new SelectOption(metaField.getName(), metaField.getId()+"", metaField.getDescription()).toJson());
        }
        return optionNodes;
    }

    public static ArrayNode listToArrayNode(List<SelectOption> options){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (SelectOption selectOption : options) {
            optionNodes.add(selectOption.toJson());
        }
        return optionNodes;
    }
    public static ArrayNode nofillReasonList(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        NoFillReason nfr[] = NoFillReason.values();
        for (NoFillReason nfrval : nfr) {
            optionNodes.add(new SelectOption(nfrval.name(), nfrval.name(), nfrval.name()).toJson());
        }
        return optionNodes;
    }
    public static ArrayNode postImpEventList(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        PostImpressionEvent event[] = PostImpressionEvent.values();
        for (PostImpressionEvent eventval : event) {
            String name = eventval.name();
            if(!name.startsWith("THIRD")){
                optionNodes.add(new SelectOption(name, name, name).toJson());
            }
        }
        return optionNodes;
    }

}
