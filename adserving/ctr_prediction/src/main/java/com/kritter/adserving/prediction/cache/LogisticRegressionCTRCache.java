package com.kritter.adserving.prediction.cache;

import com.kritter.adserving.prediction.interfaces.CTRPredictor;
import com.kritter.abstraction.cache.abstractions.AbstractFileStatsReloadableCache;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Loads up the logistic regression CTR cache. 
 */
public class LogisticRegressionCTRCache extends AbstractFileStatsReloadableCache implements CTRPredictor {
    public static final String OTHERSID_KEY = "others-id";
    public static final String DIMENSION_VALUE_DELIMITER_KEY = "dim-value-delimiter";
    public static final String DIMENSION_COEFFICIENT_DELIMITER_KEY = "dim-coeff-delimiter";

    private Logger logger;
    private String name;

    private int othersId;
    private volatile Map<String, Map<Integer, Double>> dimensionCoefficients;
    private String dimValueDelimiter;
    private String dimCoeffDelimiter;

    public LogisticRegressionCTRCache(String name, String loggerName, Properties properties) throws InitializationException {
        super(LoggerFactory.getLogger(loggerName), properties);
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.othersId = Integer.parseInt(properties.getProperty(OTHERSID_KEY));
        this.dimValueDelimiter = properties.getProperty(DIMENSION_VALUE_DELIMITER_KEY);
        this.dimCoeffDelimiter = properties.getProperty(DIMENSION_COEFFICIENT_DELIMITER_KEY);
    }

    @Override
    public double getCTR(String[] dimensionNames, int[] dimensionValues) {

        double coefficientSum = 0;
        double ctr = 0.0;

        for(int i = 0 ; i < dimensionValues.length; ++i) {
            String dimensionName = dimensionNames[i];
            int dimensionValue = dimensionValues[i];
            double coefficient = 0;
            if(dimensionCoefficients.containsKey(dimensionName)) {
                Map<Integer, Double> curDimCoefficients = dimensionCoefficients.get(dimensionName);
                if(curDimCoefficients.containsKey(dimensionValue)) {
                    coefficient = curDimCoefficients.get(dimensionValue);
                    logger.debug("Dimension name = {}, value = {}, coefficient value = {}", dimensionName, dimensionValue, coefficient);
                } else if(curDimCoefficients.containsKey(this.othersId)) {
                    coefficient = curDimCoefficients.get(this.othersId);
                    logger.debug("Dimension name = {}, value = {} not found. Falling back to others_id = {}, coefficient value = {}", dimensionName, dimensionValue, this.othersId, coefficient);
                }
            } else {
                continue;
            }
            coefficientSum += coefficient;
        }

        ctr = 1.0 / (1 + Math.exp(-coefficientSum));
        return ctr;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void refreshFile(File file) throws RefreshException {
        if(file == null) {
            logger.debug("File provided for refresh is null");
            return;
        }

        Map<String, Map<Integer, Double>> curDimCoefficients = new HashMap<String, Map<Integer, Double>>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Parse the line and put the corresponding data in the map
                // Split the line on the dimension-coefficient delimiter and then on the dimension name-value delimiter
                String[] dimNameCoeffTokens = line.split(this.dimCoeffDelimiter);
                if(dimNameCoeffTokens.length != 2) {
                    logger.debug("Malformed line in CTR file. Line = {}", line);
                    continue;
                }

                String[] dimNameValueTokens = dimNameCoeffTokens[0].split(this.dimValueDelimiter);
                if(dimNameValueTokens.length != 2) {
                    logger.debug("Malformed line in CTR file. Line = {}", line);
                    continue;
                }

                String dimName = dimNameValueTokens[0];
                int dimValue = Integer.parseInt(dimNameValueTokens[1]);
                double coefficient = Double.parseDouble(dimNameCoeffTokens[1]);

                if(!curDimCoefficients.containsKey(dimName)) {
                    curDimCoefficients.put(dimName, new HashMap<Integer, Double>());
                }
                Map<Integer, Double> dimNameCoefficients = curDimCoefficients.get(dimName);
                dimNameCoefficients.put(dimValue, coefficient);
                logger.debug("Dimension name = {}, value = {}, coefficient = {}", dimName, dimValue, coefficient);
            }
        } catch (IOException ioe) {
            logger.debug("Unable to read the CTR file, file name = {}", file.getName());
            throw new RefreshException();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch(IOException ioException) {
            }
        }

        this.dimensionCoefficients = curDimCoefficients;
        logger.debug("Loaded the CTR feed file successfully");
    }

    @Override
    protected void release() throws ProcessingException {
    }
}
