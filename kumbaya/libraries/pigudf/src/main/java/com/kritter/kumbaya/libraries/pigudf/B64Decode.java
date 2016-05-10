package com.kritter.kumbaya.libraries.pigudf;
   
import java.io.IOException;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.Tuple;
import org.apache.commons.codec.binary.Base64;
import org.apache.pig.backend.executionengine.ExecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class B64Decode extends EvalFunc<DataByteArray> {
    private static final Logger LOG = LoggerFactory.getLogger(B64Decode.class);
	public DataByteArray exec(Tuple input) throws IOException {
		try {
			String s = input.get(0).toString();
			Base64 decoder = new Base64(0);
			return new DataByteArray(decoder.decodeBase64(s.getBytes()));
		}catch (ExecException e) {
		    LOG.error(e.getMessage());
	        return null;
		}
	}
}
