package com.kritter.billing.ad_budget.update;

import com.kritter.billing.ad_budget.EntityType;
import com.kritter.billing.ad_budget.utils.GeneralUtils;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import com.kritter.utils.dbconnector.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class UpdateMain {
    private static final Logger logger = LoggerFactory.getLogger(UpdateMain.class);

    public static Connection getConnection(Properties properties) throws Exception {
        String dbType = properties.getProperty("dbtype");
        String dbHost = properties.getProperty("dbhost");
        String dbPort = properties.getProperty("dbport");
        String dbName = properties.getProperty("dbname");
        String dbUser = properties.getProperty("dbuser");
        String dbPassword = properties.getProperty("dbpwd");

        return DBConnector.getConnection(dbType, dbHost, dbPort, dbName, dbUser, dbPassword);
    }

    public static void update(String entityType, String eventType, Properties properties) throws Exception {
        Connection connection = null;
        try {
            connection = getConnection(properties);
            UpdateEventCounts updateEventCounter = new UpdateEventCounts();
            PostImpressionEvent event = null;
            if (eventType.equalsIgnoreCase("impression")) {
                event = PostImpressionEvent.RENDER;
            }

            EntityType entity = null;
            if(entityType.equalsIgnoreCase("campaign")) {
                entity = EntityType.CAMPAIGN;
            } else if(entityType.equalsIgnoreCase("ad")) {
                entity = EntityType.AD;
            }

            String inputDir = properties.getProperty("input_log_dir");
            String inProcessDir = properties.getProperty("in_process_dir");
            String doneDir = properties.getProperty("done_dir");

            updateEventCounter.readLogsAndUpdateEntityBudget(inputDir, inProcessDir, doneDir, connection, event,
                    entity);
        } catch (SQLException sqle) {
            logger.error("Error connecting to db : {}", sqle);
            throw sqle;
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length != 4) {
            throw new Exception("Incorrect Usage\n" +
                    "Correct Usage: com.kritter.billing.ad_budget.update.UpdateMain entitytype eventtype config_file_path " +
                    "logger_config_file_path\n" +
                    "entitytype must be one of the following :\n" +
                    "\tcampaign\n" +
                    "\tad\n" +
                    "eventtype must of one of the following :\n" +
                    "\timpression\n" +
                    "\tclick");
        }

        String entityType = args[0];
        String eventType = args[1];
        Properties properties = GeneralUtils.readProperties(args[2]);
        GeneralUtils.configureLogger(args[3]);

        int sleepInterval = Integer.parseInt(properties.getProperty("sleep_interval_ms"));

        while(true) {
            update(entityType, eventType, properties);

            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
