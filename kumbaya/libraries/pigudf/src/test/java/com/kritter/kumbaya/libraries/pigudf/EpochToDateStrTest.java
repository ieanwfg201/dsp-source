package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EpochToDateStrTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        a.append(""+date.getTime());
        a.append("yyyy-MM-dd HH:mm:00");
        a.append("UTC");
        EpochToDateStr d = new EpochToDateStr();
        Assert.assertEquals(d.exec(a), dfm.format(date));
    }

}
