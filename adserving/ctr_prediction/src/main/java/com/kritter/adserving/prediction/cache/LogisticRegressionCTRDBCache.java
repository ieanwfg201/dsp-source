package com.kritter.adserving.prediction.cache;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.abstraction.cache.abstractions.AbstractDBStatsReloadableQueryableCache;
import com.kritter.adserving.prediction.entity.LogisticRegressionCTRModelEntity;
import com.kritter.adserving.prediction.entity.LogisticRegressionCTRModelEntity.CTRData;
import com.kritter.utils.databasemanager.DatabaseManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.List;
import java.util.Properties;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Loads up the logistic regression CTR cache. 
 */
public class LogisticRegressionCTRDBCache extends AbstractDBStatsReloadableQueryableCache<Integer, LogisticRegressionCTRModelEntity> {
    public static final String OTHERSID_KEY = "others-id";
    public static final String DIMENSION_VALUE_DELIMITER_KEY = "dim-value-delimiter";
    public static final String DIMENSION_COEFFICIENT_DELIMITER_KEY = "dim-coeff-delimiter";

    private static Logger logger = LogManager.getLogger("cache.logger");
    private String name;

    private int othersId;
    private String dimValueDelimiter;
    private String dimCoeffDelimiter;

    public LogisticRegressionCTRDBCache(List<Class> secIndexKeyClassList, Properties properties, DatabaseManager dbMgr, String cacheName) throws InitializationException {
        super(secIndexKeyClassList, logger, properties, dbMgr);
        this.name = cacheName;
        this.othersId = Integer.parseInt(properties.getProperty(OTHERSID_KEY));
        this.dimValueDelimiter = properties.getProperty(DIMENSION_VALUE_DELIMITER_KEY);
        this.dimCoeffDelimiter = properties.getProperty(DIMENSION_COEFFICIENT_DELIMITER_KEY);
    }

    @Override
    public ISecondaryIndexWrapper getSecondaryIndexKey(Class classname, LogisticRegressionCTRModelEntity entity) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected LogisticRegressionCTRModelEntity buildEntity(ResultSet resultSet) throws RefreshException {
        Integer modelId = null;
        try {
            Integer id = resultSet.getInt("id");
            String data = resultSet.getString("data");
            Timestamp lastModifiedOn = resultSet.getTimestamp("last_modified");

            CTRData ctrData = LogisticRegressionCTRModelEntity.parseDataStringToMap(data, this.dimCoeffDelimiter, this.dimValueDelimiter, this.logger);

            if(null == ctrData)
                return new LogisticRegressionCTRModelEntity.Builder(id, this.othersId, logger, lastModifiedOn).build();

            return new LogisticRegressionCTRModelEntity.Builder(id, ctrData, this.othersId, logger, lastModifiedOn).build();
        } catch(SQLException e) {
            logger.error("SQLException thrown while processing model {}", e);
            throw new RefreshException("SQLException thrown while processing model {}", e);
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }
}
