package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetNElementMTuple extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(GetNElementMTuple.class);

    public String exec(Tuple input) throws IOException {
            String defaultRet = "-1";
            if (input == null && input.get(0) == null){
                return defaultRet;
            }
            try {
                    defaultRet = input.get(0).toString();
                    if(input.size() == 4 && input.get(1) != null && input.get(2) != null && input.get(3) != null){
                        int tupleIndex = Integer.parseInt(input.get(2).toString());
                        int elementIndex = Integer.parseInt(input.get(3).toString());
                        DataBag values = (DataBag)input.get(1);
                        Tuple required = null;
                        int i= 0;
                        for(Tuple t : values){
                            if(i==tupleIndex){
                                required = t;
                            }
                            i++;
                        }
                        if(required != null && required.size() > elementIndex){
                            return required.get(elementIndex).toString();
                        }
                    }
                    return defaultRet;
            } catch (Exception e) {
                    LOG.error(e.getMessage());
                    return defaultRet;
            }
    }
}

