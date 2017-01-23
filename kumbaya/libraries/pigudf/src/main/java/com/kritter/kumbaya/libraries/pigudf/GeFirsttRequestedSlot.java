package com.kritter.kumbaya.libraries.pigudf;
   
import java.io.IOException;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.backend.executionengine.ExecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeFirsttRequestedSlot extends EvalFunc<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(GeFirsttRequestedSlot.class);
	public Integer exec(Tuple input) throws IOException {
		try {
            if(input == null || input.get(0) == null){
                return -1;
            }
			DataBag s = (DataBag)input.get(0);
			if(s.size()>0){
				for(Tuple t : s){
					if(t != null && t.size()>0){
						try{
							Integer i = Integer.parseInt(t.get(0).toString());
							return i;
						}catch(Exception e){
							
						}
					}
					return -1;
				}
			}
			return -1;
		}catch (ExecException e) {
		    LOG.error(e.getMessage());
	        return -1;
		}
	}
}
