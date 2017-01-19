package com.kritter.billing.pub_payout_serde;

import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.kritter.constants.Payout;

public class ReadPayoutTest {
    
    @Test
    public void t() throws IOException{
        Assert.assertEquals(ReadPayout.getPayoutPercent(null),Payout.default_payout_percent);
        Assert.assertEquals(ReadPayout.getPayoutPercent(""),Payout.default_payout_percent);
        Assert.assertEquals(ReadPayout.getPayoutPercent("{\"payout\":50}"),50.0);
        Assert.assertEquals(ReadPayout.getPayoutPercent("{\"payout\":12}"),12.0);
    }
}
