package com.kritter.utils.pub_payout_serde;

import com.kritter.constants.Payout;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonNode;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;



public class ReadPayout{
    private static final Logger LOG = LogManager.getLogger(ReadPayout.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static double getPayoutPercent(String billingpayoutrule){
        try{
            if(billingpayoutrule != null){
                JsonNode rootNode = mapper.readValue(billingpayoutrule,JsonNode.class);
                return rootNode.get(Payout.payout_percent_key).getDoubleValue();
            }
            return Payout.default_payout_percent;
        }catch(Exception e){
                LOG.error(e.getMessage(),e);
                return Payout.default_payout_percent;
        }
    }
}

