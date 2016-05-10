package com.kritter.billing.ad_budget.reset;

import com.kritter.billing.ad_budget.EntityType;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to reset impression cap for all the entities
 */
public class ResetEvents {
    private static String IMPRESSION_CAMPAIGN_BUDGET_FETCH_QUERY = "SELECT campaign_impressions_budget.campaign_guid " +
            "as entity_guid, campaign_impressions_budget.impression_cap as event_cap, " +
            "campaign_impressions_budget.time_window_hours as time_window_hours FROM campaign_impressions_budget, " +
            "campaign WHERE campaign.guid = campaign_impressions_budget.campaign_guid AND " +
            "campaign.status_id = 1";

    private static String IMPRESSION_CAMPAIGN_BUDGET_RESET_QUERY = "UPDATE campaign_impressions_budget SET impressions_accrued=0, " +
            "last_modified=? WHERE campaign_guid=?";

    private static String IMPRESSION_AD_BUDGET_FETCH_QUERY = "SELECT ad_budget.ad_guid as entity_guid, " +
            "ad_budget.impression_cap as event_cap, ad_budget.time_window_hours as time_window_hours FROM ad_budget, " +
            "ad WHERE ad.guid = ad_budget.ad_guid AND ad.status_id = 1";

    private static String IMPRESSION_AD_BUDGET_RESET_QUERY = "UPDATE ad_budget SET impressions_accrued=0, " +
            "last_modified=? WHERE ad_guid=?";

    private static final Logger logger = LoggerFactory.getLogger(ResetEvents.class);

    @Setter
    @Getter
    private static class EntityBudget {
        private String entityGuid;
        private int eventCap;
        private int timeWindow;

        public EntityBudget(String entityGuid, int eventCap, int timeWindow) {
            this.entityGuid = entityGuid;
            this.eventCap = eventCap;
            this.timeWindow = timeWindow;
        }
    }

    /***
     * Fetches all entity budget rows from the database for active entities. The query for fetching the entity budget is also passed
     * as an argument.
     * @param connection Database connection
     * @param fetchQuery query to fetch entity budget. The result of the query must have rows such that they have three
     *                   columns with the following names :
     *                       entity_guid : String (containing the guid of the entity)
     *                       event_cap : Integer (containing the maximum number of events)
     *                       time_window_hours : Integer (number of hours for the cap)
     * @return List of entity budget objects corresponding to each active entity
     */
    public List<EntityBudget> getEntityBudget(Connection connection, String fetchQuery) {
        if(connection == null) {
            logger.error("DB connection in get entity budget is null");
            throw new RuntimeException("DB connection is null");
        }

        List<EntityBudget> entityBudgets = new LinkedList<>();

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(fetchQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String entityGuid = resultSet.getString("entity_guid");
                int eventCap = resultSet.getInt("event_cap");
                int timeWindowLength = resultSet.getInt("time_window_hours");
                EntityBudget entityBudget = new EntityBudget(entityGuid, eventCap, timeWindowLength);
                entityBudgets.add(entityBudget);
            }
        } catch (SQLException sqle) {
            logger.error("While fetching entity budgets to reset, encountered exception : {}", sqle);
            throw new RuntimeException(sqle);
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException sqle) {
                logger.error("Error closing prepared statement");
            }
        }

        return entityBudgets;
    }

    /***
     * Given an entity budget object and hour of day, determines whether the impressions accrued for this entity should be reset
     * Time window duration must not be more than 24 hours.
     * The impressions accrued field is reset in one of the following cases
     *    - If the current hour of day is divisible by the time window duration
     *    - Start of day (0th hour, which is anyways divisible by any time window duration. This is to highlight the
     *    fact)
     * @param entityBudget entity budget object
     * @param currentHourOfDay current hour of day (starting 0)
     * @return true if the budget should be reset, false otherwise
     */
    public boolean toReset(EntityBudget entityBudget, int currentHourOfDay) {
        if(entityBudget == null)
            return false;

        // If there is no impression cap, why waste network bandwidth in clearing impressions accrued
        if(entityBudget.getEventCap() == 0) {
            logger.debug("Impression cap for entity id : {} is 0. Not impression capped, hence no reset",
                    entityBudget.getEntityGuid());
            return false;
        }

        // The impression cap is for the duration of the entity
        if(entityBudget.getTimeWindow() == 0) {
            logger.debug("Time window for entity id : {} is 0. Impression cap for the entire duration of campaign, hence" +
                    "not resetting.", entityBudget.getEntityGuid());
            return false;
        }

        // If current hour of day is divisible by time window, reset budget
        if((currentHourOfDay % entityBudget.getTimeWindow()) == 0) {
            logger.debug("Entity id : {}, time window : {}, current hour : {}. Resetting!!", entityBudget.getEntityGuid(),
                    entityBudget.getTimeWindow(), currentHourOfDay);
            return true;
        }

        // If not divisible return false
        logger.debug("Entity id : {}, time window : {}, current hour : {}. Not resetting!!", entityBudget.getEntityGuid(),
                entityBudget.getTimeWindow(), currentHourOfDay);
        return false;
    }

    /***
     * Given a list of entity budget objects, returns the guid of entities for which impressions accrued need to be reset
     * @param entityBudgets entity budget objects representing rows in entity_budget table
     * @return list of guids of entities to reset
     */
    public List<String> getEntityGuidsToReset(List<EntityBudget> entityBudgets) {
        if(entityBudgets == null)
            return null;

        List<String> entityGuidsToReset = new LinkedList<>();

        DateTime dateTime = new DateTime();
        int currentHourOfDay = dateTime.getHourOfDay();

        for(EntityBudget entityBudget : entityBudgets) {
            if(toReset(entityBudget, currentHourOfDay)) {
                entityGuidsToReset.add(entityBudget.getEntityGuid());
                logger.info("Entity id {} needs to be updated.", entityBudget.getEntityGuid());
            } else {
                logger.info("Entity id {} need not be updated.", entityBudget.getEntityGuid());
            }
        }
        return entityGuidsToReset;
    }

    /***
     * Given the list of entity guids for which to reset impressions accrued, resets the impressions accrued and updates
     * the same in the database. Also takes a reset query, which must have wild cards, the first corresponding to
     * timestamp for last_modified, the second for entity guid
     * @param connection Database connection
     * @param entityGuidsToReset list of entity guids for which to reset impressions accrued
     * @param resetQuery reset query in the data base. must have wild cards, first corresponding to timestamp and the
     *                   second corresponding to the entity guid
     */
    public void resetEntityBudget(Connection connection, List<String> entityGuidsToReset, String resetQuery) {
        if(connection == null) {
            logger.error("Could not update entity budgets since connection is null");
            throw new RuntimeException("Could not update entity budgets since connection is null");
        }

        DateTime dateTime = new DateTime();
        Timestamp timestamp = new Timestamp(dateTime.getMillis());

        boolean autoCommit = false;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(resetQuery);
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            for (String entityGuid : entityGuidsToReset) {
                preparedStatement.setTimestamp(1, timestamp);
                preparedStatement.setString(2, entityGuid);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException sqle) {
            logger.error("SQL Exception during update of entity budgets : {}", sqle);
            throw new RuntimeException(sqle);
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                connection.setAutoCommit(autoCommit);
            } catch (SQLException sqle) {
                logger.error("SQL Exception during finally of update of entity budgets : {}", sqle);
            }
        }
    }

    /***
     * Top level function which combines the functionality of the other functions. Fetches entity budgets from the database,
     * finds which entities to reset impressions for and then resets the impressions for those entities
     * @param connection Database connection
     */
    public void readEntityBudgetsAndReset(Connection connection, PostImpressionEvent event, EntityType entity) {
        if(entity == null) {
            throw new RuntimeException("No entity type passed");
        }

        if(event == null) {
            throw new RuntimeException("No event type passed");
        }

        // Get all entity budgets
        String fetchQuery = null;
        switch (entity) {
            case CAMPAIGN:
                switch (event) {
                    case RENDER:
                        fetchQuery = IMPRESSION_CAMPAIGN_BUDGET_FETCH_QUERY;
                        break;
                    default:
                        throw new RuntimeException("Unsupported event type " + event.toString());
                }
                break;
            case AD:
                switch (event) {
                    case RENDER:
                        fetchQuery = IMPRESSION_AD_BUDGET_FETCH_QUERY;
                        break;
                    default:
                        throw new RuntimeException("Unsupported event type " + event.toString());
                }
                break;
            default:
                throw new RuntimeException("Unsupported entity type " + entity.toString());
        }
        List<EntityBudget> entityBudgets = getEntityBudget(connection, fetchQuery);

        // Get the guids of entities to reset
        List<String> entityGuidsToReset = getEntityGuidsToReset(entityBudgets);

        // Go ahead and reset entity impressions accrued
        String resetQuery = null;
        switch (entity) {
            case CAMPAIGN:
                switch (event) {
                    case RENDER:
                        resetQuery = IMPRESSION_CAMPAIGN_BUDGET_RESET_QUERY;
                        break;
                    default:
                        throw new RuntimeException("Unsupported event type " + event.toString());
                }
                break;
            case AD:
                switch (event) {
                    case RENDER:
                        resetQuery = IMPRESSION_AD_BUDGET_RESET_QUERY;
                        break;
                    default:
                        throw new RuntimeException("Unsupported event type " + event.toString());
                }
                break;
            default:
                throw new RuntimeException("Unsupported entity type " + entity.toString());
        }
        resetEntityBudget(connection, entityGuidsToReset, resetQuery);
    }
}
