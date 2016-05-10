package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetKeyValueFromCSVTest {
    @Test
    public void t() throws IOException{
        Tuple a = TupleFactory.getInstance().newTuple();
        a.append(null);
        GetKeyValueFromCSV d = new GetKeyValueFromCSV();
        Tuple expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("");
        expectTuple.append(0.0);
        Assert.assertEquals(d.exec(a), expectTuple);
        
        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(null);
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("");
        expectTuple.append(0.0);
        Assert.assertEquals(d.exec(a), expectTuple);

        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(null);
        a.append(null);
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("");
        expectTuple.append(0.0);
        Assert.assertEquals(d.exec(a), expectTuple);

        a = TupleFactory.getInstance().newTuple();
        a.append(null);
        a.append(null);
        a.append("1");
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("");
        expectTuple.append(0.0);
        Assert.assertEquals(d.exec(a), expectTuple);

        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append(null);
        a.append("1");
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("");
        expectTuple.append(0.0);
        Assert.assertEquals(d.exec(a), expectTuple);
        
        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append("2");
        a.append(",");
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("1");
        expectTuple.append(2.0);
        Assert.assertEquals(d.exec(a), expectTuple);

        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append("2");
        a.append(",");
        a.append("a,b,1");
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("1");
        expectTuple.append(2.0);
        Assert.assertEquals(d.exec(a), expectTuple);

        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append("2");
        a.append(",");
        a.append("a,b,1");
        a.append("1,2");
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("1");
        expectTuple.append(2.0);
        Assert.assertEquals(d.exec(a), expectTuple);
        
        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append("2");
        a.append(",");
        a.append("a,b,1");
        a.append("0,1");
        a.append("2");
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("1");
        expectTuple.append(2.0);
        Assert.assertEquals(d.exec(a), expectTuple);

        a = TupleFactory.getInstance().newTuple();
        a.append("1");
        a.append("2");
        a.append(",");
        a.append("a,b,1");
        a.append("0,1");
        a.append("2");
        a.append(",");
        d = new GetKeyValueFromCSV();
        expectTuple = TupleFactory.getInstance().newTuple();
        expectTuple.append("a,b");
        expectTuple.append(1.0);
        Assert.assertEquals(d.exec(a), expectTuple);
    }

}
