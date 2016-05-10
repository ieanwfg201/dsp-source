package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExtractFromBagOfTupleTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        //a.append(""+date.getTime());
        //a.append("yyyy-MM-dd HH:mm:00");
        ExtractFromBagOfTuple d = new ExtractFromBagOfTuple();
        a.append(null);
        System.out.println(a);
        //Assert.assertEquals(d.exec(a), dfm.format(date));
        
    }

}
