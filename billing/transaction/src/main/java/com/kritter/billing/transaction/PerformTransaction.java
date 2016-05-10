package com.kritter.billing.transaction;

import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.billing.billing_serde.Common;
import com.kritter.billing.prepare_transaction.perevent.IPrepareTransaction;
import com.kritter.billing.prepare_transaction.perevent.impl.DirectPublisherCPM;
import com.kritter.billing.prepare_transaction.perevent.impl.DirectPublisherClick;
import com.kritter.billing.prepare_transaction.perevent.impl.DirectPublisherConversion;
import com.kritter.billing.prepare_transaction.perevent.impl.ExchangeCPM;
import com.kritter.billing.prepare_transaction.perevent.impl.ExchangeClick;
import com.kritter.billing.prepare_transaction.perevent.impl.WinNotification;
import com.kritter.billing.prepare_transaction.perevent.impl.InternalExcWinNotification;
import com.kritter.billing.prepare_transaction.perevent.impl.BEVENT_CSCWIN;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.postimpression.thrift.struct.Billing;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;

public class PerformTransaction {
	
	private static final Logger LOG = LoggerFactory.getLogger(PerformTransaction.class);
	private static final Logger billingLogger = LoggerFactory.getLogger("billing.thriftlog");

	public static boolean performUpdates(Connection con, List<PostImpressionRequestResponse> inputEventLines) throws Exception{
		int size = 0;
		if(con == null || inputEventLines == null ) {return false;}
		size = inputEventLines.size();
		if(size < 1) {return false;}
		boolean autoCommitMode = con.getAutoCommit();
		con.setAutoCommit(false);
		List<Billing> billingLogList = new LinkedList<Billing>();
		try{
		    Iterator<PostImpressionRequestResponse> itr = inputEventLines.iterator();
		    while(itr.hasNext()){
		        PostImpressionRequestResponse pirr = itr.next();
		        if(pirr != null){
		            LOG.debug(pirr.toString());
		            IPrepareTransaction itransaction = null;
		            switch(pirr.getEvent()){
		            case CLICK:
		                if(pirr.getInventorySource() == INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode() || 
		                pirr.getInventorySource() == INVENTORY_SOURCE.SSP.getCode() ||
		                pirr.getInventorySource() == INVENTORY_SOURCE.AGGREGATOR.getCode()){
		                    itransaction = new DirectPublisherClick();
		                }else if(pirr.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode()){
		                    itransaction = new ExchangeClick();
		                }
		                break;
		            case WIN_NOTIFICATION:
		                if(pirr.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode()){
                            itransaction = new WinNotification();
                        }
		                break;
		            case INT_EXCHANGE_WIN:
                        if(pirr.getInventorySource() == INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode() || 
                                pirr.getInventorySource() == INVENTORY_SOURCE.SSP.getCode() ||
                                pirr.getInventorySource() == INVENTORY_SOURCE.AGGREGATOR.getCode()){
                            itransaction = new InternalExcWinNotification();
                        }
                        break;
                    case BEVENT_CSCWIN:
                        if(pirr.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode()){
                            itransaction = new BEVENT_CSCWIN();
                        }
                        break;
		            case RENDER:
		                if(pirr.getInventorySource() == INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode() || 
		                pirr.getInventorySource() == INVENTORY_SOURCE.SSP.getCode() ||
		                pirr.getInventorySource() == INVENTORY_SOURCE.AGGREGATOR.getCode()){
		                    itransaction = new DirectPublisherCPM();
		                }else if(pirr.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode()){
		                    itransaction = new ExchangeCPM();
                        }
		                break;
		            case CONVERSION:
		                if(pirr.getInventorySource() == INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode() || 
                        pirr.getInventorySource() == INVENTORY_SOURCE.SSP.getCode() ||
                        pirr.getInventorySource() == INVENTORY_SOURCE.AGGREGATOR.getCode()){
                            itransaction = new DirectPublisherConversion();
                        }
                        break;
		            default:
		                break;
		            }
		            if(itransaction != null){
		                itransaction.prepare_campaign_budget(billingLogList, con, pirr);
		                itransaction.prepare_account_budget(billingLogList, con, pirr);
		                itransaction.prepare_payout_network(billingLogList, con, pirr);
		                itransaction.prepare_payout_exchange(billingLogList, con, pirr);
		                itransaction.prepare_updates(billingLogList, con, pirr);
		            }
		        }
		    }
			con.commit();
			Iterator<Billing> billingLogItr = billingLogList.iterator();
			while(billingLogItr.hasNext()){
			    Billing billingLogObject = billingLogItr.next();
			    String encodedStr = Common.serialize_encode(billingLogObject);
			    billingLogger.info(encodedStr);
			}
			return true;
		}catch(Exception e){
		    LOG.error(e.getMessage(),e);
			con.rollback();
			throw new Exception(e);
		}finally{
			con.setAutoCommit(autoCommitMode);
			if (con != null) {
				con.close();
			}
		}
	}
	
}
