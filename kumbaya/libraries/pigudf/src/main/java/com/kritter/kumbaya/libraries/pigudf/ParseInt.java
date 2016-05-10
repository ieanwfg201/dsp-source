package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseInt extends EvalFunc<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(ParseInt.class);
    public Integer exec(Tuple input) throws IOException {
        int defaultRet = new Integer(-1);
        try {
            if(input != null && input.size()==2 && input.get(0)!=null && input.get(1) != null){
                defaultRet = new Integer(Integer.parseInt(input.get(1).toString()));
                String str = input.get(0).toString().trim();
                if(!"".equals(str)){
                    return new Integer(Integer.parseInt(str));
                }
            }
            return defaultRet;
        }catch (Exception e) {
            LOG.error(e.getMessage());
            return defaultRet;
        }
    }
}
