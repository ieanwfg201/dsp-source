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

public class EpochToDateStrFifteenMin extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(EpochToDateStrFifteenMin.class);
    public String exec(Tuple input) throws IOException {
        try {
            long timestamp = Long.parseLong(input.get(0).toString());
            TimeZone.setDefault(TimeZone.getTimeZone(input.get(1).toString()));
            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String str = dfm.format(new Date(timestamp));
            int mm = Integer.parseInt(str.substring(14));
            String pref = str.substring(0,14);
            if(mm < 15){
                return pref+"00";
            }
            if(mm < 30){
                return pref+"15";
            }
            if(mm < 45){
                return pref+"30";
            }
            return pref+"45";
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }
}
