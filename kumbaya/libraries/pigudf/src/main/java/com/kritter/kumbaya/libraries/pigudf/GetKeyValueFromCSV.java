package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetKeyValueFromCSV extends EvalFunc<Tuple> {
    private static final Logger LOG = LoggerFactory.getLogger(GetKeyValueFromCSV.class);
    TupleFactory mTupleFactory = TupleFactory.getInstance();
    
    public Tuple exec(Tuple input) throws IOException {
            Tuple tuple = mTupleFactory.newTuple();
            if (input == null || input.size() < 2 || input.get(0) == null || input.get(1)==null){
                tuple.append("");
                tuple.append(0.0);
                return tuple;
            }
            try {
                    int size = input.size();
                    if(size == 1 ){
                        tuple.append(input.get(0));
                        tuple.append(0.0);
                        return tuple;
                    }
                    if(size ==  7){
                        if(input.get(2) == null || input.get(3)==null || input.get(4)==null || input.get(5)==null || input.get(6)==null){
                            tuple.append(input.get(0));
                            tuple.append(Double.parseDouble(input.get(1).toString()));
                            return tuple;
                        }
                        String delimiter = input.get(2).toString();
                        String str = input.get(3).toString();
                        String keyIndexStr = input.get(4).toString();
                        int valueIndex = Integer.parseInt(input.get(5).toString());
                        String keyJoinDelimiter = input.get(6).toString();
                        String strSplit[] = str.split(delimiter);
                        int strSize = strSplit.length;
                        StringBuffer strBuffer=new StringBuffer("");
                        boolean first = true;
                        if(valueIndex < strSize){
                            String keyIndexSplit[] = keyIndexStr.split(",");
                            int keyIndexInt = -1;
                            for(String keyIndex:keyIndexSplit){
                                keyIndexInt = Integer.parseInt(keyIndex);
                                if(keyIndexInt >= strSize){
                                    tuple.append(input.get(0));
                                    tuple.append(Double.parseDouble(input.get(1).toString()));
                                    return tuple;
                                }
                                if(first){
                                    first = false;
                                }else{
                                    strBuffer.append(keyJoinDelimiter);
                                }
                                strBuffer.append(strSplit[keyIndexInt]);
                            }
                            if(!first){ 
                                tuple.append(strBuffer.toString());
                                tuple.append(Double.parseDouble(strSplit[valueIndex]));
                                return tuple;
                            }
                        }
                    }
                    tuple.append(input.get(0));
                    tuple.append(Double.parseDouble(input.get(1).toString()));
                    return tuple;
            } catch (Exception e) {
                    LOG.error(e.getMessage());
                    tuple.append("");
                    tuple.append(0.0);
                    return tuple;
            }
    }
}

