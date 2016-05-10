package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetNumericSuffix extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(GetNumericSuffix.class);
    public String exec(Tuple input) throws IOException {
        try {
            if(input != null && input.get(0) != null){
                String str = input.get(0).toString();
                StringBuffer sb = new StringBuffer("");
                boolean found = false;
                for(int i = 0; i < str.length(); i++){
                    char c = str.charAt(i);
                    if(c > 47 && c < 58){
                        sb.append(c);
                        found = true;
                    }
                }
                if(found){
                    return sb.toString();
                }
            }
            return "-1";
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return "-1";
        }
    }
}
