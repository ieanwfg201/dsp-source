package com.kritter.adserving.shortlisting.core.openrtbhelper;

import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;

public class ValidateRichMediaType {
    public static boolean validate(Request request, Logger logger){
        if(!request.getSite().isRichMediaAllowed())
        {
            ReqLog.debugWithDebugNew(logger, request, "Richmedia is not allowed for this impression.Site does not allow it.");
            return false;
        }
        return true;
    }
    public static boolean validateDeviceHandset(Request request, Logger logger){
        if(null == request.getHandsetMasterData() || !request.getHandsetMasterData().isDeviceJavascriptCompatible())
        {
            ReqLog.debugWithDebugNew(logger, request, "Richmedia is not allowed for this impression.Requesting handset is not javascript compatible.");
            return false;
        }
        return true;
    }

    public static boolean validateBCAT(Request request, Logger logger, Set<Short> blockedCreativeTypeSet, short RICHMEDIA_CREATIVE_TYPE){
        if(
                null != blockedCreativeTypeSet     &&
                blockedCreativeTypeSet.contains(RICHMEDIA_CREATIVE_TYPE)
               )
             {
                ReqLog.debugWithDebugNew(logger, request, "Richmedia is not allowed for this impression.Creative types do not allow richmedia.");
                 return false;
             }
        return true;
    }
    public static boolean validateBCATInteger(Request request, Logger logger, Set<Integer> blockedCreativeTypeSet, short RICHMEDIA_CREATIVE_TYPE){
        if(
                null != blockedCreativeTypeSet     &&
                blockedCreativeTypeSet.contains(RICHMEDIA_CREATIVE_TYPE)
               )
             {
                ReqLog.debugWithDebugNew(logger, request, "Richmedia is not allowed for this impression.Creative types do not allow richmedia.");
                 return false;
             }
        return true;
    }
}
