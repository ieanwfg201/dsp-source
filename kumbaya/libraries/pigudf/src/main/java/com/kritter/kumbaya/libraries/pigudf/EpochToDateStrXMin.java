package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpochToDateStrXMin extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(EpochToDateStrXMin.class);
    public String exec(Tuple input) throws IOException {
        try {
            long timestamp = Long.parseLong(input.get(0).toString());
            TimeZone.setDefault(TimeZone.getTimeZone(input.get(1).toString()));
            String format = input.get(2).toString();
            int length =  format.length();
            int mininterval = Integer.parseInt(input.get(3).toString());
            int mmindex = format.indexOf("mm");
            DateFormat dfm = new SimpleDateFormat(format);
            String str = dfm.format(new Date(timestamp));
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
