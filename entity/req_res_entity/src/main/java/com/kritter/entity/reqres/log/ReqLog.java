package com.kritter.entity.reqres.log;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import com.kritter.entity.reqres.entity.Request;

public class ReqLog {
    public static void infoWithDebugNew(Logger logger, Request request, String str){
        logger.info(str);
        if(request.isRequestForSystemDebugging()){
            request.addDebugMessageForTestRequest(str);
        }
    }
    public static void debugWithDebugNew(Logger logger, Request request, String str){
        logger.debug(str);
        if(request.isRequestForSystemDebugging()){
            request.addDebugMessageForTestRequest(str);
        }
    }
    public static void requestDebugNew(Request request, String str){
        if(request.isRequestForSystemDebugging()){
            request.addDebugMessageForTestRequest(str);
        }
    }
    public static void debugWithDebugNew(Logger logger, Request request, String str, Object... object){
        logger.debug(str,object);
        if(request.isRequestForSystemDebugging()){
            request.addDebugMessageForTestRequest(MessageFormatter.arrayFormat(str, object).getMessage());
        }
    }
    public static void errorWithDebugNew(Logger logger, Request request, String str){
        logger.error(str);
        if(request.isRequestForSystemDebugging()){
            request.addDebugMessageForTestRequest(str);
        }
    }
    public static void errorWithDebugNew(Logger logger, Request request, String str, Exception e){
        logger.error(str,e);
        if(request.isRequestForSystemDebugging()){
            request.addDebugMessageForTestRequest(str);
            request.addDebugMessageForTestRequest(ExceptionUtils.getFullStackTrace(e));
        }
    }
    public static void errorWithDebugNew(Logger logger, Request request, String str, Object... object){
        logger.error(str, object);
        if(request.isRequestForSystemDebugging()){
            request.addDebugMessageForTestRequest(MessageFormatter.arrayFormat(str, object).getMessage());
        }
    }
}
