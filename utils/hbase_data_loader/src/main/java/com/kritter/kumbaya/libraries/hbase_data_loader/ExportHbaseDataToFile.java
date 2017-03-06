package com.kritter.kumbaya.libraries.hbase_data_loader;

import com.alibaba.fastjson.JSON;
import com.kritter.utils.nosql.aerospike.AerospikeNoSqlNamespaceOperations;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;
import java.util.*;

/**
 * 从premiummad的hbase查询audience数据同步到optimad的aerospike中。
 * Created by hamlin on 17-2-23.
 */
@SuppressWarnings("Duplicates")
public class ExportHbaseDataToFile {
    private static Properties properties;

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(ExportHbaseDataToFile.class);
    private static final org.apache.logging.log4j.Logger LOG_HBASE_DATA_WRITE_TO_FILE = LogManager.getLogger("hbaseToFileLogger");
    private static final org.apache.logging.log4j.Logger LOG_LASTMODIFY = LogManager.getLogger("lastModifyLogger");

    // 声明静态配置
    private static Configuration conf = null;
    private static AerospikeNoSqlNamespaceOperations aeroUtils = null;
    private static long oldLastModify = 0;
    private static long newLastModify = 0;
    private static String PARTITION = "_";
    private static Map<String, Map<String, List<String>>> asSet = null;
    private static String LASTMODIFY_FILE_PATH;
    private static String TABLE_NAME;
    private static String FIRST_ROWKEY;


    // 保持这个表的连接，程序退出前再释放。
    private static HTable TABLE_ONLY_FIRST;
    private static HTable TABLE;

    static {
        try {
            properties = new Properties();
            properties.load(ExportHbaseDataToFile.class.getClassLoader().getResourceAsStream("data_load.properties"));
        } catch (Exception e) {
            LOG.error("load properties file ERROR:", e);
        }

        FileInputStream fi = null;
        ConfigurationSource source;
        try {
            String pathname = ExportHbaseDataToFile.class.getClassLoader().getResource("log4j2.xml").toString();
            fi = new FileInputStream(new File(pathname));
            source = new ConfigurationSource(fi);
            Configurator.initialize(null, source);
        } catch (Exception e) {
            LOG.error("init log4j ERROR :", e);
        } finally {
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 1,从hbase拿到不重复的deviceId（hbase的ROW），使用firstKeyOnlyFilter.
     * scan 'maddsp_fourfusion_bytarget_data',FILTER=>"FirstKeyOnlyFilter()"
     * 2，遍历deviceId，每次使用当前deviceId，从hbase取出这个deviceId所有的记录。
     * 2.1，遍历每条记录，封装NoSqlData对象，key是deviceId,value是json，json的结构是：List<Map<hbase_family,List<hbase_qualifier>>>。
     * 3，将封装好的这条记录以json格式写入本地文件中。
     * 4，回到2.
     */
    public static void main(String[] args) throws IOException {
        try {
            /**
             * 设置hbase连接信息
             */
            LOG.debug("begin init hbase config");
            buildConfig();
            LOG.debug("read last modify file");
            readLastModifyTime();
            //1,从hbase拿到不重复的deviceId（hbase的ROW），使用firstKeyOnlyFilter.
            LOG.debug("begin get distinct deviceId from hbase");
            ResultScanner resultScanner = getDistinctDeviceId(TABLE_NAME);
            // 用来存放从resultScanner获取的next记录。
            Result next;
            if (resultScanner != null) {
                //2，遍历deviceId，每次使用当前的deviceId，从hbase取出这个deviceId所有的记录。
                while ((next = resultScanner.next()) != null) {
                    // 获取deviceId存入rowKey中
                    String rowKey = Bytes.toString(next.getRow());
//                LOG.debug("rowKey:{}", rowKey);
                    LOG.debug("build aerospike data object.");
                    // 因为一直可以从hbase查询到记录，不知道是不是循环获取了，因此这里保存了第一个获取的rowKey，如果发现非第一次获取的rowKey与保存的第一个rowKey重复，停止循环。
                    if (FIRST_ROWKEY == null || FIRST_ROWKEY.equalsIgnoreCase("")) {
                        FIRST_ROWKEY = rowKey;
                    } else if (FIRST_ROWKEY.equalsIgnoreCase(rowKey)) {
                        break;
                    }
                    // 构建写入aerospike的对象，放到asSet成员对象中。
                    buildAerospikeDataObject(TABLE_NAME, rowKey.trim());
                    // 判断asSet的大小，达到1000条就插入aerospike，并清空asSet。
                    if (asSet.size() >= 1000) {
                        LOG.debug("begin inert into aerospike");
                        // 写入本地文件
                        writeToLocalFile();
                        asSet.clear();
                    }
                }
            } else {
                LOG.warn("get distinct deviceId scanner is null");
            }
            LOG.debug("begin inert into aerospike");
            // 写入本地文件
            writeToLocalFile();
            asSet.clear();
            LOG.debug("write local file done.");
            writeLastModifyToFile();
            closeHbaseConnetion();
        } catch (Exception e) {
            LOG.debug("main ERROR:", e);
        }
    }


    private static void closeHbaseConnetion() {
        if (TABLE_ONLY_FIRST != null) {
            try {
                TABLE_ONLY_FIRST.close();
            } catch (Exception e) {
                TABLE_ONLY_FIRST = null;
            } finally {
                TABLE_ONLY_FIRST = null;
            }
        }
    }


    private static void buildAerospikeDataObject(String tableName, String rowKey) {
        /**
         * 根据rowkey查询所有记录，遍历每条记录，封装NoSqlData对象，可以是deviceId,value是json，json的结构是：List<Map<hbase_family,List<hbase_qualifier>>>。
         */
        try {
            if (TABLE == null)
                TABLE = new HTable(conf, tableName);
            Result result = TABLE.get(new Get(Bytes.toBytes(rowKey)));
            for (KeyValue kv : result.list()) {
                String qualifier = Bytes.toString(kv.getQualifier());
                String[] split = qualifier.split(PARTITION);
                /**
                 * 这里将aerospike中的整体结构封装成一个稍有些复杂的map，结构是Map_1<deviceId, List_1<Map_2<sourceType, List_2<tagId>>>>
                 * 其中，最外层的map_1表示aerospike的set，deviceId作为key，
                 * list_1表示aerospike一条记录中的bins，
                 * map_2的key表示source的type，
                 * list_2表示tagId数组
                 */
                if (asSet == null)
                    asSet = new HashMap<>();
                Map<String, List<String>> sourceMap = asSet.get(rowKey);
//                List<Map<String, List<String>>> binsList = (List<Map<String, List<String>>>) listMap;
                if (sourceMap == null) {
                    sourceMap = new HashMap<>();
                    asSet.put(rowKey, sourceMap);
                }
                if (split.length > 1) {
                    if (sourceMap.size() == 0) {
                        List<String> tagList = new ArrayList<>();
                        tagList.add(split[1]);
                        sourceMap.put(split[0], tagList);
                    } else {
                        for (String source : sourceMap.keySet()) {
                            List<String> tagList = sourceMap.get(source);
                            if (null == tagList) {
                                List<String> list = new ArrayList<>();
                                list.add(split[1]);
                                sourceMap.put(split[0], list);
                            } else {
                                if (!tagList.contains(split[1]))
                                    tagList.add(split[1]);
                            }
                        }
                    }
                    if (newLastModify < kv.getTimestamp())
                        newLastModify = kv.getTimestamp();
                    LOG.debug("rowKey:{},have {} the source. LastModify ： {}", rowKey != null ? sourceMap.get(rowKey).size() : 0, newLastModify);
                }
            }
        } catch (Exception e) {
            LOG.error("build aerospike data object ERROR : ", e);
        }
    }

    private static void buildConfig() {
        conf = HBaseConfiguration.create();
        if (properties == null)
            System.out.println("proterties file is null");
        conf.set("hbase.zookeeper.property.clientPort", properties.getProperty("hbase_zookeeper_property_clientport"));
        conf.set("hbase.zookeeper.quorum", properties.getProperty("hbase_zookeeper_quorum"));
        conf.set("zookeeper.znode.parent", properties.getProperty("zookeeper_znode_parent"));
        conf.set("hbase.rpc.timeout", properties.getProperty("hbase_rpc_timeout"));
        conf.set("ipc.socket.timeout", properties.getProperty("ipc_socket_timeout"));
        conf.set("hbase.client.operation.timeout", properties.getProperty("hbase_client_operation_timeout"));
        conf.set("hbase.client.pause", properties.getProperty("hbase_client_pause"));
        conf.set("hbase.client.retries.number", properties.getProperty("hbase_client_retries_number"));
        conf.set("zookeeper.recovery.retry", properties.getProperty("zookeeper_recovery_retry"));
        conf.set("zookeeper.session.timeout", properties.getProperty("zookeeper_session_timeout"));

        LASTMODIFY_FILE_PATH = properties.getProperty("lastmodify_file_path");
        TABLE_NAME = properties.getProperty("table_name");
    }

    public static ResultScanner getDistinctDeviceId(String tableName) {
        try {
            if (TABLE_ONLY_FIRST == null)
                TABLE_ONLY_FIRST = new HTable(conf, tableName);
            Scan scan = new Scan();
            scan.setBatch(1000);
            scan.setFilter(new FirstKeyOnlyFilter());
            // 每次从scanner中获取一个Result对象，都是做了一次查询
            ResultScanner scanner = TABLE_ONLY_FIRST.getScanner(scan);
            return scanner;
        } catch (IOException e) {
            LOG.error("get distinct deviceId ERROR : ", e);
        }
        return null;
    }

    private static void writeToLocalFile() {
        for (String rowKey : asSet.keySet()) {
            HashMap<String, Map<String, List<String>>> tempMap = new HashMap<>();
            tempMap.put(rowKey, asSet.get(rowKey));
            LOG_HBASE_DATA_WRITE_TO_FILE.info(JSON.toJSONString(tempMap));
        }
    }

    private static void readLastModifyTime() {
        LOG.debug("begin read last modify file.");
        File file = new File(LASTMODIFY_FILE_PATH);
        if (!file.exists()) {
            try {
                LOG.debug("last modify file not exists,create file.");
                file.createNewFile();
            } catch (IOException e) {
                LOG.error("cant create new file.", e);
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                oldLastModify = Long.parseLong(line);
                LOG.debug("old last modify is " + oldLastModify);
            }
            if (oldLastModify <= 100) {
                oldLastModify = 0;
                newLastModify = oldLastModify;
            }

        } catch (Exception e) {
            LOG.error("read last modify file ERROR:", e);
        }
        LOG.debug("end read last modify file.");
    }

    private static void writeLastModifyToFile() {
        LOG.debug("begin write last modify file.");
        LOG_LASTMODIFY.info(newLastModify);
        LOG.debug("end write last modify file.");
    }


}
