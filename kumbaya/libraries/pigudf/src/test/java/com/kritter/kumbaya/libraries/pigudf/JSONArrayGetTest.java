package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JSONArrayGetTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append(null);
        JSONArrayGet d = new JSONArrayGet();
        Assert.assertEquals(d.exec(a), "-1");
        a = TupleFactory.getInstance().newTuple();
        a.append("379");
        d = new JSONArrayGet();
        Assert.assertEquals(d.exec(a), "379");
        a = TupleFactory.getInstance().newTuple();
        a.append("379");
        a.append(null);
        d = new JSONArrayGet();
        Assert.assertEquals(d.exec(a), "379");
        a = TupleFactory.getInstance().newTuple();
        a.append("379");
        a.append(null);
        a.append(null);
        d = new JSONArrayGet();
        Assert.assertEquals(d.exec(a), "379");
        a = TupleFactory.getInstance().newTuple();
        a.append("379");
        a.append("[\"1\"]");
        d = new JSONArrayGet();
        Assert.assertEquals(d.exec(a), "379");
        a = TupleFactory.getInstance().newTuple();
        a.append("379");
        a.append("[\"1\"]");
        a.append("2");
        d = new JSONArrayGet();
        Assert.assertEquals(d.exec(a), "379");
        a = TupleFactory.getInstance().newTuple();
        a.append("379");
        a.append("[\"1\"]");
        a.append("0");
        d = new JSONArrayGet();
        Assert.assertEquals(d.exec(a), "1");
        a = TupleFactory.getInstance().newTuple();
        a.append("379");
        a.append("[\"1\",\"2\"]");
        a.append("0");
        d = new JSONArrayGet();
        Assert.assertEquals(d.exec(a), "1");

    }

}
