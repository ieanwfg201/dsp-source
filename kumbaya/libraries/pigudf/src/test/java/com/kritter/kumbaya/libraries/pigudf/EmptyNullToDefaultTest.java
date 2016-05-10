package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EmptyNullToDefaultTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append(null);
        EmptyNullToDefault d = new EmptyNullToDefault();
        Assert.assertEquals(d.exec(a), "-1");
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(null);
        d = new EmptyNullToDefault();
        Assert.assertEquals(d.exec(a), "-1");
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append("1");
        d = new EmptyNullToDefault();
        Assert.assertEquals(d.exec(a), "-1");
        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append("1");
        d = new EmptyNullToDefault();
        Assert.assertEquals(d.exec(a), "1");
        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append(null);
        d = new EmptyNullToDefault();
        Assert.assertEquals(d.exec(a), "1");
        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append("");
        d = new EmptyNullToDefault();
        Assert.assertEquals(d.exec(a), "1");
    }

}
