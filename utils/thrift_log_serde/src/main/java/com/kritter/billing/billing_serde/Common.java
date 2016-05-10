package com.kritter.billing.billing_serde;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TBase;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;


public class Common{
    public static String serialize_encode(TBase t) throws Exception{
        TSerializer ts = new TSerializer(new TBinaryProtocol.Factory());
        Base64 encoder = new Base64(0);
        String s = new String(encoder.encode(ts.serialize(t)));
        return s;
    }
}
