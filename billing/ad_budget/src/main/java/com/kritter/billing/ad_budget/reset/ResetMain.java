package com.kritter.billing.ad_budget.reset;

import com.kritter.billing.ad_budget.EntityType;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import com.kritter.utils.dbconnector.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ResetMain {
    private static final Logger logger = LoggerFactory.getLogger(ResetMain.class);

    public static void main(String[] args) throws Exception {
        if(args.length != 8) {
            throw new Exception("Incorrect Usage\n" +
                    "Correct Usage: com.kritter.billing.ad_budget.impression_cap.reset.BudgetReset entitytype eventtype " +
                    "dbtype dbhost dbport dbname dbuser dbpwd\n" +
                    "entitytype must be one of the following :\n" +
                    "\tcampaign\n" +
                    "\tad\n" +
                    "eventtype must of one of the following :\n" +
                    "\timpression\n" +
                    "\tclick");
        }

        String entityType = args[0];
        String eventType = args[1];
        String dbType = args[2];
        String dbHost = args[3];
        String dbPort = args[4];
        String dbName = args[5];
        String dbUser = args[6];
        String dbPassword = args[7];

        Connection connection = null;
        try {
            connection = DBConnector.getConnection(dbType, dbHost, dbPort, dbName, dbUser, dbPassword);
            ResetEvents resetEvents = new ResetEvents();

            EntityType entity = null;
            if(entityType.equalsIgnoreCase("campaign")) {
                entity = EntityType.CAMPAIGN;
            } else if(entityType.equalsIgnoreCase("ad")) {
                entity = EntityType.AD;
            }

            PostImpressionEvent event = null;
            if(eventType.equalsIgnoreCase("impression")) {
                event = PostImpressionEvent.RENDER;
            }
            resetEvents.readEntityBudgetsAndReset(connection, event, entity);
        } catch (SQLException sqle) {
            logger.error("Error connecting to db : {}", sqle);
            throw sqle;
        } finally {
            if(connection != null)
                connection.close();
        }
    }
}
