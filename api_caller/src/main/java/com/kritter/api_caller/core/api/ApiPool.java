package com.kritter.api_caller.core.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This class keeps all possible api implementations against their signature.
 */
public class ApiPool {

    private Logger logger;
    private Map<String,Api> apiInstancesAgainstSignature;

    public ApiPool(Logger logger,Map<String,Api> apiInstancesAgainstSignature){

        this.logger = logger;
        this.apiInstancesAgainstSignature = apiInstancesAgainstSignature;
    }

    public Api fetchApiInstanceForSignature(String signature){

        if(null == signature || null == this.apiInstancesAgainstSignature)
            return null;

        return this.apiInstancesAgainstSignature.get(signature);
    }

}
