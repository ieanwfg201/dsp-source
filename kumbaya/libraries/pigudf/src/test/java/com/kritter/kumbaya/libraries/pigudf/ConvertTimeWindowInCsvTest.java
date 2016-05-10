package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConvertTimeWindowInCsvTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append(null);
        ConvertTimeWindowInCsv d = new ConvertTimeWindowInCsv();
        Assert.assertEquals(d.exec(a), "");
        
        a = TupleFactory.getInstance().newTuple();
        a.append(null);a.append(null);
        d = new ConvertTimeWindowInCsv();
        Assert.assertEquals(d.exec(a), "");

        a = TupleFactory.getInstance().newTuple();
        a.append(null);a.append(null);a.append(null);
        d = new ConvertTimeWindowInCsv();
        Assert.assertEquals(d.exec(a), "");

        a = TupleFactory.getInstance().newTuple();
        a.append("1");a.append(null);a.append(null);
        d = new ConvertTimeWindowInCsv();
        Assert.assertEquals(d.exec(a), "");

        a = TupleFactory.getInstance().newTuple();
        a.append("1");a.append(null);a.append(null);
        a.append(null);a.append(null);a.append(null);
        d = new ConvertTimeWindowInCsv();
        Assert.assertEquals(d.exec(a), "1");

        a = TupleFactory.getInstance().newTuple();
        a.append("1");a.append("1");a.append("1234");
        a.append("");a.append("2014-03-01");a.append("H48");
        d = new ConvertTimeWindowInCsv();
        Assert.assertEquals(d.exec(a), "12014-03-01 01:00:0034");
    }

}
