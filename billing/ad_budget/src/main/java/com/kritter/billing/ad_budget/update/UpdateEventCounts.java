package com.kritter.billing.ad_budget.update;

import com.kritter.billing.ad_budget.EntityType;
import com.kritter.billing.ad_budget.event_counter.AdImpressionCounter;
import com.kritter.billing.ad_budget.event_counter.CampaignImpressionCounter;
import com.kritter.billing.ad_budget.event_counter.EventCounter;
import com.kritter.billing.ad_budget.input.InputReader;
import com.kritter.billing.loglineconverter.postimpression.PostImpressionConverter;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UpdateEventCounts {
    private final String loggerName;
    private final Logger logger;
    private static int BATCH_SIZE = 1000;

    public static final String UPDATE_CAMPAIGN_IMPRESSION_COUNT= "INSERT INTO campaign_impressions_budget " +
            "(campaign_guid, impressions_accrued, last_modified, modified_by) VALUES ((select guid from campaign " +
            "where id = ?), ?, now(), 1) ON DUPLICATE KEY UPDATE impressions_accrued = impressions_accrued + ?, " +
            "last_modified = now()";

    public static final String UPDATE_AD_IMPRESSION_COUNT= "INSERT INTO ad_budget (ad_guid, impressions_accrued, " +
            "last_modified, modified_by) VALUES ((select guid from ad where id = ?), ?, now(), 1) ON DUPLICATE KEY " +
            "UPDATE impressions_accrued = impressions_accrued + ?, last_modified = now()";

    public UpdateEventCounts(String loggerName) {
        this.loggerName = loggerName;
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    /***
     * Updates ad budget given db connection, map containing ad id to event count mapping and query to run on the
     * database. The query must have 3 wildcards, first wild card corresponding to event count, second to last_modified
     * which is a time stamp and third to ad id
     * @param connection Database connection
     * @param entityIdEventCountMap map containing ad inc id to event counts
     * @param query query to run on the database. Must have wild cards, first wild card corresponding to event count,
     *              second to last_modified which should be timestamp and third to ad inc id
     */
    public void updateEntityBudget(Connection connection, Map<Integer, Integer> entityIdEventCountMap, String query) {
        if(entityIdEventCountMap == null)
            return;

        DateTime dateTime = new DateTime();
        Timestamp timestamp = new Timestamp(dateTime.getMillis());

        boolean autoCommit = false;
        PreparedStatement preparedStatement = null;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(query);

            int batchCount = 0;
            for(Map.Entry<Integer, Integer> entityEventCount : entityIdEventCountMap.entrySet()) {
                int entityId = entityEventCount.getKey();
                int count = entityEventCount.getValue();
                preparedStatement.setInt(1, entityId);
                preparedStatement.setInt(2, count);
                preparedStatement.setInt(3, count);
                preparedStatement.addBatch();
                ++batchCount;

                if(batchCount % BATCH_SIZE == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    logger.debug("Committed {}'th batch of queries.", batchCount/BATCH_SIZE);
                }
            }

            preparedStatement.executeBatch();
            connection.commit();
            logger.debug("Committed last batch of queries.");
        } catch (SQLException sqle) {
            logger.error("Error in update of entity budgets : {}", sqle);
            throw new RuntimeException(sqle);
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                connection.setAutoCommit(autoCommit);
            } catch (SQLException sqle) {
                logger.error("Error trying to close resources : {}", sqle);
            }
        }
    }

    /***
     * Function to plumb all the individual pieces together. Reads the logs, aggregates the events for each ad and then
     * updates the database with event counts
     * @param inputLogDir Path to directory containing input log files
     * @param inProcessDir Path to directory holding the files while they're being processed
     * @param doneDir Path to directory to which the files are moved once they're processed
     * @param connection Database connection
     * @param eventType Type of events to aggregate. Can be render, click, conversion, etc.
     */
    public void readLogsAndUpdateEntityBudget(String inputLogDir, String inProcessDir, String doneDir,
                                          Connection connection, PostImpressionEvent eventType, EntityType entityType
                                          ) {
        if(entityType == null) {
            throw new RuntimeException("No entity type specified in update!");
        }

        if(eventType == null) {
            throw new RuntimeException("No event type specified in update!");
        }

        // Move all files from input log directory to in process directory
        try {
            InputReader.moveFilesFromSourceToDestDirectory(inputLogDir, inProcessDir);
        } catch (IOException ioe) {
            logger.error("Error moving files from input log directory : {}, to in process directory : {}. Error : {}",
                    inputLogDir, inProcessDir, ioe);
            throw new RuntimeException(ioe);
        }

        // Read logs
        InputReader reader = new InputReader(this.loggerName, new PostImpressionConverter());
        // reader.readLogsFromDir(inProcessDir, postImpressionRequestResponses);

        File directory = new File(inProcessDir);

        File[] fileList = directory.listFiles();
        if(fileList == null || fileList.length == 0) {
            logger.debug("No files in the directory: ", inProcessDir);
            return;
        }

        EventCounter eventCounter = null;
        switch (entityType) {
            case CAMPAIGN:
                eventCounter = new CampaignImpressionCounter(loggerName);
                break;
            case AD:
                eventCounter = new AdImpressionCounter(loggerName);
                break;
            default:
                throw new RuntimeException("Unsupported entity type : " + entityType.toString());
        }

        for (File file : fileList) {
            logger.debug("Processing file : {}", file.getAbsolutePath());
            List<PostImpressionRequestResponse> postImpressionRequestResponses = new LinkedList<>();
            reader.readLogFile(file, postImpressionRequestResponses);

            // Get event counts per ad id
            Map<Integer, Integer> entityIdEventCountMap = eventCounter.countEvents(postImpressionRequestResponses, eventType);
            logger.debug("Entity count map has {} entries.", entityIdEventCountMap.size());

            // Update in database
            String query = null;
            switch (entityType) {
                case AD:
                    switch (eventType) {
                        case RENDER:
                            query = UPDATE_AD_IMPRESSION_COUNT;
                            break;
                        default:
                            throw new RuntimeException("Unsupported event type : " + eventType.toString());
                    }
                    break;
                case CAMPAIGN:
                    switch (eventType) {
                        case RENDER:
                            query = UPDATE_CAMPAIGN_IMPRESSION_COUNT;
                            break;
                        default:
                            throw new RuntimeException("Unsupported event type : " + eventType.toString());
                    }
                    break;
                default:
                    throw new RuntimeException("Unsupported entity type : " + entityType.toString());
            }

            updateEntityBudget(connection, entityIdEventCountMap, query);
            logger.debug("Done processing file : {}", file.getAbsolutePath());
        }

        // Finally move all the files from in process directory to done directory
        try {
            InputReader.moveFilesFromSourceToDestDirectory(inProcessDir, doneDir);
        } catch (IOException ioe) {
            logger.error("Error moving files from in process direcotyr : {}, to done directory : {}. Error : {}",
                    inProcessDir, doneDir, ioe);
            throw new RuntimeException(ioe);
        }
    }
}
