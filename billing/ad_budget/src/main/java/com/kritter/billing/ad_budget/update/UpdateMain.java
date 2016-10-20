package com.kritter.billing.ad_budget.update;

import com.kritter.billing.ad_budget.EntityType;
import com.kritter.billing.ad_budget.utils.GeneralUtils;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import com.kritter.utils.dbconnector.DBConnector;
import org.apache.log4j.Appender;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.or.ObjectRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

public class UpdateMain {
    private String loggerName;
    private final Logger logger;

    public UpdateMain(String loggerName) {
        this.loggerName = loggerName;
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    public static Connection getConnection(Properties properties) throws Exception {
        String dbType = properties.getProperty("dbtype");
        String dbHost = properties.getProperty("dbhost");
        String dbPort = properties.getProperty("dbport");
        String dbName = properties.getProperty("dbname");
        String dbUser = properties.getProperty("dbuser");
        String dbPassword = properties.getProperty("dbpwd");

        return DBConnector.getConnection(dbType, dbHost, dbPort, dbName, dbUser, dbPassword);
    }

    public void update(String entityType, String eventType, Properties properties) throws Exception {
        Connection connection = null;
        try {
            connection = getConnection(properties);
            UpdateEventCounts updateEventCounter = new UpdateEventCounts(loggerName);
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
        String configFile = args[2];
        String log4jConfigFile = args[3];

        System.out.println("Entity type : " + entityType);
        System.out.println("Event type : " + eventType);
        System.out.println("Config file : " + configFile);
        System.out.println("Log4j config file : " + log4jConfigFile);

        String loggerName = "billing.adbudget";
        Properties properties = GeneralUtils.readProperties(configFile);
        PropertyConfigurator.configure(log4jConfigFile);

        int sleepInterval = Integer.parseInt(properties.getProperty("sleep_interval_ms"));

        UpdateMain updateMain = new UpdateMain(loggerName);
        while(true) {
            updateMain.update(entityType, eventType, properties);

            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
