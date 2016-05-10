package com.kritter.adserving.prediction.interfaces;

/**
 * This interface defines the basic functionality of a CSR predictor module.
 */
public interface CSRPredictor {
    /*
     * Function to return the csr for a request given the dimension values
     */
    public double getCSR(String[] dimensionNames, int[] dimensionValues);
}
