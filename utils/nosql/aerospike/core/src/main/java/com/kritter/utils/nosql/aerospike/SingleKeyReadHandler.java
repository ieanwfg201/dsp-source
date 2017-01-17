package com.kritter.utils.nosql.aerospike;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.listener.RecordListener;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.SignalingNotificationObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

public class SingleKeyReadHandler implements RecordListener {
    private SignalingNotificationObject<Map<String, NoSqlData>> resultObject;
    private Logger logger;

    public SingleKeyReadHandler(SignalingNotificationObject<Map<String, NoSqlData>> resultObject,
                                Logger logger) {
        this.resultObject = resultObject;
        this.logger = logger;
    }

    @Override
    public void onSuccess(Key key, Record record) {
        //logger.debug("Fetched result for key : {}", key.toString());
        Map<String, NoSqlData> resultMap = new HashMap<String, NoSqlData>();
        if (null != record && null != record.bins) {
            for (String attributeName : record.bins.keySet()) {
                logger.debug("Fetched bin : {}.", attributeName);
                Object binValue = record.bins.get(attributeName);

                NoSqlData binValueToUse = new NoSqlData(NoSqlData.NoSqlDataType.BINARY, binValue);

                resultMap.put(attributeName, binValueToUse);
            }
        } else {
            logger.debug("No bins found for the key.");
        }

        this.resultObject.put(resultMap);
    }

    @Override
    public void onFailure(AerospikeException exception) {
        this.resultObject.put(null);

        if(exception instanceof AerospikeException.Timeout) {
            long timeoutCount = AerospikeNoSqlNamespaceOperations.timeouts.incrementAndGet();

            if(timeoutCount < 1000 || timeoutCount % 1000 == 0) {
                logger.error("Caught timeout exception for the {}'th time. Exception : {}.", timeoutCount, exception);
            }
        } else {
            logger.error("Caught aerospike exception : ", exception);
        }
    }
}
