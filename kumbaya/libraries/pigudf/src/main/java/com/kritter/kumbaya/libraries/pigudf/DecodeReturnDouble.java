package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.PigWarning;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;

public class DecodeReturnDouble extends EvalFunc<Double> {
    int numParams = -1;
    @Override
    public Schema outputSchema(Schema input) {
        try {
            return new Schema(new Schema.FieldSchema(getSchemaName(this
                    .getClass().getName().toLowerCase(), input),
                    DataType.DOUBLE));
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public Double exec(Tuple tuple) throws IOException {
        if (numParams==-1)  // Not initialized
        {
            numParams = tuple.size();
            if (numParams <= 2) {
                String msg = "Decode: Atleast an expression and default string is required.";
                throw new IOException(msg);
            }
            if (tuple.size()%2!=0) {
                String msg = "Decode : Some parameters are unmatched.";
                throw new IOException(msg);
            }
        }
        
        if (tuple.get(0)==null)
            return DataType.toDouble("0");

        try {
            for (int count = 1; count < numParams - 1; count += 2)
            {
                if (tuple.get(count).equals(tuple.get(0)))
                {
                    return DataType.toDouble(tuple.get(count+1).toString());
                }
            }
        } catch (ClassCastException e) {
            warn("Decode : Data type error", PigWarning.UDF_WARNING_1);
            return DataType.toDouble("0");
        } catch (NullPointerException e) {
            String msg = "Decode : Encounter null in the input";
            throw new IOException(msg);
        }

        return DataType.toDouble(tuple.get(tuple.size()-1).toString());
    }

}
