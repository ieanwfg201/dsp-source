package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmptyNullToDefault extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(EmptyNullToDefault.class);
    public String exec(Tuple input) throws IOException {
        String defaultRet = "-1";
        try {
            if(input != null && input.size()==2 && input.get(0)!=null){
                if(input.get(1) != null){
                    String str = input.get(1).toString().trim();
                    if(!"".equals(str)){
                        return str;
                    }
                }
                defaultRet = input.get(0).toString();
            }
            return defaultRet;
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return defaultRet;
        }
    }
}
