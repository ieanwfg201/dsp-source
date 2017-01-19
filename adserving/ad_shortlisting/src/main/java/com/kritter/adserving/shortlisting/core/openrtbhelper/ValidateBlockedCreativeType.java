package com.kritter.adserving.shortlisting.core.openrtbhelper;

import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;

public class ValidateBlockedCreativeType {
    public static boolean validate(Request request, Set<Short> blockedCreativeTypeSet, Logger logger, short BANNER_CREATIVE_TYPE){
        if(
                null != blockedCreativeTypeSet     &&
                blockedCreativeTypeSet.contains(BANNER_CREATIVE_TYPE)
               )
             {
                ReqLog.debugWithDebugNew(logger, request, "Banner is not allowed for this impression.Creative types block it.");
                 return false;
             }
        return true;
    }
    public static boolean validateInteger(Request request, Set<Integer> blockedCreativeTypeSet, Logger logger, short BANNER_CREATIVE_TYPE){
        if(
                null != blockedCreativeTypeSet     &&
                blockedCreativeTypeSet.contains(BANNER_CREATIVE_TYPE)
               )
             {
                ReqLog.debugWithDebugNew(logger, request, "Banner is not allowed for this impression.Creative types block it.");
                 return false;
             }
        return true;
    }

}
