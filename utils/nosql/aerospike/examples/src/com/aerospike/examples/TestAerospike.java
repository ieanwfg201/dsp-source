package com.aerospike.examples;


import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.async.AsyncClient;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;

/**
 * This class has various functions to test interaction with aerospike local
 * machine setup.
 */
public class TestAerospike
{
    /*As implemented by the aerospike api*/
    private AerospikeClient aerospikeClient;

    /*database name known as namespace*/
    private String namespace;

    /*table name known as set*/
    private String setName;

    public TestAerospike()
    {
        //default client policy as defined in api,change if required.See class for details.
        ClientPolicy clientPolicy = new ClientPolicy();

        //this namespace is already available.
        this.namespace = "test";
        this.setName = "test_table";

        this.aerospikeClient = new AerospikeClient(clientPolicy,"127.0.0.1",3000);
    }

    public static void main(String s[])
    {
        TestAerospike testAerospike = new TestAerospike();

        Key key = new Key(testAerospike.namespace,testAerospike.setName,"test");

        WritePolicy writePolicy = new WritePolicy();

        Bin bin = new Bin("bin_name","1");
        Bin bin2 = new Bin("bin_name_2","2");
        Bin bin3= new Bin("bin_name","3");

        testAerospike.aerospikeClient.put(writePolicy,key,bin);
        testAerospike.aerospikeClient.put(writePolicy,key,bin2);
        testAerospike.aerospikeClient.put(writePolicy,key,bin3);

        Policy policy = new ClientPolicy().readPolicyDefault;

        String bins[] =new String[2];
        bins[0] = "bin_name";
        bins[1] = "bin_name_2";

        Record record = testAerospike.aerospikeClient.get(policy,key,bins);

        System.out.println(record.getValue("bin_name"));
        System.out.println(record.getValue("bin_name_2"));

        //use AsyncClient for asynchronous operations, method put for async put,pass listener as null to fire and forget

    }
}
