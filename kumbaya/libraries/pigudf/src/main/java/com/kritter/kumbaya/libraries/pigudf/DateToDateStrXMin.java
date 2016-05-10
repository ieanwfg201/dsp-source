package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateToDateStrXMin extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(DateToDateStrXMin.class);
    public String exec(Tuple input) throws IOException {
        try {
            String str = input.get(0).toString();
            String format = input.get(1).toString();
            int length =  format.length();
            int mininterval = Integer.parseInt(input.get(2).toString());
            int mmindex = format.indexOf("mm");
            int mm = Integer.parseInt(str.substring(mmindex,mmindex+2));
            String pref = str.substring(0,mmindex);
            String suffix = "";
            if(mmindex+2 < length){
                suffix = format.substring(mmindex+2);
            }
            String mmStr = "";
            int divisor = mm / mininterval;
            int xvalue = divisor * mininterval;
            if(xvalue < 10){
                return pref+"0"+xvalue+suffix;
            }
            return pref+xvalue+suffix;
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }
}
