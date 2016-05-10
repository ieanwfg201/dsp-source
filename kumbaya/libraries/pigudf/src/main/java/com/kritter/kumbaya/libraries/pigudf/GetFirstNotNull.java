package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetFirstNotNull extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(GetFirstNotNull.class);
    public String exec(Tuple input) throws IOException {
        String defaultRet = null;
        try {
            if(input != null && input.size()>1){
                int size = input.size();
                for(int i = 1; i < size; i++){
                     Object obj = input.get(i);
                    if(obj != null){
                        return obj.toString();
                    }
                }
                if( input.get(0) != null){
                    defaultRet = input.get(0).toString();
                }
            }
            return defaultRet;
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return defaultRet;
        }
    }
}
