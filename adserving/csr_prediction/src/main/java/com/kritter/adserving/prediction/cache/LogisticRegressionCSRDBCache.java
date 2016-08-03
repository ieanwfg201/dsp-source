package com.kritter.adserving.prediction.cache;

import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.adserving.prediction.entity.LogisticRegressionCSRModelEntity;
import com.kritter.adserving.prediction.entity.LogisticRegressionCSRModelEntity.CSRData;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * Loads up the logistic regression CSR cache.
 */
public class LogisticRegressionCSRDBCache extends AbstractDBStatsReloadableQueryableCache<Integer, LogisticRegressionCSRModelEntity> {
    public static final String OTHERSID_KEY = "others-id";
    public static final String DIMENSION_VALUE_DELIMITER_KEY = "dim-value-delimiter";
    public static final String DIMENSION_COEFFICIENT_DELIMITER_KEY = "dim-coeff-delimiter";

    private static Logger logger = LoggerFactory.getLogger("cache.logger");
    private String name;

    private int othersId;
    private String dimValueDelimiter;
    private String dimCoeffDelimiter;

    public LogisticRegressionCSRDBCache(List<Class> secIndexKeyClassList, Properties properties, DatabaseManager dbMgr, String cacheName) throws InitializationException{
        super(secIndexKeyClassList, logger, properties, dbMgr);
        this.name = cacheName;
        this.othersId = Integer.parseInt(properties.getProperty(OTHERSID_KEY));
        this.dimValueDelimiter = properties.getProperty(DIMENSION_VALUE_DELIMITER_KEY);
        this.dimCoeffDelimiter = properties.getProperty(DIMENSION_COEFFICIENT_DELIMITER_KEY);
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class classname, LogisticRegressionCSRModelEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected LogisticRegressionCSRModelEntity buildEntity(ResultSet resultSet) throws RefreshException {
        Integer modelId = null;
        try {
            Integer id = resultSet.getInt("id");
            String data = resultSet.getString("data");
            Timestamp lastModifiedOn = resultSet.getTimestamp("last_modified");

            CSRData csrData = LogisticRegressionCSRModelEntity.parseDataStringToMap(data, this.dimCoeffDelimiter, this.dimValueDelimiter, this.logger);

            return new LogisticRegressionCSRModelEntity.Builder(id, csrData, this.othersId, logger, lastModifiedOn).build();
        } catch(SQLException e) {
            logger.error("SQLException thrown while processing model {}", e);
            throw new RefreshException("SQLException thrown while processing model {}", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }
}
