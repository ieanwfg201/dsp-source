package com.kritter.billing.loglineconverter.postimpression;


import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.kritter.constants.DefaultCurrency;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;

public class PostImpressionConverterTest {
    @Test
    public void t() throws Exception{
        PostImpressionConverter pic = new PostImpressionConverter();
        Assert.assertNull(pic.convert(null));
        
        pic = new PostImpressionConverter();
        PostImpressionRequestResponse pirr = new PostImpressionRequestResponse();
        pirr.setAdId(0);
        pirr.setAdvertiser_bid(0.1);
        pirr.setAuction_currency(DefaultCurrency.defaultCurrency.getName());
        TSerializer ts = new TSerializer(new TBinaryProtocol.Factory());
        Base64 encoder = new Base64(0);
        String s = new String(encoder.encode(ts.serialize(pirr)));
        Assert.assertEquals(pic.convert(s), pirr);

        
    }

}
