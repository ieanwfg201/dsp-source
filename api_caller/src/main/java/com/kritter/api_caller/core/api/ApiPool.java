package com.kritter.api_caller.core.api;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.Map;

/**
 * This class keeps all possible api implementations against their signature.
 */
public class ApiPool
{
    private Logger logger;
    private Map<String,Api> apiInstancesAgainstSignature;

    public ApiPool(String loggerName,Map<String,Api> apiInstancesAgainstSignature)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.apiInstancesAgainstSignature = apiInstancesAgainstSignature;
    }

    public Api fetchApiInstanceForSignature(String signature)
    {
        if(null == signature || null == this.apiInstancesAgainstSignature)
            return null;

        return this.apiInstancesAgainstSignature.get(signature);
    }
}
