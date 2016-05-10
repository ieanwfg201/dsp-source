package com.kritter.kumbaya.libraries.pigudf;
   
import java.io.IOException;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.backend.executionengine.ExecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountTuplesInBag extends EvalFunc<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(CountTuplesInBag.class);
	public Integer exec(Tuple input) throws IOException {
		try {
            if(input == null || input.get(0) == null){
                return 0;
            }
			DataBag s = (DataBag)input.get(0);
			return (int)s.size();
		}catch (ExecException e) {
		    LOG.error(e.getMessage());
	        return 0;
		}
	}
}
