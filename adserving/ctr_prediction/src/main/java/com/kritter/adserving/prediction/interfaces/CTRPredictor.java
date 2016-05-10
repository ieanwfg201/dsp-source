package com.kritter.adserving.prediction.interfaces;

/**
 * This interface defines the basic functionality of a CTR predictor module
 */
public interface CTRPredictor {
    /*
     * Function to return the ctr for a request given the dimension values
     */
    public double getCTR(String[] dimensionNames, int[] dimensionValues);
}
