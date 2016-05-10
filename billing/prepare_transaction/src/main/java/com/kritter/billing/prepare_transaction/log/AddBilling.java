package com.kritter.billing.prepare_transaction.log;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.billing.billing_object_creator.CreateBillingObject;
import com.kritter.billing.prepare_transaction.perevent.impl.DirectPublisherCPM;
import com.kritter.billing.prepare_transaction.perevent.impl.DirectPublisherClick;
import com.kritter.billing.prepare_transaction.perevent.impl.DirectPublisherConversion;
import com.kritter.billing.prepare_transaction.perevent.impl.ExchangeCPM;
import com.kritter.billing.prepare_transaction.perevent.impl.ExchangeClick;
import com.kritter.billing.prepare_transaction.perevent.impl.WinNotification;
import com.kritter.billing.prepare_transaction.perevent.impl.InternalExcWinNotification;
import com.kritter.billing.prepare_transaction.perevent.impl.BEVENT_CSCWIN;
import com.kritter.constants.MarketPlace;
import com.kritter.postimpression.thrift.struct.Billing;
import com.kritter.postimpression.thrift.struct.BillingTerminationReason;
import com.kritter.postimpression.thrift.struct.BillingType;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class AddBilling {
    
    private static final Logger LOG = LoggerFactory.getLogger(AddBilling.class);

    public static void createNaddTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billingLogList.add(billing);
        
    }
    
    public static void createNaddNonTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType,DirectPublisherClick dpClick ){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billing.setSupplyCost(dpClick.getEventPayOut());
        billing.setDemandCharges(pirr.getAdvertiser_bid());
        billing.setInternalDemandCharges(pirr.getInternal_max_bid());
        billing.setNetworkpayout(dpClick.getEventPayOut());
        billing.setNetworkrevenue(pirr.getAdvertiser_bid());
        billingLogList.add(billing);
    }
    public static void createNaddNonTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType,DirectPublisherConversion dpConversion ){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billing.setSupplyCost(dpConversion.getEventPayOut());
        billing.setDemandCharges(pirr.getAdvertiser_bid());
        billing.setInternalDemandCharges(pirr.getInternal_max_bid());
        billing.setNetworkpayout(dpConversion.getEventPayOut());
        billing.setNetworkrevenue(pirr.getAdvertiser_bid());
        billingLogList.add(billing);
    }
    public static void createNaddNonTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType,DirectPublisherCPM dpCPM ){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billing.setSupplyCost(dpCPM.getEventPayOut());
        billing.setDemandCharges(pirr.getAdvertiser_bid()/1000);
        billing.setInternalDemandCharges(pirr.getInternal_max_bid()/1000);
        billing.setNetworkpayout(dpCPM.getEventPayOut());
        billing.setNetworkrevenue(pirr.getAdvertiser_bid()/1000);
        billingLogList.add(billing);
    }
    public static void createNaddNonTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType,ExchangeCPM dpCPM ){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billing.setSupplyCost(0);
        billing.setDemandCharges(pirr.getAdvertiser_bid()/1000);
        billing.setInternalDemandCharges(pirr.getInternal_max_bid()/1000);
        billing.setExchangepayout(0);
        billing.setExchangerevenue(pirr.getAdvertiser_bid()/1000);
        billingLogList.add(billing);
    }
    public static void createNaddNonTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType,ExchangeClick dpClick ){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billing.setSupplyCost(0);
        billing.setDemandCharges(pirr.getAdvertiser_bid());
        billing.setInternalDemandCharges(pirr.getInternal_max_bid());
        billing.setExchangepayout(0);
        billing.setExchangerevenue(pirr.getAdvertiser_bid());
        billingLogList.add(billing);
    }
    
    public static void createNaddNonTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType,WinNotification win, MarketPlace mp){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billing.setSupplyCost(win.getEventPayOut());
        billing.setDemandCharges(0);
        billing.setInternalDemandCharges(0);
        billing.setExchangepayout(win.getEventPayOut());
        billing.setExchangerevenue(0);
        billingLogList.add(billing);
        
    }
    
    public static void createNaddNonTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType,InternalExcWinNotification intexcwin ){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billing.setSupplyCost(intexcwin.getEventPayOut());
        billing.setDemandCharges(pirr.getAuction_price()/1000);
        billing.setInternalDemandCharges(pirr.getAuction_price()/1000);
        billing.setNetworkpayout(intexcwin.getEventPayOut());
        billing.setNetworkrevenue(pirr.getAuction_price()/1000);
        billingLogList.add(billing);
    }
    public static void createNaddNonTerminatedBillingObject(List<Billing> billingLogList, PostImpressionRequestResponse pirr, 
            BillingTerminationReason btr, BillingType bType,BEVENT_CSCWIN beventCscWin ,boolean updateDemand){
        Billing billing = new Billing();
        CreateBillingObject.create(pirr, billing);
        billing.setStatus(btr);
        billing.setBillingType(bType);
        billing.setRequestId(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
        billing.setSupplyCost(beventCscWin.getEventPayOut());
        billing.setNetworkrevenue(pirr.getAuction_price()/1000);
        if(updateDemand){
            billing.setDemandCharges(pirr.getAuction_price()/1000);
            billing.setInternalDemandCharges(pirr.getAuction_price()/1000);
            billing.setNetworkpayout(beventCscWin.getEventPayOut());
        }
        billingLogList.add(billing);
    }
    
}
