package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*for getting model from A1 - where 1 refers to bidder model
for getting model from A_1 - where 1 refers to bidder model
for getting model from A_1_2 - where 1 refers to bidder model
*/
public class SplitNGetBidderModel extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(SplitNGetBidderModel.class);
    public String exec(Tuple input) throws IOException {
        try {
            if(input != null && input.get(0) != null){
                String str = input.get(0).toString();
                String[] strSplit = str.split("_");
                int len = strSplit.length;
                if(len == 1){
                    StringBuffer sb = new StringBuffer("");
                    boolean found = false;
                    for(int i = 0; i < strSplit[0].length(); i++){
                        char c = strSplit[0].charAt(i);
                        if(c > 47 && c < 58){
                            sb.append(c);
                            found = true;
                        }
                    }
                    if(found){
                        return sb.toString();
                    }
                }else if(len > 1){
                    return strSplit[1];
                }
            }
            return "-1";
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return "-1";
        }
    }
}
