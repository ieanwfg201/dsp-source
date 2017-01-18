package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractFromBagOfTupleExchange extends EvalFunc<DataBag> {
    private static final Logger LOG = LoggerFactory.getLogger(ExtractFromBagOfTupleExchange.class);
    TupleFactory mTupleFactory = TupleFactory.getInstance();
    BagFactory mBagFactory = BagFactory.getInstance();
    
    private Tuple createDefault(Tuple input) throws ExecException{
        Tuple defaultTuple = mTupleFactory.newTuple();
        int size = input.size();
        if(size > 2){
            for(int i=2; i<size; i++){
                defaultTuple.append(input.get(i));
            }
        }
        return defaultTuple;
    }

    public DataBag exec(Tuple input) throws IOException {
            DataBag output_bag = mBagFactory.newDefaultBag();
            if (input == null ){
                return output_bag;
            }
            try {
            		Double winprice =0.0;
            		Object wp = input.get(0);
            		if(wp !=null){
            			try{
            				winprice = Double.parseDouble(wp.toString());
            			}catch(Exception e){
            			}
            		}
                    Object values = input.get(1);
                    if (!(values instanceof DataBag)) {
                            output_bag.add(createDefault(input));
                            return output_bag;
                    } else {
                            DataBag databag = (DataBag) values;
                            if( databag.size() < 1){
                                output_bag.add(createDefault(input));
                                return output_bag;
                            }else{
                                boolean isFirst = true;
                                int i=0;
                                for(Tuple t : databag){
                                	boolean winner =false;
                                	if(t.size()>4){
                                		Object o =t.get(4);
                                		if(o != null){
                                			if("FILL".equals(o.toString())){
                                				winner=false;
                                			}
                                		}
                                	}
                                    if(isFirst){
                                        t.append(1);
                                        t.append(1);
                                    }else{
                                    	t.append(0);
                                        t.append(1);    
                                    }
                                    if(winner){
                                    	t.append(winprice);
                                    }else{
                                    	t.append(0.0);
                                    }
                                    isFirst = false;
                                    
                                }
                                return databag;
                            }
                    }
            } catch (Exception e) {
                    LOG.error(e.getMessage());
                    return output_bag;
            }
    }
}

