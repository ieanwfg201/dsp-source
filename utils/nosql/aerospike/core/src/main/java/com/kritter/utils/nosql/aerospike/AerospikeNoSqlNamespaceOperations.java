package com.kritter.utils.nosql.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.async.AsyncClientPolicy;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.WritePolicy;
import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlData.NoSqlDataType;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class has implementation for aerospike operations required by a namespace
 * for insertion , updation, deletion etc.
 *
 * Class Value has polymorphic types so that different types of values can be
 * used while storing into value for a key or for a bin.
 */
public class AerospikeNoSqlNamespaceOperations implements NoSqlNamespaceOperations
{
    private Logger logger;
    private String host;
    private int port;
    @Getter
    private AerospikeClient aerospikeAsyncClient;
    @Getter
    private WritePolicy writePolicy;
    @Getter
    private Policy readPolicy;
    @Getter
    private BatchPolicy readBatchPolicy;

    public AerospikeNoSqlNamespaceOperations(String loggerName,
                                             String aerospikeHost,
                                             int aerospikePort,
                                             int maxRetries,
                                             int timeout) throws AerospikeException
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.host = aerospikeHost;
        this.port = aerospikePort;
        //in case in future we need to re-define async client policy, for now this will do.
        AsyncClientPolicy asyncClientPolicy = new AsyncClientPolicy();
        this.aerospikeAsyncClient = new AerospikeClient(asyncClientPolicy,host,port);

        /**************************Define default write policy************************************/
        this.writePolicy = asyncClientPolicy.asyncWritePolicyDefault;
        this.writePolicy.sendKey = true;
        /*****************************************************************************************/

        /**************************Define a read policy for records*******************************/
        Policy readPolicyDefined = new Policy();
        readPolicyDefined.maxRetries = maxRetries;
        readPolicyDefined.priority = Priority.DEFAULT;
        readPolicyDefined.sleepBetweenRetries = 0;
        //timeout to 5 milliseconds.
        readPolicyDefined.timeout = timeout;
        /*****************************************************************************************/

        this.readPolicy = readPolicyDefined;
        this.readBatchPolicy = asyncClientPolicy.batchPolicyDefault;
    }

    @Override
    public boolean insertAttributeToThisNamespace(String namespace,
                                                  String tableName,
                                                  NoSqlData primaryKeyValue,
                                                  String attributeName,
                                                  NoSqlData attributeValue) throws AerospikeException
    {
        try
        {
            Value keyValue = fetchAerospikeValueForNoSqlDataType(primaryKeyValue);

            Key key = new Key(namespace, tableName, keyValue);

            Value binValue = fetchAerospikeValueForNoSqlDataType(attributeValue);

            Bin attribute = new Bin(attributeName, binValue);

            //passing WriteListener as null so that we fire and forget and does not wait for the result.
            this.aerospikeAsyncClient.put(this.writePolicy, key, attribute);
        }
        catch (Exception e)
        {
            this.logger.error("Exception inside insertAttributeToThisNamespace of AerospikeNoSqlNamespaceOperations ",e);
            return false;
        }


        //return true if no problems till here
        return true;
    }

    @Override
    public boolean insertMultipleAttributesToThisNamespace(String namespace,
                                                           String tableName,
                                                           NoSqlData primaryKeyValue,
                                                           Map<String, NoSqlData> attributesNameValueMap)
                                                           throws AerospikeException
    {
        if(null == namespace || null == tableName || null == primaryKeyValue ||
           null == primaryKeyValue.getValue() || null == attributesNameValueMap)
            return false;

        try
        {
            Value keyValue = fetchAerospikeValueForNoSqlDataType(primaryKeyValue);

            Key key = new Key(namespace, tableName, keyValue);

            Bin attributes[] = new Bin[attributesNameValueMap.size()];

            int counter = 0;
            for(String entryKey : attributesNameValueMap.keySet())
            {
                Value binValue = fetchAerospikeValueForNoSqlDataType(attributesNameValueMap.get(entryKey));
                Bin attribute = new Bin(entryKey, binValue);
                attributes[counter++] = attribute;
            }

            //passing WriteListener as null so that we fire and forget and does not wait for the result.
            this.aerospikeAsyncClient.put(this.writePolicy, key, attributes);
        }
        catch (Exception e)
        {
            this.logger.error("Exception inside insertMultipleAttributesToThisNamespace of AerospikeNoSqlNamespaceOperations ",e);
            return false;
        }

        //return true if no problems till here
        return true;
    }

    @Override
    public boolean updateAttributesInThisNamespace(String namespace,
                                                   String tableName,
                                                   NoSqlData primaryKeyValue,
                                                   Map<String, NoSqlData> attributesNameUpdatedValueMap)
                                                   throws AerospikeException
    {
        if(null == namespace || null == tableName || null == primaryKeyValue ||
           null == primaryKeyValue.getValue() || null == attributesNameUpdatedValueMap)
            return false;

        //update is same as insert as the bins are again put for this given primary key.
        return insertMultipleAttributesToThisNamespace(namespace,
                                                       tableName,
                                                       primaryKeyValue,
                                                       attributesNameUpdatedValueMap);
    }

    @Override
    public boolean deleteRecordInThisNamespace(String namespace,
                                               String tableName,
                                               NoSqlData primaryKeyValue)
                                               throws AerospikeException
    {

        if(null == namespace || null == tableName || null == primaryKeyValue || null == primaryKeyValue.getValue())
            return false;

        try
        {
            Value value = fetchAerospikeValueForNoSqlDataType(primaryKeyValue);
            Key key = new Key(namespace,tableName,value);
            this.aerospikeAsyncClient.delete(writePolicy,key);
        }
        catch (Exception e)
        {
            this.logger.error("Exception inside deleteRecordInThisNamespace of AerospikeNoSqlNamespaceOperations ",e);
            return false;
        }

        return false;
    }

    @Override
    public Map<String, NoSqlData> fetchSingleRecordAttributes(String namespace,
                                                              String tableName,
                                                              NoSqlData primaryKeyValue,
                                                              final Set<String> attributes)
                                                              throws AerospikeException
    {
        if(
           null == namespace || null == tableName || null == attributes ||
           null == primaryKeyValue || null == primaryKeyValue.getValue()
          )
            return null;


        final Map<String,NoSqlData> mapToReturn = new HashMap<String, NoSqlData>();

        try
        {
            Value value = fetchAerospikeValueForNoSqlDataType(primaryKeyValue);
            Key key = new Key(namespace,tableName,value);

            Record record = this.aerospikeAsyncClient.get(
                                                          readPolicy,
                                                          key,
                                                          attributes.toArray(new String[attributes.size()])
                                                         );

            if(null != record && null != record.bins)
            {
                for(String attributeName : record.bins.keySet())
                {
                    Object binValue = record.bins.get(attributeName);

                    NoSqlData binValueToUse = new NoSqlData(NoSqlDataType.BINARY,
                                                            binValue);

                    mapToReturn.put(attributeName, binValueToUse);
                }
            }
        }
        catch (Exception e)
        {
            this.logger.error("Exception inside fetchSingleRecordAttributes of AerospikeNoSqlNamespaceOperations ",e);
            return null;
        }

        return mapToReturn;
    }

    /**
     * This method takes input a map with key as primary-key value and its value as
     * set of attributes that need to be read in batch from the namespace table.
     * @param namespace
     * @param tableName
     * @param primaryKeyValues
     * @return
     */
    @Override
    public Map<NoSqlData, Map<String, NoSqlData>> fetchMultipleRecordsAttributes(String namespace,
                                                                                 String tableName,
                                                                                 Set<NoSqlData> primaryKeyValues,
                                                                                 final Set<String> attributes)
    {
        if(null == namespace || null == tableName || null == primaryKeyValues)
            return null;

        final Map<NoSqlData,Map<String,NoSqlData>> mapToReturn = new HashMap<NoSqlData, Map<String, NoSqlData>>();

        try
        {
            Key[] primaryKeyArrayForAerospike = new Key[primaryKeyValues.size()];

            int counter = 0;
            for(NoSqlData noSqlData : primaryKeyValues)
            {
                Value value = fetchAerospikeValueForNoSqlDataType(noSqlData);
                Key key = new Key(namespace,tableName,value);
                primaryKeyArrayForAerospike[counter ++ ] = key;
            }

            Record[] records = this.aerospikeAsyncClient.get(
                                                             readBatchPolicy,
                                                             primaryKeyArrayForAerospike,
                                                             attributes.toArray(new String[attributes.size()])
                                                            );

            if(null != records && records.length > 0)
            {
                counter = 0;
                for(Key key : primaryKeyArrayForAerospike)
                {
                    Record record = records[counter ++];

                    Map<String,NoSqlData> attriutesMapForThisRecord = new HashMap<String, NoSqlData>();

                    if(null != record && null != record.bins)
                    {
                        for(String attributeName : record.bins.keySet())
                        {
                            Object binValue = record.bins.get(attributeName);

                            NoSqlData binValueToUse = new NoSqlData(NoSqlDataType.BINARY,
                                    binValue);

                            attriutesMapForThisRecord.put(attributeName, binValueToUse);
                        }
                    }

                    NoSqlData keyAsNoSqlDataValue = new NoSqlData(NoSqlDataType.OBJECT,key.userKey.getObject());
                    mapToReturn.put(keyAsNoSqlDataValue,attriutesMapForThisRecord);
                }
            }
        }
        catch (Exception e)
        {
            this.logger.error("Exception inside fetchMultipleRecordsAttributes of AerospikeNoSqlNamespaceOperations",e);
            return null;
        }

        return mapToReturn;
    }

    private Value fetchAerospikeValueForNoSqlDataType(NoSqlData noSqlValue) throws AerospikeException
    {
        if(null == noSqlValue || null == noSqlValue.getValue())
            throw new AerospikeException("Value for attribute is null, cannot be stored in Aerospike namespace");

        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.STRING.getCode())
        {
            return new Value.StringValue((String)noSqlValue.getValue());
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.INTEGER.getCode())
        {
            return new Value.IntegerValue((Integer)noSqlValue.getValue());
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.LONG.getCode())
        {
            return new Value.LongValue((Long)noSqlValue.getValue());
        }
        //store decimal numbers double and float as string.
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.DOUBLE.getCode())
        {
            return new Value.StringValue(noSqlValue.getValue().toString());
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.FLOAT.getCode())
        {
            return new Value.StringValue(noSqlValue.getValue().toString());
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.BOOLEAN.getCode())
        {
            Boolean incomingValue = (Boolean)noSqlValue.getValue();

            return new Value.IntegerValue(incomingValue.equals(Boolean.FALSE) ? 0 : 1);
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.BINARY.getCode())
        {
            return new Value.BytesValue((byte[]) noSqlValue.getValue());
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.LIST_STRING.getCode())
        {
            List<String> list = (List<String>)noSqlValue.getValue();
            return new Value.ListValue(list);
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.LIST_INTEGER.getCode())
        {
            List<Integer> list = (List<Integer>)noSqlValue.getValue();
            return new Value.ListValue(list);
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.LIST_DOUBLE.getCode())
        {
            List<Double> list = (List<Double>)noSqlValue.getValue();
            return new Value.ListValue(list);
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.LIST_FLOAT.getCode())
        {
            List<Float> list = (List<Float>)noSqlValue.getValue();
            return new Value.ListValue(list);
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.LIST_LONG.getCode())
        {
            List<Long> list = (List<Long>)noSqlValue.getValue();
            return new Value.ListValue(list);
        }
        if(noSqlValue.getNoSqlDataType().getCode() == NoSqlDataType.OBJECT.getCode()) {
            return new Value.BlobValue(noSqlValue.getValue());
        }

        return null;
    }

    public static void main(String[] args) {
        AerospikeNoSqlNamespaceOperations noSqlNamespaceOperations = new AerospikeNoSqlNamespaceOperations("local",
                "localhost", 3000, 10, 1000);

        String namespace = args[0];
        String set = args[1];
        String key = "java_test";
        Bin[] bins = new Bin[1];
        bins[0] = new Bin("identifier", "java");
        System.out.println("Setting values now");
        noSqlNamespaceOperations.aerospikeAsyncClient.put(noSqlNamespaceOperations.writePolicy, new Key(namespace, set,
                key), bins);
    }
}
