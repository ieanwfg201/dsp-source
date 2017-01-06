package com.kritter.adserving.prediction.entity;

import java.sql.Timestamp;
import java.util.Map;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.adserving.prediction.interfaces.CTRPredictor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of={"modelId"})
public class LogisticRegressionCTRModelEntity implements IUpdatableEntity<Integer>, CTRPredictor {

    @Getter
    private Integer modelId;

    private final int othersId;
    private final Logger logger;
    private final double intercept;
    private final double absThreshold;
    private final Map<String, Map<Integer, Double>> dimensionCoefficients;
    private final Timestamp updateTime;

    public LogisticRegressionCTRModelEntity(Builder builder) {
        this.modelId = builder.modelId;
        this.intercept = builder.intercept;
        this.absThreshold = builder.absThreshold;
        this.dimensionCoefficients = builder.dimensionCoefficients;
        this.othersId = builder.othersId;
        this.logger = builder.logger;
        this.updateTime = builder.updateTime;
    }

    @Override
    public int hashCode() {
        return this.modelId;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || getClass() != obj.getClass())
            return false;

        LogisticRegressionCTRModelEntity externalObject = (LogisticRegressionCTRModelEntity) obj;

        if (this.modelId.equals(externalObject.modelId))
            return true;
        return false;
    }

    @Override
    public Long getModificationTime() {
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return false;
    }

    @Override
    public Integer getId() {
        return this.modelId;
    }

    public static class CTRData {
        @Getter
        private final double intercept;
        @Getter
        private final double absThreshold;
        @Getter
        private final Map<String, Map<Integer, Double>> curDimCoefficients;

        public CTRData(double intercept, Map<String, Map<Integer, Double>> curDimCoefficients) {
            this.intercept = intercept;
            this.curDimCoefficients = curDimCoefficients;
            this.absThreshold = 100;
        }

        public CTRData(double intercept, Map<String, Map<Integer, Double>> curDimCoefficients, double absThreshold) {
            this.intercept = intercept;
            this.curDimCoefficients = curDimCoefficients;
            this.absThreshold = absThreshold;
        }
    }

    // public static Map<String, Map<Integer, Double>> parseDataStringToMap(String data, String dimCoeffDelimiter, String dimValueDelimiter, Logger logger) {
    public static CTRData parseDataStringToMap(String data, String dimCoeffDelimiter, String dimValueDelimiter, Logger logger) {

        if(null == data || "".equals(data))
            return null;

        Map<String, Map<Integer, Double>> curDimCoefficients = new HashMap<String, Map<Integer, Double>>();
        double intercept = 0;
        double absThreshold = 100;

        String[] lines = data.split("\n");
        for(String line : lines) {
            // Parse the line and put the corresponding data in the map
            // Split the line on the dimension-coefficient delimiter and then on the dimension name-value delimiter
            String[] dimNameCoeffTokens = line.split(dimCoeffDelimiter);
            if(dimNameCoeffTokens.length != 2) {
                logger.debug("Malformed line in CTR file. Line = {}", line);
                continue;
            }

            String[] dimNameValueTokens = dimNameCoeffTokens[0].split(dimValueDelimiter);
            if(dimNameValueTokens.length != 2) {
                if(dimNameValueTokens[0].equals("intercept")) {
                    intercept = Double.parseDouble(dimNameCoeffTokens[1]);
                    continue;
                }
                if(dimNameValueTokens[0].equals("absoluteCtrCeiling")) {
                    absThreshold = Double.parseDouble(dimNameCoeffTokens[1]);
                    continue;
                }
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

        return new CTRData(intercept, curDimCoefficients, absThreshold);
    }

    @Override
    public double getCTR(String[] dimensionNames, int[] dimensionValues) {

        if(null == this.dimensionCoefficients)
            return -1.0;

        double coefficientSum = intercept;
        for(int i = 0 ; i < dimensionValues.length; ++i) {
            String dimensionName = dimensionNames[i];
            int dimensionValue = dimensionValues[i];
            double coefficient = 0;
            if(this.dimensionCoefficients.containsKey(dimensionName)) {
                Map<Integer, Double> curDimCoefficients = this.dimensionCoefficients.get(dimensionName);
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

        double ctr = 1.0 / (1 + Math.exp(-coefficientSum));
        if(ctr > absThreshold) {
            ctr = absThreshold;
        }
        return ctr;
    }

    public static class Builder {
        private final Integer modelId;
        private final double intercept;
        private final double absThreshold;
        private final Map<String, Map<Integer, Double>> dimensionCoefficients;
        private final int othersId;
        private final Logger logger;
        private final Timestamp updateTime;

        public Builder(Integer modelId, CTRData data, int othersId, Logger logger, Timestamp modifiedTime) {
            this.modelId = modelId;
            this.intercept = data.getIntercept();
            this.absThreshold = data.getAbsThreshold();
            this.dimensionCoefficients = data.getCurDimCoefficients();
            this.othersId = othersId;
            this.logger = logger;
            this.updateTime = modifiedTime;
        }

        public Builder(Integer modelId, int othersId, Logger logger, Timestamp modifiedTime) {
            this.modelId = modelId;
            this.intercept = 0;
            this.absThreshold = 0;
            this.dimensionCoefficients = null;
            this.othersId = othersId;
            this.logger = logger;
            this.updateTime = modifiedTime;
        }

        public LogisticRegressionCTRModelEntity build() {
            return new LogisticRegressionCTRModelEntity(this);
        }
    }
}
