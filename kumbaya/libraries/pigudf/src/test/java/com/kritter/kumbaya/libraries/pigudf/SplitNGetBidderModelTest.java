package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SplitNGetBidderModelTest {
    @Test
    public void t() throws IOException{
        Tuple a = null;
        SplitNGetBidderModel d = new SplitNGetBidderModel();
        Assert.assertEquals(d.exec(a), "-1");
        a = TupleFactory.getInstance().newTuple();
        a.append("");
        d = new SplitNGetBidderModel();
        Assert.assertEquals(d.exec(a), "-1");
        a = TupleFactory.getInstance().newTuple();
        a.append("K1");
        d = new SplitNGetBidderModel();
        Assert.assertEquals(d.exec(a), "1");
        a = TupleFactory.getInstance().newTuple();
        a.append("KA1");
        d = new SplitNGetBidderModel();
        Assert.assertEquals(d.exec(a), "1");
        a = TupleFactory.getInstance().newTuple();
        a.append("K0123456789");
        d = new SplitNGetBidderModel();
        Assert.assertEquals(d.exec(a), "0123456789");
        a = TupleFactory.getInstance().newTuple();
        a.append("K_123");
        d = new SplitNGetBidderModel();
        Assert.assertEquals(d.exec(a), "123");
        a = TupleFactory.getInstance().newTuple();
        a.append("K_12_1");
        d = new SplitNGetBidderModel();
        Assert.assertEquals(d.exec(a), "12");
    }

}
