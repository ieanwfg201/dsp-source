package com.kritter.adserving.shortlisting.targetingmatcher;

import com.alibaba.fastjson.JSON;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.common.caches.audience_cache.AudienceCache;
import com.kritter.common.caches.audience_cache.entity.AudienceCacheEntity;
import com.kritter.core.workflow.Context;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AudienceTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private AudienceCache audienceCache;
    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;

    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String PRIMARY_KEY_NAME_KEY = "primary_key_name";
    public static final String ATTRIBUTE_NAME_KEY = "attribute_name_lifetime_history";
    private final String attributeNameHistory;
    private Set<String> attributeNameSet;
    @Getter
    private final String namespaceName;
    @Getter
    private final String tableName;
    @Getter
    private final String primaryKeyName;


    private final String packageAttributeNameHistory;
    private Set<String> packageAttributeNameSet;
    @Getter
    private final String packageNamespaceName;
    @Getter
    private final String packageTableName;
    @Getter
    private final String packagePrimaryKeyName;


    public AudienceTargetingMatcher(String name,
                                    String loggerName,
                                    AdEntityCache adEntityCache,
                                    AudienceCache audienceCache,
                                    NoSqlNamespaceOperations noSqlNamespaceOperationsInstance,
                                    Properties properties,
                                    Properties packageProperties) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.audienceCache = audienceCache;
        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperationsInstance;

        this.namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        this.tableName = properties.getProperty(TABLE_NAME_KEY);
        this.primaryKeyName = properties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.attributeNameHistory = properties.getProperty(ATTRIBUTE_NAME_KEY);
        this.attributeNameSet = new HashSet<String>();
        this.attributeNameSet.add(attributeNameHistory);

        this.packageNamespaceName = packageProperties.getProperty(NAMESPACE_NAME_KEY);
        this.packageTableName = packageProperties.getProperty(TABLE_NAME_KEY);
        this.packagePrimaryKeyName = packageProperties.getProperty(PRIMARY_KEY_NAME_KEY);
        this.packageAttributeNameHistory = packageProperties.getProperty(ATTRIBUTE_NAME_KEY);
        this.packageAttributeNameSet = new HashSet<String>();
        this.packageAttributeNameSet.add(attributeNameHistory);
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        if (adIdSet == null || adIdSet.isEmpty()) {
            ReqLog.debugWithDebugNew(this.logger, request, "Candidate ad id set is null or empty.");
            return adIdSet;
        }

        boolean isSslRequest = request.getSecure();
        if (!isSslRequest) {
            ReqLog.debugWithDebugNew(this.logger, request, "Request is not ssl enabled. passing all the ads.");
            return adIdSet;
        }

        Set<Integer> shortlistedAdIds = new HashSet<Integer>();
        ReqLog.debugWithDebugNew(this.logger, request, "Request is ssl enabled. Selecting ads with secure landing " +
                "url.");
        for (int adId : adIdSet) {
            ReqLog.debugWithDebugNew(this.logger, request, "Processing ad id : {}", adId);
            AdEntity adEntity = adEntityCache.query(adId);
            if (adEntity == null) {
                ReqLog.debugWithDebugNew(this.logger, request, "Ad entity for id : {} is null in ad entity cache",
                        adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            if (targetingProfile == null) {
                ReqLog.debugWithDebugNew(this.logger, request, "ad:{} no audience targeting", adId);
                continue;
            }


            String audienceIds = targetingProfile.getAudienceIds();

            //no audience targeting
            if (audienceIds == null || audienceIds.trim().length() == 0) {
                continue;
            }

            AudienceCacheEntity audienceCacheEntity = audienceCache.query(adId);

            if (audienceCacheEntity == null) {
                ReqLog.debugWithDebugNew(this.logger, request, "audience entity for id : {} is null in audience cache", audienceCacheEntity.getId());
                continue;
            }

            Integer type = targetingProfile.getAudienceType();

            //audience code
            if (type == 0) {


                //request deviceId  ->  tags
                NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING, request.getUserId());
                Map<String, NoSqlData> dataMap = noSqlNamespaceOperationsInstance.fetchSingleRecordAttributes(namespaceName, tableName, primaryKeyValue, this.attributeNameSet);

                if (dataMap == null || dataMap.get(this.attributeNameHistory) == null) {
                    ReqLog.debugWithDebugNew(this.logger, request, "user id not in aerospiker: {}", request.getUserId());
                    continue;
                }

                if (dataMap != null && dataMap.size() != 0) {
                    NoSqlData noSqlData = dataMap.get(this.attributeNameHistory);
                    ReqLog.debugWithDebugNew(this.logger, request, "user id not in aerospiker: {}", request.getUserId());
                    if (null == noSqlData) {
                        continue;
                    }
                }

                NoSqlData noSqlData = dataMap.get(this.attributeNameHistory);
                byte[] serializedObject = (byte[]) noSqlData.getValue();

                if (serializedObject == null || serializedObject.length == 0) {
                    ReqLog.debugWithDebugNew(this.logger, request, "user id not in aerospiker: {}", request.getUserId());
                    continue;
                }

                //获取deviceid对应的tag,deviceId -> map<String,String>

                Map<String, Boolean> deviceTags = new HashMap<String, Boolean>();

                Map<String, List<String>> map = JSON.parseObject(new String((byte[]) noSqlData.getValue()), Map.class);

                //对应五个source:td,am,up,qx mh
                Map<String, List<String>> allList = map;
                for (Map.Entry<String, List<String>> entry : allList.entrySet()) {
                    String key = entry.getKey();
                    List<String> values = entry.getValue();
                    if (values != null && values.size() != 0) {
                        for (String code : values)
                            deviceTags.put(key + "_" + code, true);
                    }
                }


                Map<String, List<Integer>> ids = JSON.parseObject(audienceIds, Map.class);

                List<Integer> incList = ids.get("inc");
                if (incList == null || incList.size() == 0) {
                    continue;
                }


                //优先判断exclude
                //exclude
                List<Integer> excList = ids.get("exc");
                if (excList == null || excList.size() == 0) {
                    continue;
                }

                boolean isExeclude = false;
                for (Integer id : excList) {
                    AudienceCacheEntity entity = audienceCache.query(id);

                    if (entity == null) {
                        continue;
                    }

                    Integer sourceId = entity.getSource_id();
                    String tags = entity.getTags();
                    if (tags == null) {
                        continue;
                    }
                    List<List<String>> tagList = JSON.parseObject(tags, List.class);
                    if (tagList == null || tagList.size() == 0) {
                        continue;
                    }


                    String source = convertSource(sourceId);

                    //同级是或的关系,不同级是并的关系
                    boolean flag = false;
                    for (List<String> level1_tag : tagList) {
                        for (String level2_tag : level1_tag) {
                            flag = flag || isExist(deviceTags, source + "_" + level2_tag);
                        }
                        flag = flag && isExist(deviceTags, source + "_" + level1_tag);
                    }
                    if (flag) { //设备满足该标签定向,但是是execlude,需要排除
                        isExeclude = flag;
                        break;
                    }
                }

                if (isExeclude) {
                    ReqLog.debugWithDebugNew(this.logger, request, "user Id is execluded in audience targeting : {}", request.getUserId());
                    continue;
                }


                boolean isInclude = false;
                //include,判断是否include
                for (Integer id : incList) {

                    AudienceCacheEntity entity = audienceCache.query(id);

                    if (entity == null) {
                        continue;
                    }

                    Integer sourceId = entity.getSource_id();
                    String tags = entity.getTags();
                    if (tags == null) {
                        continue;
                    }
                    List<List<String>> tagList = JSON.parseObject(tags, List.class);
                    if (tagList == null || tagList.size() == 0) {
                        continue;
                    }


                    String source = convertSource(sourceId);

                    //同级是或的关系,不同级是并的关系
                    boolean flag = false;
                    for (List<String> level1_tag : tagList) {
                        for (String level2_tag : level1_tag) {
                            flag = flag || isExist(deviceTags, source + "_" + level2_tag);
                        }
                        flag = flag && isExist(deviceTags, source + "_" + level1_tag);
                    }
                    if (!flag) { //设备不满足该标签定向,需要排除
                        isInclude = flag;
                        break;
                    }

                }

                if (!isInclude) {
                    ReqLog.debugWithDebugNew(this.logger, request, "user Id is execluded in audience targeting : {}", request.getUserId());
                    continue;
                }


            } else {
                //audience package
                NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING, request.getUserId());
                Map<String, NoSqlData> dataMap = noSqlNamespaceOperationsInstance.fetchSingleRecordAttributes(packageNamespaceName, packageTableName, primaryKeyValue, this.packageAttributeNameSet);

                if (dataMap == null || dataMap.get(this.packageAttributeNameHistory) == null) {
                    ReqLog.debugWithDebugNew(this.logger, request, "user id not in aerospiker: {}", request.getUserId());
                    continue;
                }

                if (dataMap != null && dataMap.size() != 0) {
                    NoSqlData noSqlData = dataMap.get(this.packageAttributeNameHistory);
                    ReqLog.debugWithDebugNew(this.logger, request, "user id not in aerospiker: {}", request.getUserId());
                    if (null == noSqlData) {
                        continue;
                    }
                }

                NoSqlData noSqlData = dataMap.get(this.packageAttributeNameHistory);
                byte[] serializedObject = (byte[]) noSqlData.getValue();

                if (serializedObject == null || serializedObject.length == 0) {
                    ReqLog.debugWithDebugNew(this.logger, request, "user id not in aerospiker: {}", request.getUserId());
                    continue;
                }


                String audienceId = new String(serializedObject);

                Map<String, List<Integer>> ids = JSON.parseObject(audienceIds, Map.class);

                List<Integer> incList = ids.get("inc");
                if (incList == null || incList.size() == 0) {
                    continue;
                }


                //优先判断exclude
                //exclude
                List<Integer> excList = ids.get("exc");
                if (excList == null || excList.size() == 0) {
                    continue;
                }

                boolean isExeclude = false;
                for (Integer id : excList) {
                    if (id.equals(audienceId)) {
                        isExeclude = true;
                        break;
                    }
                }

                if (isExeclude) {
                    ReqLog.debugWithDebugNew(this.logger, request, "user Id is execluded in audience targeting : {}", request.getUserId());
                    continue;
                }

                boolean isInclude = false;
                //include,判断是否include
                for (Integer id : incList) {
                    if (id.equals(audienceId)) {
                        isInclude = true;
                        break;
                    }
                }

                if (!isInclude) {
                    ReqLog.debugWithDebugNew(this.logger, request, "user Id is execluded in audience targeting : {}", request.getUserId());
                    continue;
                }


            }


        }
        return shortlistedAdIds;
    }


    public String convertSource(Integer sourceType) {
        if (sourceType == null) {
            return "";
        }

        switch (sourceType) {
            case 1:
                return "td";
            case 2:
                return "am";
            case 3:
                return "up";
            case 4:
                return "qx";
            case 5:
                return "mh";
            default:
                return "";
        }
    }

    public boolean isExist(Map<String, Boolean> map, String code) {

        Boolean flag = map.get(code);
        if (flag == null) {
            return false;
        } else {
            return true;
        }
    }

}
