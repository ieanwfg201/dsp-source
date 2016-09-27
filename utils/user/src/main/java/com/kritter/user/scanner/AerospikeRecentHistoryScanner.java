package com.kritter.user.scanner;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.ScanPolicy;
import com.kritter.user.thrift.struct.RecentImpressionHistory;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

public class AerospikeRecentHistoryScanner {
    public static class RecentHistoryScanCallback implements ScanCallback {
        private String hostName;
        private int port;
        private String namespaceName;
        private String setName;
        private String binName;
        private TDeserializer thriftDeserializer;

        public RecentHistoryScanCallback(String hostName,
                                         int port,
                                         String namespaceName,
                                         String setName,
                                         String binName) {
            this.hostName = hostName;
            this.port = port;
            this.namespaceName = namespaceName;
            this.setName = setName;
            this.binName = binName;
            this.thriftDeserializer = new TDeserializer(new TBinaryProtocol.Factory());
        }

        public void runTest() throws AerospikeException {
            AerospikeClient client = new AerospikeClient(hostName, port);

            try {
                ScanPolicy policy = new ScanPolicy();
                policy.concurrentNodes = true;
                policy.priority = Priority.LOW;
                policy.includeBinData = true;

                client.scanAll(policy, namespaceName, setName, this);
            }
            finally {
                client.close();
            }
        }

        public void scanCallback(Key key, Record record) {
            String keyString = key.userKey.toString();
            if(record.bins.containsKey(binName)) {
                // Deserialize the bin
                byte[] impHistoryBytes = (byte[]) record.bins.get(binName);
                RecentImpressionHistory recentImpressionHistory = new RecentImpressionHistory();
                try {
                    thriftDeserializer.deserialize(recentImpressionHistory, impHistoryBytes);
                } catch (TException te) {
                    System.out.println("Caught exception while deserializing for key" + keyString);
                    return;
                }
                System.out.println("(" + keyString + " : " + recentImpressionHistory.toString() + "), size = " +
                        recentImpressionHistory.getCircularList().size());
            }
        }
    }

    public static void main(String[] args) {
        if(args.length != 5) {
            System.out.println("Usage : java -cp <classpath> com.kritter.user.scanner.AerospikeRecentHistoryScanner " +
                    "<hostname> <port> <namespace name> <set name> <bin name>");
            return;
        }

        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        String namespaceName = args[2];
        String setName = args[3];
        String binName = args[4];

        RecentHistoryScanCallback callback = new RecentHistoryScanCallback(hostName, port, namespaceName, setName,
                binName);
        callback.runTest();
    }
}
