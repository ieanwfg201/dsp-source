package com.kritter.utils.nosql.aerospike;

import com.kritter.utils.nosql.common.NoSqlData;
import com.kritter.utils.nosql.common.NoSqlNamespaceOperations;
import com.kritter.utils.nosql.common.NoSqlNamespaceTable;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used for testing aerospike operations such as write,read,update,etc.
 * using a simple table.
 * For this test class to work the aerospike daemon has to be running on the local
 * machine.
 */
public class TestAerospikeNoSqlNamespaceOperations
{
    private TestTable testTable;
    private AerospikeNoSqlNamespaceOperations aerospikeDatabase;

    public TestAerospikeNoSqlNamespaceOperations()
    {
        try {
            aerospikeDatabase = new AerospikeNoSqlNamespaceOperations("", "127.0.0.1", 3000, 0, 5);
        } catch (Exception e) {
            System.out.println(e);
            aerospikeDatabase = null;
        }
    }

    private void setUpAndTestAerospikeOperations() throws Exception
    {
        testTable = new TestTable(aerospikeDatabase,"dsp_id");
        if(aerospikeDatabase == null || aerospikeDatabase.getAerospikeAsyncClient() == null ||
                !aerospikeDatabase.getAerospikeAsyncClient().isConnected()) {
            return;
        }

        String dspIdValue = "unique_dsp_id_example_value";
        NoSqlData primaryKeyValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING,"unique_exchange_id_example_value");
        NoSqlData attributeValue = new NoSqlData(NoSqlData.NoSqlDataType.STRING,dspIdValue);

        boolean result = testTable.getNoSqlNamespaceOperationsInstance().
                               insertAttributeToThisNamespace(testTable.getNamespaceName(),
                                       testTable.getTableName(),
                                       primaryKeyValue,
                                       testTable.getDspIdAttributeName(),
                                       attributeValue);

        if(!result)
            throw new Exception("Insertion failed in aerospike namespace, check aerospike daemon if running or not...");

        System.out.println("============= Insertion is successful into Aerospike ======================!!!");
        //insertion done, now get and check if value is same and


        Set<String> attributesToRead = new HashSet<String>();
        attributesToRead.add(testTable.getDspIdAttributeName());
        Map<String,NoSqlData> attributesData =
                testTable.getNoSqlNamespaceOperationsInstance().
                                fetchSingleRecordAttributes(testTable.getNamespaceName(),
                                        testTable.getTableName(), primaryKeyValue, attributesToRead);

        if(null == attributesData)
            throw new Exception("Fetch from nosql aerospike database failed!!!");

        System.out.println("VALUE ===== " + attributesData.get(testTable.getDspIdAttributeName()).getValue());

        if(dspIdValue.equals(attributesData.get(testTable.getDspIdAttributeName()).getValue().toString()))
        {
            System.out.println("============ Fetch from nosql Aerospike Successful ====== ");
        }
        else
            throw new Exception("Fetch from nosql aerospike database failed!!!");
    }

    @Test
    public void testAerospikeOperations() throws Exception
    {
        setUpAndTestAerospikeOperations();
    }

    private class TestTable implements NoSqlNamespaceTable
    {
        /*aerospike default namespace that comes with the installation*/
        private static final String namespace = "user";
        private static final String tableName = "test_table";
        @Getter @Setter
        private String exchangeId;
        @Getter @Setter
        private String dspId;
        @Getter
        private String dspIdAttributeName;

        private NoSqlNamespaceOperations noSqlNamespaceOperations;

        public TestTable(NoSqlNamespaceOperations noSqlNamespaceOperations,String dspIdAttributeName)
        {
            this.noSqlNamespaceOperations = noSqlNamespaceOperations;
            this.dspIdAttributeName = dspIdAttributeName;
        }

        @Override
        public String getNamespaceName()
        {
            return namespace;
        }

        @Override
        public String getTableName()
        {
            return tableName;
        }

        @Override
        public String getPrimaryKeyName()
        {
            return null;
        }

        @Override
        public NoSqlData.NoSqlDataType getPrimaryKeyDataType()
        {
            return NoSqlData.NoSqlDataType.STRING;
        }

        @Override
        public NoSqlNamespaceOperations getNoSqlNamespaceOperationsInstance()
        {
            return noSqlNamespaceOperations;
        }
    }
}