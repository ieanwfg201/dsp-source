package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmptyNullZeroToDefaultInt extends EvalFunc<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(EmptyNullZeroToDefaultInt.class);
    public Integer exec(Tuple input) throws IOException {
        int defaultRet = new Integer(-1);
        try {
            if(input != null && input.size()==2 && input.get(0)!=null && input.get(1) != null){
                String str = input.get(0).toString().trim();
                if(!"".equals(str)){
                    int val  = Integer.parseInt(str);
                    if( val == 0 ) {
                        val = -1;
                    } 
                    return new Integer(val);   
                }
                int val  = Integer.parseInt(input.get(1).toString());
                return new Integer(val);
            }
            return defaultRet;
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return defaultRet;
        }
    }
}
