package com.kritter.kumbaya.libraries.query_planner.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.constants.ChartType;
import com.kritter.constants.Frequency;
import com.kritter.constants.ReportingDIMTypeEnum;
import com.kritter.constants.ReportingTableType;
import com.kritter.kumbaya.libraries.data_structs.common.TABLE;
import com.kritter.kumbaya.libraries.data_structs.query_planner.ColumnType;
import com.kritter.kumbaya.libraries.data_structs.query_planner.ErrorCode;
import com.kritter.kumbaya.libraries.data_structs.query_planner.Header;
import com.kritter.kumbaya.libraries.data_structs.query_planner.HeaderType;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KFilter;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KFilterFields;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KGroupby;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KJoin;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KLimit;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KOrderBy;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KProjection;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KumbayaQueryPlanner;
import com.kritter.kumbaya.libraries.data_structs.query_planner.METRICTYPE;
import com.kritter.kumbaya.libraries.data_structs.query_planner.OperationEnum;
import com.kritter.kumbaya.libraries.data_structs.query_planner.PlanStatus;
import com.kritter.kumbaya.libraries.data_structs.query_planner.TimeRange;

public class HelperKumbayaQueryPlanner {

    public static String createDateFormat(Frequency frequency){
        switch(frequency){
        case YESTERDAY:
            return "yyyy-MM-dd 00:00:00";
        case TODAY:
            return "yyyy-MM-dd HH:00:00";
        case LAST7DAYS:
            return "yyyy-MM-dd 00:00:00";
        case CURRENTMONTH:
            return "yyyy-MM-dd 00:00:00";
        case LASTMONTH:
            return "yyyy-MM-dd 00:00:00";
        case DATERANGE:
            return "yyyy-MM-dd 00:00:00";
        case ADMIN_INTERNAL_HOURLY:
            return "yyyy-MM-dd HH:00:00";
        case MONTHLY:
            return "yyyy-MM-01 00:00:00";
        default:
            return null;
        }
    }

    public static TABLE getTABLE(Frequency frequency, ReportingDIMTypeEnum reportDIMTypeEnum,
    		ReportingTableType reportingDTableType){
    	if(reportingDTableType == ReportingTableType.FIRSTLEVEL){
    		if(reportDIMTypeEnum == ReportingDIMTypeEnum.EXHAUSTIVE){
    			switch(frequency){
    			case TODAY:
    				return TABLE.first_level;
    			case YESTERDAY:
    				return TABLE.first_level_daily;
    			case LAST7DAYS:
    				return TABLE.first_level_daily;
    			case CURRENTMONTH:
    				return TABLE.first_level_daily;
    			case LASTMONTH:
    				return TABLE.first_level_daily;
    			case ADMIN_INTERNAL_HOURLY:
    				return TABLE.first_level;
    			case DATERANGE:
    				return TABLE.first_level_daily;
    			case MONTHLY:
    				return TABLE.first_level_monthly;
    			default:
    				return null;
    			}
    		}else{
    			switch(frequency){
    			case TODAY:
    				return TABLE.first_level_limited;
    			case YESTERDAY:
    				return TABLE.first_level_limited_daily;
    			case LAST7DAYS:
    				return TABLE.first_level_limited_daily;
    			case CURRENTMONTH:
    				return TABLE.first_level_limited_daily;
    			case LASTMONTH:
    				return TABLE.first_level_limited_daily;
    			case ADMIN_INTERNAL_HOURLY:
    				return TABLE.first_level_limited;
    			case DATERANGE:
    				return TABLE.first_level_limited_daily;
    			case MONTHLY:
    				return TABLE.first_level_limited_monthly;
    			default:
    				return null;
    			}
    		}
    	}else if(reportingDTableType == ReportingTableType.CHANNEL){
    		switch(frequency){
    		case TODAY:
    			return TABLE.channel_hourly;
    		case YESTERDAY:
    			return TABLE.channel_daily;
    		case LAST7DAYS:
    			return TABLE.channel_daily;
    		case CURRENTMONTH:
    			return TABLE.channel_daily;
    		case LASTMONTH:
    			return TABLE.channel_daily;
    		case ADMIN_INTERNAL_HOURLY:
    			return TABLE.channel_hourly;
    		case DATERANGE:
    			return TABLE.channel_daily;
    		case MONTHLY:
    			return TABLE.channel_monthly;
    		default:
    			return null;
    		}    		
    	}else if(reportingDTableType == ReportingTableType.ADPOSITION){
    		switch(frequency){
    		case TODAY:
    			return TABLE.ad_position_hourly;
    		case YESTERDAY:
    			return TABLE.ad_position_daily;
    		case LAST7DAYS:
    			return TABLE.ad_position_daily;
    		case CURRENTMONTH:
    			return TABLE.ad_position_daily;
    		case LASTMONTH:
    			return TABLE.ad_position_daily;
    		case ADMIN_INTERNAL_HOURLY:
    			return TABLE.ad_position_hourly;
    		case DATERANGE:
    			return TABLE.ad_position_daily;
    		case MONTHLY:
    			return TABLE.ad_position_monthly;
    		default:
    			return null;
    		}    		
    	}else{
    		return null;
    	}

    }


    public static void populatePlanStatus(KumbayaQueryPlanner kQueryPlanner, ErrorCode errorCode, String msg){
        PlanStatus planStatus = kQueryPlanner.getPlanStatus();
        if(planStatus == null){
            planStatus = new PlanStatus();
            planStatus.setErrorcode(errorCode);
            planStatus.setMsg(msg);
            kQueryPlanner.setPlanStatus(planStatus);
        }else{
            planStatus.setErrorcode(errorCode);
            planStatus.setMsg(msg);            
        }
    }

    public static void populateKProjection(HashMap<String, String> kprojectionMap, 
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        KProjection tmp = kQueryPlanner.getKprojection();
        if(tmp == null){
            tmp = new KProjection();
        }
        tmp.setKeyvalue(kprojectionMap);
        kQueryPlanner.setKprojection(tmp);
    }
    public static void populateHeaderList(List<Header> headerList, 
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        List<Header> tmp = headerList;
        if(tmp == null){
            tmp = new LinkedList<Header>();
        }
        kQueryPlanner.setHeaderList(tmp);
    }
    public static void populateKFilter(HashSet<KFilterFields> kFilterSet, 
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        KFilter tmp = kQueryPlanner.getKfilter();
        if(tmp == null){
            tmp = new KFilter();
        }
        tmp.setKeyvalue(kFilterSet);
        kQueryPlanner.setKfilter(tmp);
    }
    public static void populateKJoin(HashMap<String, String> kjoinMap, 
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        KJoin tmp = kQueryPlanner.getKjoin();
        if(tmp == null){
            tmp = new KJoin();
        }
        tmp.setKeyvalue(kjoinMap);
        kQueryPlanner.setKjoin(tmp);
    }

    public static void populateKGroupby(HashSet<String> kgroupbyHashSet, 
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        KGroupby tmp = kQueryPlanner.getKgroupby();
        if(tmp == null){
            tmp = new KGroupby();
        }
        tmp.setKeyvalue(kgroupbyHashSet);
        kQueryPlanner.setKgroupby(tmp);
    }
    public static void populateKOrderby(HashSet<String> korderbyHashSet, 
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        KOrderBy tmp = kQueryPlanner.getKorderby();
        if(tmp == null){
            tmp = new KOrderBy();
        }
        tmp.setKeyvalue(korderbyHashSet);
        kQueryPlanner.setKorderby(tmp);
    }

    public static void populateKLimit(int start_index, int pagesize, 
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        KLimit tmp = kQueryPlanner.getKlimit();
        if(tmp == null){
            tmp = new KLimit();
        }
        tmp.setStartindex(start_index);
        tmp.setPagesize(pagesize);
        kQueryPlanner.setKlimit(tmp);
    }
    
    public static void populateTimeRangeStarttime(String start_time, String column_name,
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        TimeRange tmp = kQueryPlanner.getTimeRange();
        if(tmp == null){
            tmp = new TimeRange();
        }
        tmp.setStart_time(start_time);
        tmp.setColumn_name(column_name);
        kQueryPlanner.setTimeRange(tmp);
    }
    public static void populateTimeRangeEndtime(String end_time, String column_name,
            KumbayaQueryPlanner kQueryPlanner){
        /*Perform null checks beforehand*/
        TimeRange tmp = kQueryPlanner.getTimeRange();
        if(tmp == null){
            tmp = new TimeRange();
        }
        tmp.setEnd_time(end_time);
        tmp.setColumn_name(column_name);
        kQueryPlanner.setTimeRange(tmp);
    }
    public static int setAlias(String aliasPrefix, HashMap<String, String> aliasMap, String table_name, int aliasCounter){
        /*Perform null checks beforehand*/
        if(aliasMap.get(table_name) == null){
            aliasMap.put(table_name, aliasPrefix+aliasCounter);
            return 1;
        }
        return 0;
    }
    
    public static void populateMap(HashMap<String, String> hashMap, String key, String value){
        if(hashMap != null && hashMap.get(key) == null){
            hashMap.put(key, value);
        }
    }   
    public static void populateSet(HashSet<String> hashSet,String key){
        if(hashSet != null && !hashSet.contains(key)){
            hashSet.add(key);
        }
    }
    public static void populateSet(HashSet<KFilterFields> hashSet,KFilterFields key){
        if(hashSet != null && !hashSet.contains(key)){
            hashSet.add(key);
        }
    }

    public static void populateFromList(String prefix,List<Integer> intList, 
            String fact_table, String fact_column, String dim_table, String dim_column_id, String dim_column_name,boolean returnId,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList,
            String ui_header_name, boolean returnGuid, String guidColumn, boolean clickable, boolean onlyFilter){

        if(intList == null){ return;}
        int size = intList.size();
        if(size == 0) {
            if(returnId){
                populateMap(kprojectionMap, aliasMap.get(fact_table)+"."+fact_column, prefix+"_id");
                addToHeader(HeaderType.INT, prefix+"_id", HeaderType.INT, "id of "+ui_header_name, headerList,ColumnType.DIM,false,clickable);
            }
            populateMap(kprojectionMap, aliasMap.get(dim_table)+"."+dim_column_name, prefix+"_name");
            addToHeader(HeaderType.STRING, prefix+"_name", HeaderType.INT, ui_header_name, headerList,ColumnType.DIM,true,false);
            if(returnGuid){
                populateMap(kprojectionMap, aliasMap.get(dim_table)+"."+guidColumn, prefix+"_guid");
                addToHeader(HeaderType.STRING, prefix+"_guid", HeaderType.INT, "GUID of "+ui_header_name, headerList,ColumnType.DIM,false,clickable);                
            }
            KFilterFields kff = new KFilterFields();
            kff.setLeft(aliasMap.get(fact_table)+"."+fact_column);
            kff.setRight(aliasMap.get(dim_table)+"."+dim_column_id);
            kff.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff);
            populateMap(kjoinMap, fact_table, aliasMap.get(fact_table));
            populateMap(kjoinMap, dim_table, aliasMap.get(dim_table));
            populateSet(kgroupbyHashSet, aliasMap.get(fact_table)+"."+fact_column);

        }
        if(size > 0){
            if(!onlyFilter){
                if(returnId){
                    populateMap(kprojectionMap, aliasMap.get(fact_table)+"."+fact_column, prefix+"_id");
                    addToHeader(HeaderType.INT, prefix+"_id", HeaderType.INT, "id of" + ui_header_name, headerList,ColumnType.DIM,false, clickable);
                }
                populateMap(kprojectionMap, aliasMap.get(dim_table)+"."+dim_column_name, prefix+"_name");
                addToHeader(HeaderType.STRING, prefix+"_name", HeaderType.INT, ui_header_name, headerList,ColumnType.DIM,true, false);
                if(returnGuid){
                    populateMap(kprojectionMap, aliasMap.get(dim_table)+"."+guidColumn, prefix+"_guid");
                    addToHeader(HeaderType.STRING, prefix+"_guid", HeaderType.INT, "GUID of "+ui_header_name, headerList,ColumnType.DIM,false, clickable);                
                }
            }
            KFilterFields kffin = new KFilterFields();
            kffin.setLeft(aliasMap.get(fact_table)+"."+fact_column);
            StringBuffer sbuff = new StringBuffer();
            boolean isFirst = true;
            for(Integer i : intList){
                if(isFirst){
                    isFirst =false;
                }else{
                    sbuff.append(",");
                }
                sbuff.append(i);
            }
            kffin.setRight(sbuff.toString());
            kffin.setOperation(OperationEnum.IN);
            populateSet(kFilterSet, kffin);
            KFilterFields kff = new KFilterFields();
            kff.setLeft(aliasMap.get(fact_table)+"."+fact_column);
            kff.setRight(aliasMap.get(dim_table)+"."+dim_column_id);
            kff.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff);
            populateMap(kjoinMap, fact_table, aliasMap.get(fact_table));
            populateMap(kjoinMap, dim_table, aliasMap.get(dim_table));
            if(!onlyFilter){
                populateSet(kgroupbyHashSet, aliasMap.get(fact_table)+"."+fact_column);
            }
        }
    }
    public static void populateFromFactTable(String prefix,List<String> strList, 
            String fact_table, String fact_column,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList,
            String ui_header_name, boolean clickable){

        if(strList == null){ return;}
        int size = strList.size();
        if(size == 0) {
            populateMap(kprojectionMap, aliasMap.get(fact_table)+"."+fact_column, prefix+"_name");
            addToHeader(HeaderType.STRING, prefix+"_name", HeaderType.STRING, ui_header_name, headerList,ColumnType.DIM,true,false);
            populateSet(kgroupbyHashSet, aliasMap.get(fact_table)+"."+fact_column);

        }
        if(size > 0){
            populateMap(kprojectionMap, aliasMap.get(fact_table)+"."+fact_column, prefix+"_name");
            addToHeader(HeaderType.STRING, prefix+"_name", HeaderType.STRING, ui_header_name, headerList,ColumnType.DIM,true, false);
            KFilterFields kffin = new KFilterFields();
            kffin.setLeft(aliasMap.get(fact_table)+"."+fact_column);
            StringBuffer sbuff = new StringBuffer();
            boolean isFirst = true;
            for(String s : strList){
                if(isFirst){
                    isFirst =false;
                }else{
                    sbuff.append(",");
                }
                sbuff.append(s);
            }
            kffin.setRight(sbuff.toString());
            kffin.setOperation(OperationEnum.IN);
            populateSet(kFilterSet, kffin);
            populateSet(kgroupbyHashSet, aliasMap.get(fact_table)+"."+fact_column);
        }
    }
    
    public static void populateHierarchyFromList(List<Integer> intList, 
            String first_table,String first_column,String first_column_name,String second_table,
            String second_column_name, String uiname, String return_prefix, 
            String fact_column,String second_fact_column,String table_name,boolean returnId,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, boolean clickable){

        if(intList == null){ return;}
        int size = intList.size();
        if(size == 0) {
            if(returnId){
                populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column, return_prefix+"_id");
                addToHeader(HeaderType.INT, return_prefix+"_id", HeaderType.INT, "id of "+uiname, headerList,ColumnType.DIM,false, clickable);
            }
            populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_name, return_prefix+"_name");
            addToHeader(HeaderType.STRING, return_prefix+"_name", HeaderType.INT, uiname, headerList,ColumnType.DIM,true, false);
            KFilterFields kff = new KFilterFields();
            kff.setLeft(aliasMap.get(first_table)+"."+first_column);
            kff.setRight(aliasMap.get(second_table)+"."+second_column_name);
            kff.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff);
            KFilterFields kff1 = new KFilterFields();
            kff1.setLeft(aliasMap.get(second_table)+"."+second_fact_column);
            kff1.setRight(aliasMap.get(table_name)+"."+fact_column);
            kff1.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff1);
            populateMap(kjoinMap, table_name, aliasMap.get(table_name));
            populateMap(kjoinMap, first_table, aliasMap.get(first_table));
            populateMap(kjoinMap, second_table, aliasMap.get(second_table));
            populateSet(kgroupbyHashSet, aliasMap.get(first_table)+"."+first_column);

        }
        if(size > 0){
            if(returnId){
                populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column, return_prefix+"_id");
            }
            populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_name, return_prefix+"_name");
            addToHeader(HeaderType.STRING, return_prefix+"_name", HeaderType.INT, uiname, headerList,ColumnType.DIM,true, false);
            KFilterFields kffin = new KFilterFields();
            kffin.setLeft(aliasMap.get(first_table)+"."+first_column);
            StringBuffer sbuff = new StringBuffer();
            boolean isFirst = true;
            for(Integer i : intList){
                if(isFirst){
                    isFirst =false;
                }else{
                    sbuff.append(",");
                }
                sbuff.append(i);
            }
            kffin.setRight(sbuff.toString());
            kffin.setOperation(OperationEnum.IN);
            populateSet(kFilterSet, kffin);
            KFilterFields kff = new KFilterFields();
            kff.setLeft(aliasMap.get(first_table)+"."+first_column);
            kff.setRight(aliasMap.get(second_table)+"."+second_column_name);
            kff.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff);
            KFilterFields kff1 = new KFilterFields();
            kff1.setLeft(aliasMap.get(second_table)+"."+second_fact_column);
            kff1.setRight(aliasMap.get(table_name)+"."+fact_column);
            kff1.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff1);
            populateMap(kjoinMap, table_name, aliasMap.get(table_name));
            populateMap(kjoinMap, first_table, aliasMap.get(first_table));
            populateMap(kjoinMap, second_table, aliasMap.get(second_table));
            populateSet(kgroupbyHashSet, aliasMap.get(first_table)+"."+first_column);
        }
    }
    public static void populateHierarchyFromStringList(List<String> stringList,String first_table,String first_column_filter,String first_column, 
            String first_column_name, String second_table, String second_column_name, String uiname, String return_prefix,
            String fact_column,String second_fact_column,boolean returnId, 
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList,
            String table_name, boolean clickable){
        if(stringList == null){ return;}
        int size = stringList.size();
        if(size == 0) {
            if(returnId){
                populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_filter, return_prefix+"_id");
                addToHeader(HeaderType.STRING, return_prefix+"_id", HeaderType.STRING, "id of "+uiname, headerList,ColumnType.DIM,false, clickable);
            }
            populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_name, return_prefix+"_name");
            addToHeader(HeaderType.STRING, return_prefix+"_name", HeaderType.INT, uiname, headerList,ColumnType.DIM,true, false);
            KFilterFields kff = new KFilterFields();
            kff.setLeft(aliasMap.get(first_table)+"."+first_column);
            kff.setRight(aliasMap.get(second_table)+"."+second_column_name);
            kff.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff);
            KFilterFields kff1 = new KFilterFields();
            kff1.setLeft(aliasMap.get(second_table)+"."+second_fact_column);
            kff1.setRight(aliasMap.get(table_name)+"."+fact_column);
            kff1.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff1);
            populateMap(kjoinMap, table_name, aliasMap.get(table_name));
            populateMap(kjoinMap, first_table, aliasMap.get(first_table));
            populateMap(kjoinMap, second_table, aliasMap.get(second_table));
            populateSet(kgroupbyHashSet, aliasMap.get(first_table)+"."+first_column);

        }
        if(size > 0){
            if(returnId){
                populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_filter, return_prefix+"_id");
            }
            populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_name, return_prefix+"_name");
            addToHeader(HeaderType.STRING, return_prefix+"_name", HeaderType.INT, uiname, headerList,ColumnType.DIM,true, false);
            KFilterFields kffin = new KFilterFields();
            kffin.setLeft(aliasMap.get(first_table)+"."+first_column_filter);
            StringBuffer sbuff = new StringBuffer();
            boolean isFirst = true;
            for(String i : stringList){
                if(isFirst){
                    isFirst =false;
                }else{
                    sbuff.append(",");
                }
                sbuff.append(i);
            }
            kffin.setRight(sbuff.toString());
            kffin.setOperation(OperationEnum.IN);
            populateSet(kFilterSet, kffin);
            KFilterFields kff = new KFilterFields();
            kff.setLeft(aliasMap.get(first_table)+"."+first_column);
            kff.setRight(aliasMap.get(second_table)+"."+second_column_name);
            kff.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff);
            KFilterFields kff1 = new KFilterFields();
            kff1.setLeft(aliasMap.get(second_table)+"."+second_fact_column);
            kff1.setRight(aliasMap.get(table_name)+"."+fact_column);
            kff1.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff1);
            populateMap(kjoinMap, table_name, aliasMap.get(table_name));
            populateMap(kjoinMap, first_table, aliasMap.get(first_table));
            populateMap(kjoinMap, second_table, aliasMap.get(second_table));
            populateSet(kgroupbyHashSet, aliasMap.get(first_table)+"."+first_column);
        }
    }
    public static void populateAdvertiserId(List<String> stringList,String first_table,String first_column_filter,String first_column, 
            String first_column_name, String second_table, String second_column_name, String uiname, String return_prefix,
            String fact_column,String second_fact_column,boolean returnId, 
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList,
            String table_name, boolean clickable){
        if(stringList == null){ return;}
        int size = stringList.size();
        if(size == 0) {
            if(returnId){
                populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_filter, return_prefix+"_id");
                addToHeader(HeaderType.STRING, return_prefix+"_id", HeaderType.STRING, "id of "+uiname, headerList,ColumnType.DIM,false, clickable);
            }
            populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_name, return_prefix+"_name");
            addToHeader(HeaderType.STRING, return_prefix+"_name", HeaderType.INT, uiname, headerList,ColumnType.DIM,true, false);
            KFilterFields kff = new KFilterFields();
            kff.setLeft(aliasMap.get(first_table)+"."+first_column);
            kff.setRight(aliasMap.get(second_table)+"."+second_column_name);
            kff.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff);
            KFilterFields kff1 = new KFilterFields();
            kff1.setLeft(aliasMap.get(second_table)+"."+second_fact_column);
            kff1.setRight(aliasMap.get(table_name)+"."+fact_column);
            kff1.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff1);
            populateMap(kjoinMap, table_name, aliasMap.get(table_name));
            populateMap(kjoinMap, first_table, aliasMap.get(first_table));
            populateMap(kjoinMap, second_table, aliasMap.get(second_table));
            populateSet(kgroupbyHashSet, aliasMap.get(first_table)+"."+first_column);

        }
        if(size > 0){
            if(returnId){
                populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_filter, return_prefix+"_id");
            }
            populateMap(kprojectionMap, aliasMap.get(first_table)+"."+first_column_name, return_prefix+"_name");
            addToHeader(HeaderType.STRING, return_prefix+"_name", HeaderType.INT, uiname, headerList,ColumnType.DIM,true, false);
            KFilterFields kffin = new KFilterFields();
            kffin.setLeft(aliasMap.get(first_table)+"."+first_column_filter);
            StringBuffer sbuff = new StringBuffer();
            boolean isFirst = true;
            for(String i : stringList){
                if(isFirst){
                    isFirst =false;
                }else{
                    sbuff.append(",");
                }
                sbuff.append(i);
            }
            kffin.setRight(sbuff.toString());
            kffin.setOperation(OperationEnum.IN);
            populateSet(kFilterSet, kffin);
            KFilterFields kff = new KFilterFields();
            kff.setLeft(aliasMap.get(first_table)+"."+first_column);
            kff.setRight(aliasMap.get(second_table)+"."+second_column_name);
            kff.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff);
            KFilterFields kff1 = new KFilterFields();
            kff1.setLeft(aliasMap.get(second_table)+"."+second_fact_column);
            kff1.setRight(aliasMap.get(table_name)+"."+fact_column);
            kff1.setOperation(OperationEnum.EQUAL);
            populateSet(kFilterSet, kff1);
            populateMap(kjoinMap, table_name, aliasMap.get(table_name));
            populateMap(kjoinMap, first_table, aliasMap.get(first_table));
            populateMap(kjoinMap, second_table, aliasMap.get(second_table));
            populateSet(kgroupbyHashSet, aliasMap.get(first_table)+"."+first_column);
        }
    }
    
    public static void populateMetric(String prefix,boolean ismetricset,
            String fact_table, String fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, HeaderType headerType,
            String ui_header_name,int order_sequence, TreeMap<Integer, String> treeMap,ChartType chartType, StringBuffer pieProjection){
        if(!ismetricset) {return;}
        if(ChartType.PIE == chartType){
            populateMap(kprojectionMap, metrictype.toString()+"("+aliasMap.get(fact_table)+"."+fact_column+") <PIEPROJECTION>", prefix+"_name");
            pieProjection.append(prefix+"_name");
        }else{
            populateMap(kprojectionMap, metrictype.toString()+"("+aliasMap.get(fact_table)+"."+fact_column+")", prefix+"_name");
        }
        addToHeader(headerType, prefix+"_name", headerType, ui_header_name,headerList,ColumnType.METRIC,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateCTR(String prefix,boolean ismetricset,
            String click_fact_table, String click_fact_column,
            String impression_fact_table, String impression_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "("+metrictype.toString()+"("+aliasMap.get(click_fact_table)+"."+click_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(impression_fact_table)+"."+impression_fact_column+"))*100", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name",HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateECPM(String prefix,boolean ismetricset,
            String demandCharges_fact_table, String demandCharges_fact_column,
            String impression_fact_table, String impression_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "1000*("+metrictype.toString()+"("+aliasMap.get(demandCharges_fact_table)+"."+demandCharges_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(impression_fact_table)+"."+impression_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populatebilledECPM(String prefix,boolean ismetricset,
            String demandCharges_fact_table, String demandCharges_fact_column,
            String billedcsc_fact_table, String billedcsc_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "1000*("+metrictype.toString()+"("+aliasMap.get(demandCharges_fact_table)+"."+demandCharges_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(billedcsc_fact_table)+"."+billedcsc_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateEIPM(String prefix,boolean ismetricset,
            String supplyCost_fact_table, String supplyCost_fact_column,
            String impression_fact_table, String impression_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "1000*("+metrictype.toString()+"("+aliasMap.get(supplyCost_fact_table)+"."+supplyCost_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(impression_fact_table)+"."+impression_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populatebilledEIPM(String prefix,boolean ismetricset,
            String supplyCost_fact_table, String supplyCost_fact_column,
            String billedcsc_fact_table, String billedcsc_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "1000*("+metrictype.toString()+"("+aliasMap.get(supplyCost_fact_table)+"."+supplyCost_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(billedcsc_fact_table)+"."+billedcsc_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateFR(String prefix,boolean ismetricset,
            String impression_fact_table, String impression_fact_column,
            String request_fact_table, String request_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList,String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "100*("+metrictype.toString()+"("+aliasMap.get(impression_fact_table)+"."+impression_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(request_fact_table)+"."+request_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name,headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateEIPC(String prefix,boolean ismetricset,
            String revenue_fact_table, String revenue_fact_column,
            String click_fact_table, String click_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, ""+metrictype.toString()+"("+aliasMap.get(revenue_fact_table)+"."+revenue_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(click_fact_table)+"."+click_fact_column+")", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateECPC(String prefix,boolean ismetricset,
            String demandCharges_fact_table, String demandCharges_fact_column,
            String click_fact_table, String click_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, ""+metrictype.toString()+"("+aliasMap.get(demandCharges_fact_table)+"."+demandCharges_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(click_fact_table)+"."+click_fact_column+")", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateClicksr(String prefix,boolean ismetricset,
            String conversion_fact_table, String conversion_fact_column,
            String click_fact_table, String click_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "100*("+metrictype.toString()+"("+aliasMap.get(conversion_fact_table)+"."+conversion_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(click_fact_table)+"."+click_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateRTR(String prefix,boolean ismetricset,
            String render_fact_table, String render_fact_column,
            String impression_fact_table, String impression_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "("+metrictype.toString()+"("+aliasMap.get(render_fact_table)+"."+render_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(impression_fact_table)+"."+impression_fact_column+"))*100", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name",HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateWTR(String prefix,boolean ismetricset,
            String win_fact_table, String win_fact_column,
            String impression_fact_table, String impression_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "("+metrictype.toString()+"("+aliasMap.get(win_fact_table)+"."+win_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(impression_fact_table)+"."+impression_fact_column+"))*100", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name",HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateEIPW(String prefix,boolean ismetricset,
            String supplyCost_fact_table, String supplyCost_fact_column,
            String win_fact_table, String win_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "1000*("+metrictype.toString()+"("+aliasMap.get(supplyCost_fact_table)+"."+supplyCost_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(win_fact_table)+"."+win_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateECPW(String prefix,boolean ismetricset,
            String demandCharges_fact_table, String demandCharges_fact_column,
            String win_fact_table, String win_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "1000*("+metrictype.toString()+"("+aliasMap.get(demandCharges_fact_table)+"."+demandCharges_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(win_fact_table)+"."+win_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateProfitMargin(String prefix,boolean ismetricset,
            String demandCharges_fact_table, String demandCharges_fact_column,
            String supply_fact_table, String supply_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, "100*(("+metrictype.toString()+"("+aliasMap.get(demandCharges_fact_table)+"."+demandCharges_fact_column+")-"+
                metrictype.toString()+"("+aliasMap.get(supply_fact_table)+"."+supply_fact_column+"))/" +
                metrictype.toString()+"("+aliasMap.get(demandCharges_fact_table)+"."+demandCharges_fact_column+"))", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    
    public static void populateEIPA(String prefix,boolean ismetricset,
            String supplyCost_fact_table, String supplyCost_fact_column,
            String conversion_fact_table, String conversion_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, ""+metrictype.toString()+"("+aliasMap.get(supplyCost_fact_table)+"."+supplyCost_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(conversion_fact_table)+"."+conversion_fact_column+")", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }
    public static void populateECPA(String prefix,boolean ismetricset,
            String demandCharges_fact_table, String demandCharges_fact_column,
            String conversion_fact_table, String conversion_fact_column,METRICTYPE metrictype,
            HashMap<String, String> kprojectionMap, HashSet<KFilterFields> kFilterSet,
            HashSet<String> kgroupbyHashSet,HashMap<String, String> kjoinMap,
            HashMap<String, String> aliasMap, HashSet<String> korderbyHashSet, List<Header> headerList, String ui_header_name,
            int order_sequence, TreeMap<Integer, String> treeMap){
        if(!ismetricset) {return;}
        populateMap(kprojectionMap, ""+metrictype.toString()+"("+aliasMap.get(demandCharges_fact_table)+"."+demandCharges_fact_column+")/"+
                metrictype.toString()+"("+aliasMap.get(conversion_fact_table)+"."+conversion_fact_column+")", prefix+"_name");
        addToHeader(HeaderType.DOUBLE, prefix+"_name", HeaderType.DOUBLE, ui_header_name, headerList,ColumnType.DERIVED,true, false);
        prepareOrderedMap(treeMap, order_sequence, prefix+"_name");
    }

    public static void prepareOrderedMap(TreeMap<Integer, String> treeMap, int key,String value){
        if(treeMap != null && key > -1){
            treeMap.put(key, value);
        }
    }
    public static void prepareKOrderByHashSet(TreeMap<Integer, String> treeMap, HashSet<String> korderBySet){
        if(treeMap != null && korderBySet != null){
            Iterator<String> itr = treeMap.values().iterator();
            while(itr.hasNext()){
                korderBySet.add(itr.next());
            }
        }
    }
    public static void planKProjection(KProjection kProjection , 
            StringBuffer sbuff){
        if(sbuff == null || kProjection == null){
            return;
        }
        Map<String, String> kprojectionMap = kProjection.getKeyvalue();  
        if(kprojectionMap == null){
            return;
        }
        boolean isNotfirst =  false;
        for(String key: kprojectionMap.keySet()){
           if(isNotfirst){
               sbuff.append(",");
           }else{
               isNotfirst = true;
           }
           sbuff.append(key);
           sbuff.append(" as ");
           sbuff.append(kprojectionMap.get(key));
        }
        sbuff.append(" ");
    }
    public static void planKJoin(KJoin kJoin ,StringBuffer sbuff, ChartType chartType){
        if(sbuff == null || kJoin == null){
            return;
        }
        Map<String, String> kJoinMap = kJoin.getKeyvalue();  
        if(kJoinMap == null){
            return;
        }
        boolean isNotfirst =  false;
        for(String key: kJoinMap.keySet()){
           if(isNotfirst){
               sbuff.append(",");
           }else{
               isNotfirst = true;
               sbuff.append(" from ");
           }
           sbuff.append(key);
           sbuff.append(" as ");
           sbuff.append(kJoinMap.get(key));
        }
        if(ChartType.PIE == chartType){
            sbuff.append("  <PIE> ");
        }
        sbuff.append(" ");
        
    }
    public static void planKFilter(KFilter kFilter, TimeRange timeRange ,StringBuffer sbuff){
        if(sbuff == null){
            return;
        }
        boolean isNotfirst =  false;
        if (kFilter != null){
            Set<KFilterFields> kff = kFilter.getKeyvalue();  
            if(kff != null){
                for(KFilterFields key: kff){
                    if(isNotfirst){
                        sbuff.append(" and ");
                    }else{
                        sbuff.append(" where ");
                        isNotfirst = true;
                    }
                    sbuff.append(key.left);
                    switch(key.operation){
                    case EQUAL: sbuff.append(" = ");break;
                    case GT: sbuff.append(" > ");break;
                    case GT_EQ: sbuff.append(" >= ");break;
                    case LT_EQ: sbuff.append(" <= ");break;
                    case LT: sbuff.append(" < ");break;
                    case IN: sbuff.append(" IN (");break;
                    case NE: sbuff.append(" != ");break;
                    }
                    sbuff.append(key.right);
                    if(key.operation == OperationEnum.IN){
                        sbuff.append(")");
                    }
                }
                sbuff.append(" ");
            }
        }
        if(timeRange != null){
            if(isNotfirst){
                sbuff.append(" and ");
            }else{
                sbuff.append(" where ");
            }
            sbuff.append(timeRange.getColumn_name());sbuff.append(">='");sbuff.append(timeRange.getStart_time()+"'");
            sbuff.append(" and ");
            sbuff.append(timeRange.getColumn_name());sbuff.append("<='");sbuff.append(timeRange.getEnd_time()+"'");
       }
    }
    public static int planPieChart(ReportingEntity reportingEntity, StringBuffer sbuff,int aliasCounter, String aliasPrefix,
            HashMap<String, String> aliasMap, StringBuffer pieProjection){
        if(sbuff == null || reportingEntity.getChartType() != ChartType.PIE){
            return 0;
        }
        StringBuffer pieBuffer = new StringBuffer();
        pieBuffer.append(", (");
        pieBuffer.append(sbuff.toString());
        pieBuffer.append(")");
        int indexOfPie=pieBuffer.indexOf("<PIE>");
        pieBuffer.replace(indexOfPie, indexOfPie+5, "");
        indexOfPie=pieBuffer.indexOf("<PIEPROJECTION>");
        pieBuffer.replace(indexOfPie, indexOfPie+15, "");
        int returncode = HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, pieBuffer.toString(), aliasCounter);
        pieBuffer.append(" as ");
        pieBuffer.append(aliasPrefix);
        int suffix = aliasCounter+returncode;
        pieBuffer.append(suffix);
        pieBuffer.append(" ");
        indexOfPie=sbuff.indexOf("<PIE>");
        sbuff.replace(indexOfPie, indexOfPie+5, pieBuffer.toString());
        indexOfPie=sbuff.indexOf("<PIEPROJECTION>");
        sbuff.replace(indexOfPie, indexOfPie+15, "/" +aliasPrefix+suffix+"."+pieProjection.toString());
        return 1;
         
    }
    public static void planKGroupBy(KGroupby kGroupby ,StringBuffer sbuff){
        if(sbuff == null || kGroupby == null){
            return;
        }
        Set<String> kGroupBySet = kGroupby.getKeyvalue();  
        if(kGroupBySet == null){
            return;
        }
        boolean isNotfirst = false;
        for(String key:kGroupBySet){
            if(isNotfirst){
                sbuff.append(",");
            }else{
                isNotfirst=true;
                sbuff.append(" group by ");
            }
            sbuff.append(key);
        }
    }

    public static void planKOrderBy(KOrderBy kOrderby ,StringBuffer sbuff, boolean desc){
        if(sbuff == null || kOrderby == null){
            return;
        }
        Set<String> kOrderBySet =kOrderby.getKeyvalue(); 
        if(kOrderBySet == null){
            return;
        }
        boolean isNotfirst = false;
        for(String key:kOrderBySet){
            if(isNotfirst){
                sbuff.append(",");
            }else{
                isNotfirst=true;
                sbuff.append(" order by ");
            }
            sbuff.append(key);
        }
        if(desc && isNotfirst){
            sbuff.append(" desc ");                
        }

    }

    
    public static void planKLimit(KLimit kLimit ,StringBuffer sbuff){
        if(sbuff == null || kLimit == null){
            return;
        }
        sbuff.append(" limit ");
        sbuff.append(kLimit.getStartindex()*kLimit.getPagesize());
        sbuff.append(",");
        sbuff.append(kLimit.getPagesize());
    }
    
    public static void addToHeader(HeaderType headerType, String name, HeaderType id_headerType, String ui_name,List<Header> headerList, 
            ColumnType columnType, boolean visible, boolean clickable){
        Header header = new Header();
        header.setHeaderType(headerType);
        header.setName(name);
        header.setUi_name(ui_name);
        header.setId_headerType(id_headerType);
        header.setColumnType(columnType);
        header.setVisible(visible);
        header.setClickable(clickable);
        headerList.add(header);
    }
}
