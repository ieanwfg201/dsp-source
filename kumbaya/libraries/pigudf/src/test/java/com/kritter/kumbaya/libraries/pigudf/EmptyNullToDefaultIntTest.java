package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EmptyNullToDefaultIntTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append(null);
        EmptyNullToDefaultInt d = new EmptyNullToDefaultInt();
        Assert.assertEquals(d.exec(a), new Integer(-1));
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(null);
        d = new EmptyNullToDefaultInt();
        Assert.assertEquals(d.exec(a), new Integer(-1));
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append("1");
        d = new EmptyNullToDefaultInt();
        Assert.assertEquals(d.exec(a), new Integer(-1));
        a = TupleFactory.getInstance().newTuple();
        a.append("2");
        a.append("1");
        d = new EmptyNullToDefaultInt();
        Assert.assertEquals(d.exec(a), new Integer(2));
        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append(null);
        d = new EmptyNullToDefaultInt();
        Assert.assertEquals(d.exec(a), new Integer(-1));
        a = TupleFactory.getInstance().newTuple();
        a.append("");
        a.append("1");
        d = new EmptyNullToDefaultInt();
        Assert.assertEquals(d.exec(a), new Integer(-1));
    }

}
