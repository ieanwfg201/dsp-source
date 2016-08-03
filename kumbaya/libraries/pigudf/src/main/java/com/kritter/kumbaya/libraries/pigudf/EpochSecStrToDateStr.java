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

public class EpochSecStrToDateStr extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(EpochSecStrToDateStr.class);
    public String exec(Tuple input) throws IOException {
        try {
            long timestamp = Long.parseLong(input.get(0).toString())*1000;
            String pattern = input.get(1).toString();
            TimeZone.setDefault(TimeZone.getTimeZone(input.get(2).toString()));
            DateFormat dfm = new SimpleDateFormat(pattern);
            return dfm.format(new Date(timestamp));
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }
}
