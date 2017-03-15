package com.kritter.adserving.shortlisting.targetingmatcher;

import com.alibaba.fastjson.JSON;
import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.common.caches.audience_cache.AudienceCache;
import com.kritter.common.caches.audience_cache.entity.AudienceCacheEntity;
import com.kritter.core.workflow.Context;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.nosql.user.matchid.UserIdCache;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.TargetingProfile;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AudienceTargetingMatcher implements TargetingMatcher {
    private static NoFillReason noFillReason = NoFillReason.AUDIENCE_MISMATCHE;
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private AudienceCache audienceCache;
    private NoSqlNamespaceOperations noSqlNamespaceOperationsInstance;
    private String adNoFillReasonMapKey;

    private UserIdCache userIdCache;

    public static final String NAMESPACE_NAME_KEY = "namespace";
    public static final String TABLE_NAME_KEY = "table_name";
    public static final String ATTRIBUTE_NAME_KEY = "attribute_name_lifetime_history";

    private static final int AUDIENCE_TYPE_CODE = 1;
    private static final int AUDIENCE_TYPE_PACKAGE = 2;


    private Properties audienceCodeProperties;
    private Properties audiencePackageProperties;

    public AudienceTargetingMatcher(String name,
                                    String loggerName,
                                    AdEntityCache adEntityCache,
                                    AudienceCache audienceCache,
                                    NoSqlNamespaceOperations noSqlNamespaceOperationsInstance,
                                    Properties audienceCodeProperties,
                                    Properties audiencePackageProperties,
                                    String adNoFillReasonMapKey,
                                    UserIdCache userIdCache) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.audienceCache = audienceCache;
        this.noSqlNamespaceOperationsInstance = noSqlNamespaceOperationsInstance;

        this.audienceCodeProperties = audienceCodeProperties;
        this.audiencePackageProperties = audiencePackageProperties;

        this.adNoFillReasonMapKey = adNoFillReasonMapKey;

        this.userIdCache = userIdCache;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        if (adIdSet == null || adIdSet.isEmpty()) {
            ReqLog.debugWithDebugNew(this.logger, request, "Candidate ad id set is null or empty.");
            return adIdSet;
        }

        Set<Integer> shortlistedAdIds = new HashSet<Integer>();

        String deviceId = getDeviceIdFromRequest(request);

        for (int adId : adIdSet) {
            ReqLog.debugWithDebugNew(this.logger, request, "audience matcher Processing ad id : {}", adId);

            AdEntity adEntity = adEntityCache.query(adId);
            if (adEntity == null) {
                ReqLog.debugWithDebugNew(this.logger, request, "Ad entity for id : {} is null in ad entity cache", adId);
                continue;
            }

            TargetingProfile targetingProfile = adEntity.getTargetingProfile();
            if (targetingProfile == null) {
                ReqLog.debugWithDebugNew(this.logger, request, "ad:{} no audience targeting", adId);
                continue;
            }


            //struct : {inc: [audience_id], excl: [audience_id]}
            String audienceIncExcTags = targetingProfile.getAudienceTags();


//            Integer audienceType = targetingProfile.getAudienceType();

            //no audience targeting
            if (audienceIncExcTags == null || audienceIncExcTags.trim().length() == 0) {
                shortlistedAdIds.add(adId);
                continue;
            }

            Map<String, List<Integer>> audienceTagMap = JSON.parseObject(audienceIncExcTags, Map.class);

            //优先判断exclude
            List<Integer> excList = audienceTagMap.get("exc");
            List<Integer> incList = audienceTagMap.get("inc");
            List<Integer> packageList = audienceTagMap.get("package");

            int audienceType = 1;
            if ((excList != null && excList.size() != 0) || (incList != null && incList.size() != 0)) {
                audienceType = 1;
                if (deviceId == null) {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(), this.adNoFillReasonMapKey, context);
                    ReqLog.debugWithDebugNew(this.logger, request, "audience matcher Processing ad id : {},deviceId is null", adId);
                    continue;
                }
            } else if (packageList != null && packageList.size() != 0) {
                if (deviceId == null) {
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(), this.adNoFillReasonMapKey, context);
                    ReqLog.debugWithDebugNew(this.logger, request, "audience matcher Processing ad id : {},deviceId is null", adId);
                    continue;
                }
                audienceType = 2;
            } else {
                shortlistedAdIds.add(adId);
                continue;
            }

            //audience code
            if (audienceType == AUDIENCE_TYPE_CODE) {

                String jsonStr = getAudience(request, audienceCodeProperties);
                if (jsonStr == null) {
                    ReqLog.debugWithDebugNew(this.logger, request, "device id not exist : {}", request.getUserId());
                    continue;
                }

                Map<String, List<String>> allTags = JSON.parseObject(jsonStr, Map.class);
                Map<String, Boolean> deviceTags = new HashMap<String, Boolean>();

                //对应五个source:td,am,up,qx mh
                for (Map.Entry<String, List<String>> entry : allTags.entrySet()) {
                    String key = entry.getKey();
                    List<String> values = entry.getValue();
                    if (values != null && values.size() != 0) {
                        for (String code : values)
                            deviceTags.put(key + "_" + code, true);
                    }
                }


                boolean isExeclude = false;
                if (excList == null || excList.size() == 0) {
                    continue;
                }
                for (Integer id : excList) {
                    AudienceCacheEntity entity = audienceCache.query(id);

                    if (entity == null) {
                        ReqLog.debugWithDebugNew(this.logger, request, "audience entity for id : {} is null in audience cache", entity.getId());
                        continue;
                    }

                    String tags = entity.getTags();
                    if (tags == null) {
                        continue;
                    }
                    List<List<String>> tagList = JSON.parseObject(tags, List.class);
                    if (tagList == null || tagList.size() == 0) {
                        continue;
                    }

                    Integer sourceId = entity.getSource_id();
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
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(), this.adNoFillReasonMapKey, context);
                    ReqLog.debugWithDebugNew(this.logger, request, "user Id is execluded in audience targeting : {}", request.getUserId());
                    continue;
                }


                boolean isInclude = false;
                //include,判断是否include
                if (incList == null || incList.size() == 0) {
                    continue;
                }
                for (Integer id : incList) {

                    AudienceCacheEntity entity = audienceCache.query(id);

                    if (entity == null) {
                        ReqLog.debugWithDebugNew(this.logger, request, "audience entity for id : {} is null in audience cache", entity.getId());
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
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(), this.adNoFillReasonMapKey, context);
                    ReqLog.debugWithDebugNew(this.logger, request, "user Id is execluded in audience targeting : {}", request.getUserId());
                    continue;
                }

                shortlistedAdIds.add(adId);
                continue;

            } else if (audienceType == AUDIENCE_TYPE_PACKAGE) {
                //audience package

                String audienceId = getAudience(request, audiencePackageProperties);

                if (audienceId == null) {
                    ReqLog.debugWithDebugNew(this.logger, request, "device id not exist : {}", request.getUserId());
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
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(), this.adNoFillReasonMapKey, context);
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
                    AdNoFillStatsUtils.updateContextForNoFillOfAd(adId, noFillReason.getValue(), this.adNoFillReasonMapKey, context);
                    ReqLog.debugWithDebugNew(this.logger, request, "user Id is execluded in audience targeting : {}", request.getUserId());
                    continue;
                }


                shortlistedAdIds.add(adId);
                continue;

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


    public String getAudience(Request request, Properties properties) {

        //批量查询会多查询几次
//        List<String> values = new ArrayList<String>();
//        Set<NoSqlData> primaryKeyValues = new HashSet<NoSqlData>();
//        primaryKeyValues.add(new NoSqlData(NoSqlData.NoSqlDataType.STRING, request.getUserId()));
        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING, request.getUserId());

        String namespaceName = properties.getProperty(NAMESPACE_NAME_KEY);
        String tableName = properties.getProperty(TABLE_NAME_KEY);
        String attributeName = properties.getProperty(ATTRIBUTE_NAME_KEY);
        Set<String> attributeNameSet = new HashSet<String>();
        attributeNameSet.add(attributeName);

        Map<String, NoSqlData> dataMap = noSqlNamespaceOperationsInstance.fetchSingleRecordAttributes(namespaceName, tableName, primaryKeyValue, attributeNameSet);


        if (dataMap == null || dataMap.get(attributeName) == null || dataMap.size() == 0) {
            return null;
        }

        NoSqlData noSqlData = dataMap.get(attributeName);
        if (noSqlData == null) {
            return null;
        }

        byte[] serializedObject = (byte[]) noSqlData.getValue();

        if (serializedObject == null || serializedObject.length == 0) {
            return null;
        }

        return new String(serializedObject);

    }


    private String getDeviceIdFromRequest(Request request) {
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if (externalUserIds == null || externalUserIds.size() == 0) {
            return null;
        }

        ExternalUserId externalUserId = userIdCache.getHighestPriorityId(externalUserIds);

        if (externalUserId != null && externalUserId.getUserId() != null && externalUserId.getUserId().length() != 0) {
            return externalUserId.getUserId();
        }

        return null;

    }

}
