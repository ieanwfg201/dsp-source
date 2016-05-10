package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmptyNullToDefaultDouble extends EvalFunc<Double> {
    private static final Logger LOG = LoggerFactory.getLogger(EmptyNullToDefaultDouble.class);
    public Double exec(Tuple input) throws IOException {
        double defaultRet = new Double(0.0);
        try {
            if(input != null && input.size()==2 && input.get(0)!=null && input.get(1) != null){
                String str = input.get(0).toString().trim();
                if(!"".equals(str)){
                    return new Double(Double.parseDouble(str));
                }
                new Double(Double.parseDouble(input.get(1).toString()));
            }
            return defaultRet;
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return defaultRet;
        }
    }
}
