package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*for getting model from A1 - where 1 refers to ctr model
for getting model from A_1 - where 1 refers to ctr model
for getting model from A_1_2 - where 2 refers to ctr model
*/
public class SplitNGetCTRModel extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(SplitNGetCTRModel.class);
    public String exec(Tuple input) throws IOException {
        try {
            if(input != null && input.get(0) != null){
                String str = input.get(0).toString();
                String[] strSplit = str.split("_");
                int len = strSplit.length;
                if(len == 3){
                     return strSplit[2];
                }else {
                    return "-1";
                }
            }
            return "-1";
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return "-1";
        }
    }
}
