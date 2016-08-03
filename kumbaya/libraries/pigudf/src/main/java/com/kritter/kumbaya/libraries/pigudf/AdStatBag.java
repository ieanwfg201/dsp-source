package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;
import java.util.Map;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AdStatBag extends EvalFunc<DataBag> {
	private static final Logger LOG = LoggerFactory.getLogger(AdStatBag.class);
	private static final TupleFactory tupleFactory_ = TupleFactory.getInstance();
	private static final BagFactory mBagFactory = BagFactory.getInstance();
	protected void processAndaddToBag(Map<String, Map<String, Object>> nfrMap, Map<String, Long> timeMap, DataBag output) 
			throws ExecException{
        
		if(nfrMap != null && timeMap != null){
			for(String tStr:nfrMap.keySet()){
				long totalCount = timeMap.get(tStr);
				Map<String, Object> adMap = nfrMap.get(tStr);
				for(String adid:adMap.keySet()){
					try{
					long adCount=0;
					Tuple tupleStr = (Tuple)adMap.get(adid);
					Map<String,Long> countMap = (Map<String,Long> ) tupleStr.get(2);
					for(String countStrKey:countMap.keySet()){
						adCount = adCount+countMap.get(countStrKey);
						Tuple t = tupleFactory_.newTuple();
						t.append(tStr);
						t.append(adid);
						t.append(countStrKey);
						t.append(countMap.get(countStrKey));
						output.add(t);
					}
					if(totalCount-adCount>0){
						Tuple t = tupleFactory_.newTuple();
						t.append(tStr);
						t.append(adid);
						t.append("NO_ADS_COUNTRY_CARRIER_OS_BRAND");
						t.append(totalCount-adCount);
						output.add(t);
					}
					}catch(Exception e){
						
					}
				}
			}
		}
	}

	public DataBag exec(Tuple inputIN) throws IOException {
		DataBag output = mBagFactory.newDefaultBag();
		if(inputIN != null){
			if(inputIN == null || inputIN.size() != 2){
				return null;
			}
			Map<String, Map<String, Object>> nfrMap = (Map<String, Map<String, Object>>) inputIN.get(0);
			Map<String, Long> timeMap = (Map<String, Long>) inputIN.get(1);
			processAndaddToBag(nfrMap, timeMap, output);
		}else{
		}

		return output;

	}
}
