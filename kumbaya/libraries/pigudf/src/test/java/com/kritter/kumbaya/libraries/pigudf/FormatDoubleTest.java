package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FormatDoubleTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append("3");
        a.append("");
        FormatDouble d = new FormatDouble();
        Assert.assertEquals(d.exec(a), "0.000");
        a = TupleFactory.getInstance().newTuple();
        a.append("3");
        a.append("0.123445");
        d = new FormatDouble();
        Assert.assertEquals(d.exec(a), "0.123");
        a = TupleFactory.getInstance().newTuple();
        a.append("3");
        a.append("100.1278");
        d = new FormatDouble();
        Assert.assertEquals(d.exec(a), "100.128");
        a = TupleFactory.getInstance().newTuple();
        a.append("3");
        a.append("-100.1278");
        d = new FormatDouble();
        Assert.assertEquals(d.exec(a), "-100.128");
        a = TupleFactory.getInstance().newTuple();
        a.append("3");
        d = new FormatDouble();
        Assert.assertEquals(d.exec(a), "0.000");
        a = TupleFactory.getInstance().newTuple();
        a.append("3");
        a.append(null);
        d = new FormatDouble();
        Assert.assertEquals(d.exec(a), "0.000");

    }

}
