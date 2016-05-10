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

public class ExtractFromBagOfTupleAdserving extends EvalFunc<DataBag> {
    private static final Logger LOG = LoggerFactory.getLogger(ExtractFromBagOfTupleAdserving.class);
    TupleFactory mTupleFactory = TupleFactory.getInstance();
    BagFactory mBagFactory = BagFactory.getInstance();
    
    private Tuple createDefault(Tuple input) throws ExecException{
        Tuple defaultTuple = mTupleFactory.newTuple();
        int size = input.size();
        if(size > 1){
            for(int i=1; i<size; i++){
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
                    Object values = input.get(0);
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
                                int tsize=0;
                                for(Tuple t : databag){
                                    tsize = t.size();
                                    if(isFirst){
                                        if(tsize == 8){
                                            t.append(-1);
                                            t.append(1);
                                        }else{
                                            t.append(1);
                                        }
                                        t.append(1);
                                    }else{
                                        if(tsize == 8){
                                            t.append(-1);
                                            t.append(0);
                                        }else{
                                            t.append(0);
                                        }

                                        t.append(1);    
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

