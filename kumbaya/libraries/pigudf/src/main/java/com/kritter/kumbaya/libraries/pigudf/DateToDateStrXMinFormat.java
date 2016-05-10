package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateToDateStrXMinFormat extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(DateToDateStrXMinFormat.class);
    public String exec(Tuple input) throws IOException {
        try {
            String str = input.get(0).toString();
            String format = input.get(1).toString();
            String destformat = input.get(3).toString();
            int length =  format.length();
            int mininterval = Integer.parseInt(input.get(2).toString());
            int mmindex = format.indexOf("mm");
            int yyyyIndex = format.indexOf("yyyy");
            int ddIndex = format.indexOf("dd");
            int hhIndex = format.indexOf("HH");
            int monthIndex = format.indexOf("MM");
            
            int mm = Integer.parseInt(str.substring(mmindex,mmindex+2));
            String mmStr = "";
            int divisor = mm / mininterval;
            int xvalue = divisor * mininterval;
            mmStr = ""+xvalue;
            if(xvalue < 10){
                mmStr = "0"+xvalue;
            }
            return destformat.replace("yyyy",str.substring(yyyyIndex,yyyyIndex+4)).replace("MM",str.substring(monthIndex,monthIndex+2)).replace("dd",str.substring(ddIndex,ddIndex+2)).replace("HH",str.substring(hhIndex,hhIndex+2)).replace("mm",mmStr);
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }
}
