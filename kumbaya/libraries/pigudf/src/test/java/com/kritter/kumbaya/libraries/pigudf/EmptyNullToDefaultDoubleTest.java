package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EmptyNullToDefaultDoubleTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append(null);
        EmptyNullToDefaultDouble d = new EmptyNullToDefaultDouble();
        Assert.assertEquals(d.exec(a), new Double(0.0));
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(null);
        d = new EmptyNullToDefaultDouble();
        Assert.assertEquals(d.exec(a), new Double(0.0));
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(1);
        d = new EmptyNullToDefaultDouble();
        Assert.assertEquals(d.exec(a), new Double(0.0));
        a = TupleFactory.getInstance().newTuple();
        a.append(2);
        a.append(1);
        d = new EmptyNullToDefaultDouble();
        Assert.assertEquals(d.exec(a), new Double(2));
        a = TupleFactory.getInstance().newTuple();
        a.append(1);
        a.append(null);
        d = new EmptyNullToDefaultDouble();
        Assert.assertEquals(d.exec(a), new Double(0.0));
        a = TupleFactory.getInstance().newTuple();
        a.append("");
        a.append(1);
        d = new EmptyNullToDefaultDouble();
        Assert.assertEquals(d.exec(a), new Double(0.0));
    }

}
