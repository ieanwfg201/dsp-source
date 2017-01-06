package com.kritter.adserving.prediction.cache;

import com.kritter.adserving.prediction.interfaces.CTRPredictor;
import com.kritter.abstraction.cache.abstractions.AbstractFileStatsReloadableCache;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.RefreshException;
import com.kritter.rtb.ds.pair.Pair;
import com.kritter.rtb.ds.trie.Trie;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Loads up the segment ctr cache and exposes the function therein.
 */
public class SegmentCTRCache extends AbstractFileStatsReloadableCache implements CTRPredictor {
    private Logger logger;
    private String name;

    private Trie<Pair<Double, Double>> trie;
    private ReadWriteLock readWriteLock;

    public SegmentCTRCache(String name, String loggerName, Properties properties) throws InitializationException {
        super(LogManager.getLogger(loggerName), properties);
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getCTR(String[] dimensionNames, int[] dimensionValues) {

        Lock readLock = readWriteLock.readLock();

        Pair<Double, Double> impClicks = null;
        try{
            readLock.lock();
            impClicks = trie.getValueForPath(dimensionValues);
        }
        finally {
            readLock.unlock();
        }

        if(null == impClicks)
            return 0;

        double impressions = impClicks.getFirst() == null ? 0 : impClicks.getFirst();
        double clicks = impClicks.getSecond() == null ? 0 : impClicks.getSecond();

        if(impressions == 0)
            return 0;
        return clicks / impressions;
    }

    @Override
    protected void refreshFile(File file) throws RefreshException {
        if(file == null) {
            logger.debug("File provided for refresh is null");
            return;
        }

        ObjectInputStream inputStream;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(file));
        } catch (IOException ioe) {
            logger.debug("Unable to read the CTR file, file name = {}", file.getName());
            throw new RefreshException();
        }

        Trie<Pair<Double, Double>> tempTrie = null;
        try {
            tempTrie = (Trie<Pair<Double, Double>>) inputStream.readObject();
        } catch (ClassNotFoundException cnfe) {
            logger.debug("Class not found");
            throw new RefreshException();
        } catch (IOException ioe) {
            logger.debug("IOException while reading the CTR file. File name = {}", file.getName());
            throw new RefreshException();
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioe) {
                logger.debug("Unable to close the file {}", file.getName());
            }
        }

        // Swap the old trie with new trie
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            trie = tempTrie;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    protected void release() throws ProcessingException {
    }
}
