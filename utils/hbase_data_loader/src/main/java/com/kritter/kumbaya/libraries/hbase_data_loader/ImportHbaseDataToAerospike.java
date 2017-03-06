package com.kritter.kumbaya.libraries.hbase_data_loader;

import com.alibaba.fastjson.JSON;
import com.kritter.utils.nosql.aerospike.AerospikeNoSqlNamespaceOperations;
import com.kritter.utils.nosql.common.NoSqlData;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by hamlin on 17-3-2.
 */
@SuppressWarnings("Duplicates")
public class ImportHbaseDataToAerospike {
    private ImportHbaseDataToAerospike() {
    }

    private static Properties properties;

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(ExportHbaseDataToFile.class);
    // 最后修改  改成 最后处理  存储最后处理的文件名

    // 声明静态配置
    private static AerospikeNoSqlNamespaceOperations aeroUtils = null;
    private static Map<String, Map<String, List<String>>> asSet = null;
    private static String LOAD_FILE_PATH;
    private static String AEROSPIKE_NAMESPACE;
    private static String AEROSPIKE_SET_NAME;
    private static String AEROSPIKE_ATTRIBUTE_NAME;
    private static String AEROSPIKE_HOST;
    private static int AEROSPIKE_PORT;
    private static int AEROSPIKE_MAX_RETRIES;
    private static int AEROSPIKE_TIMEOUT;
    private static int AEROSPIKE_MAX_CONNS_PER_NODE;

    private static int AEROSPIKE_WRITE_BATCH;
    private static int AEROSPIKE_WRITE_BATCH_DELAY;

    static {
        ConfigurationSource source;
        URL resource = ExportHbaseDataToFile.class.getClassLoader().getResource("");
        String pathname = resource.toString().substring(5) + "log4j2.xml";
        File file = new File(pathname);
        try (FileInputStream fi = new FileInputStream(file)) {
            source = new ConfigurationSource(fi);
            Configurator.initialize(null, source);
        } catch (Exception e) {
            LOG.error("init log4j ERROR :", e);
        }
    }


    public static void main(String[] args) {
        loadPropertiesFile();
        buildConfig();
        initAerospike();
        File[] files = getFiles();
        for (File file : files) {
            readDataFromFile(file);
        }
    }

    private static void initAerospike() {
        aeroUtils = new AerospikeNoSqlNamespaceOperations(null, AEROSPIKE_HOST, AEROSPIKE_PORT, AEROSPIKE_MAX_RETRIES, AEROSPIKE_TIMEOUT, AEROSPIKE_MAX_CONNS_PER_NODE, 20000);
    }

    private static void readDataFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int number = 0;
            while (!StringUtils.isEmpty((line = reader.readLine()))) {
                asSet = JSON.parseObject(line, new com.alibaba.fastjson.TypeReference<Map<String, Map<String, List<String>>>>() {
                });
                insertIntoAerospike();
                asSet.clear();
                asSet = null;
                if (++number > 1000) {
                    number = 0;
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            LOG.debug("open file ERROR:", e);
        }
    }

    private static File[] getFiles() {
        File[] files;
        File file_path = new File(LOAD_FILE_PATH);
        if (file_path.isDirectory()) {
            files = file_path.listFiles();
        } else {
            files = new File[1];
            files[0] = file_path;
        }
        return files;
    }


    protected static void insertIntoAerospike() {
        boolean success;
        LOG.debug("asSet size : ", asSet.size());
        for (String rowKey : asSet.keySet()) {
            String value = JSON.toJSONString(asSet.get(rowKey));
            NoSqlData noSqlData = new NoSqlData(NoSqlData.NoSqlDataType.STRING, value);
            NoSqlData hbase_row = new NoSqlData(NoSqlData.NoSqlDataType.STRING, rowKey);
            LOG.debug("inert into aerospike data : key:{} , bins:{}", rowKey, value);
            success = aeroUtils.insertAttributeToThisNamespace(AEROSPIKE_NAMESPACE, AEROSPIKE_SET_NAME, hbase_row, AEROSPIKE_ATTRIBUTE_NAME, noSqlData);
            if (!success) {
                LOG.error("failed insert into aerospike:key={},bins={}", rowKey, value);
            }
        }
    }

    private static void buildConfig() {
        LOAD_FILE_PATH = properties.getProperty("load_file_path");
        AEROSPIKE_NAMESPACE = properties.getProperty("aerospike_namespace");
        AEROSPIKE_SET_NAME = properties.getProperty("aerospike_set_name");
        AEROSPIKE_ATTRIBUTE_NAME = properties.getProperty("aerospike_attribute_name");
        AEROSPIKE_HOST = properties.getProperty("aerospike_host");
        AEROSPIKE_PORT = Integer.parseInt(properties.getProperty("aerospike_port"));
        AEROSPIKE_MAX_RETRIES = Integer.parseInt(properties.getProperty("aerospike_max_retries"));
        AEROSPIKE_TIMEOUT = Integer.parseInt(properties.getProperty("aerospike_timeout"));
        AEROSPIKE_MAX_CONNS_PER_NODE = Integer.parseInt(properties.getProperty("aerospike_max_conns_per_node"));
        AEROSPIKE_WRITE_BATCH = Integer.parseInt(properties.getProperty("aerospike_write_batch"));
        AEROSPIKE_WRITE_BATCH_DELAY = Integer.parseInt(properties.getProperty("aerospike_write_batch_delay_ms"));
    }

    private static void loadPropertiesFile() {
        URL resource = ImportHbaseDataToAerospike.class.getClassLoader().getResource("");
        File file = new File(resource.toString().substring(5) + "data_load.properties");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (Exception e) {
            LOG.error("load properties file ERROR:", e);
        }
    }
}
