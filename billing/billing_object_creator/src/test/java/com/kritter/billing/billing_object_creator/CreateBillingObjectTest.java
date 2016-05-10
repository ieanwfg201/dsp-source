package com.kritter.billing.billing_object_creator;

import java.io.IOException;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.kritter.postimpression.thrift.struct.Billing;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;

public class CreateBillingObjectTest {
    @Test
    public void t() throws IOException{
        PostImpressionRequestResponse pirr = new PostImpressionRequestResponse();
        Billing billing = new Billing();
        Billing expected = billing.deepCopy();
        CreateBillingObject.create(null, billing);
        Assert.assertEquals(billing, expected);
        
        pirr = new PostImpressionRequestResponse();
        pirr.setAdId(1);
        billing = new Billing();
        expected = billing.deepCopy();
        CreateBillingObject.create(pirr, billing);
        expected.setAdId(1);
        Assert.assertEquals(billing, expected);
        
        pirr = new PostImpressionRequestResponse();
        pirr.setAdId(1);
        pirr.setAdvertiser_bid(0.1);
        pirr.setCampaignId(2);
        billing = new Billing();
        expected = billing.deepCopy();
        CreateBillingObject.create(pirr, billing);
        expected.setAdId(1);
        expected.setCampaignId(2);
        Assert.assertEquals(billing, expected);
    }
}
