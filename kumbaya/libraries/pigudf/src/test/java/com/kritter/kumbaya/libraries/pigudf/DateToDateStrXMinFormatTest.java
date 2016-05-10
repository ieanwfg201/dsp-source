package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DateToDateStrXMinFormatTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        String format = "yyyy-MM-dd HH:mm:ss";
        String destFormat = "yyyy-MM-dd-HH-mm";
        a.append("2013-03-25 12:14:00");
        a.append(format);
        a.append("15");
        a.append(destFormat);
        DateToDateStrXMinFormat d = new DateToDateStrXMinFormat();
        Assert.assertEquals(d.exec(a), "2013-03-25-12-00");
        a = TupleFactory.getInstance().newTuple();
        format = "yyyy-MM-dd HH:mm:ss";
        a.append("2013-03-25 12:22:00");
        a.append(format);
        a.append("15");
        destFormat = "yyyy-MM-dd-HH-mm";
        a.append(destFormat);
        d = new DateToDateStrXMinFormat();
        Assert.assertEquals(d.exec(a), "2013-03-25-12-15");
        a = TupleFactory.getInstance().newTuple();
        format = "yyyy-MM-dd HH:mm:ss";
        a.append("2013-03-25 12:43:11");
        a.append(format);
        a.append("15");
        destFormat = "yyyy-MM-dd-HH-mm";
        a.append(destFormat);
        d = new DateToDateStrXMinFormat();
        Assert.assertEquals(d.exec(a), "2013-03-25-12-30");
        a = TupleFactory.getInstance().newTuple();
        format = "yyyy-MM-dd HH:mm:ss";
        a.append("2013-03-25 12:59:11");
        a.append(format);
        a.append("15");
        destFormat = "yyyy-MM-dd-HH-mm";
        a.append(destFormat);
        d = new DateToDateStrXMinFormat();
        Assert.assertEquals(d.exec(a), "2013-03-25-12-45");
        a = TupleFactory.getInstance().newTuple();
        format = "yyyy-MM-dd HH:mm:ss";
        a.append("2013-03-25 12:59:00");
        a.append(format);
        a.append("15");
        destFormat = "yyyy-MM-dd HH:mm";
        a.append(destFormat);
        d = new DateToDateStrXMinFormat();
        Assert.assertEquals(d.exec(a), "2013-03-25 12:45");
         a = TupleFactory.getInstance().newTuple();
        format = "yyyy-MM-dd HH:mm:00";
        a.append("2013-03-25 12:59:59");
        a.append(format);
        a.append("15");
        destFormat = "yyyy-MM-dd HH:mm";
        a.append(destFormat);
        d = new DateToDateStrXMinFormat();
        Assert.assertEquals(d.exec(a), "2013-03-25 12:45");
        a = TupleFactory.getInstance().newTuple();
        format = "yyyy-MM-dd HH:mm:00";
        a.append("2013-03-25 12:59:59");
        a.append(format);
        a.append("15");
        destFormat = "yyyy-MM-dd HH:mm:00";
        a.append(destFormat);
        d = new DateToDateStrXMinFormat();
        Assert.assertEquals(d.exec(a), "2013-03-25 12:45:00");
    }

}
