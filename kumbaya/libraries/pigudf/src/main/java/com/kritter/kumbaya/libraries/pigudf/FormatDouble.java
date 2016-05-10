package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormatDouble extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(FormatDouble.class);
    public String exec(Tuple input) throws IOException {
        try {
            int precision = Integer.parseInt(input.get(0).toString());
            double value = 0.0;
            if(input.size() > 1 && input.get(1) != null){
                String pattern = input.get(1).toString();
                if(!"".equals(pattern.trim())){
                    value = Double.parseDouble(pattern);
                }
            }
            return String.format("%."+precision+"f", value) ;
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }
}
