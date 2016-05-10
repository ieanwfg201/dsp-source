package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetFirstNotNullTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append(null);
        GetFirstNotNull d = new GetFirstNotNull();
        Assert.assertEquals(d.exec(a), null);
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(null);
        d = new GetFirstNotNull();
        Assert.assertEquals(d.exec(a), null);
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(null);
        a.append("1");
        d = new GetFirstNotNull();
        Assert.assertEquals(d.exec(a), "1");
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append("1");
        a.append(null);
        d = new GetFirstNotNull();
        Assert.assertEquals(d.exec(a), "1");
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append("1");
        a.append("2");
        d = new GetFirstNotNull();
        Assert.assertEquals(d.exec(a), "1");
        a = TupleFactory.getInstance().newTuple();
        a.append("3");
        a.append(null);
        a.append(null);
        d = new GetFirstNotNull();
        Assert.assertEquals(d.exec(a), "3");


    }

}
