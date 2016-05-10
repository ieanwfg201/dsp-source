package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RollUpDateConversionTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append("D");
        a.append("2013-01-01 01:05:00");
        RollUpDateConversion d = new RollUpDateConversion();
        Assert.assertEquals(d.exec(a), "2013-01-01 00:00:00");
        a = TupleFactory.getInstance().newTuple();
        a.append("M");
        a.append("2013-05-05 01:05:00");
        d = new RollUpDateConversion();
        Assert.assertEquals(d.exec(a), "2013-05-01 00:00:00");
        a = TupleFactory.getInstance().newTuple();
        a.append("Y");
        a.append("2013-02-02 01:05:00");
        d = new RollUpDateConversion();
        Assert.assertEquals(d.exec(a), "2013-01-01 00:00:00");
        a = TupleFactory.getInstance().newTuple();
        a.append("H");
        a.append("2013-02-02 01:05:00");
        d = new RollUpDateConversion();
        Assert.assertEquals(d.exec(a), "2013-02-02 01:00:00");

    }

}
