package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RollUpDateConversion extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(RollUpDateConversion.class);
    public String exec(Tuple input) throws IOException {
        try {
            if(input.size() != 2){
                return "";
            }
            String rolluptype = input.get(0).toString();
            String dateStr  = input.get(1).toString();
            if("D".equals(rolluptype)){
                return dateStr.substring(0,10)+" 00:00:00";
            }
            if("M".equals(rolluptype)){
                return dateStr.substring(0,8)+"01 00:00:00";
            }
            if("Y".equals(rolluptype)){
                return dateStr.substring(0,5)+"01-01 00:00:00";
            }
            if("H".equals(rolluptype)){
                return dateStr.substring(0,13)+":00:00";
            }
            return "";
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return "";
        }
    }
}
