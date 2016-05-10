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

public class EpochToDateStrXMinTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        String format = "yyyy-MM-dd-HH-mm";
        Date date = new Date();
        DateFormat dfm = new SimpleDateFormat(format);
        a.append(""+date.getTime());
        a.append("UTC");
        a.append(format);
        a.append("15");
        EpochToDateStrXMin d = new EpochToDateStrXMin();
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
        
        a = TupleFactory.getInstance().newTuple();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        format = "yyyy-MM-dd HH:mm:00";
        date = new Date();
        dfm = new SimpleDateFormat(format);
        a.append(""+date.getTime());
        a.append("UTC");
        a.append(format);
        a.append("15");
        d = new EpochToDateStrXMin();
        str  = dfm.format(date);
        mm = Integer.parseInt(str.substring(14,16));
        pref = str.substring(0,14);
        String suffix = str.substring(16);
        System.out.println(pref);
        System.out.println(suffix);
        if(mm < 15){
            Assert.assertEquals(d.exec(a), pref+"00"+suffix);
        }else if(mm < 30){
            Assert.assertEquals(d.exec(a), pref+"15"+suffix);
        }else if(mm < 45){
            Assert.assertEquals(d.exec(a), pref+"30"+suffix);
        }else{
            Assert.assertEquals(d.exec(a), pref+"45"+suffix);
        }

    }

}
