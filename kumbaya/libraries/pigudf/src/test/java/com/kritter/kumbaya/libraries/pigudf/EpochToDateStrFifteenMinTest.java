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

public class EpochToDateStrFifteenMinTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        a.append(""+date.getTime());
        a.append("UTC");
        EpochToDateStrFifteenMin d = new EpochToDateStrFifteenMin();
        String str  = dfm.format(date);
        int mm = Integer.parseInt(str.substring(14));
        String pref = str.substring(0,14);
        if(mm < 15){
            Assert.assertEquals(d.exec(a), pref+"00");
        }else if(mm < 30){
            Assert.assertEquals(d.exec(a), pref+"15");
        }else if(mm < 45){
            Assert.assertEquals(d.exec(a), pref+"30");
        }else{
            Assert.assertEquals(d.exec(a), pref+"45");
        }
    }

}
