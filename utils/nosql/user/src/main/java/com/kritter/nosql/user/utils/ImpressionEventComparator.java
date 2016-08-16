package com.kritter.nosql.user.utils;

import com.kritter.user.thrift.struct.ImpressionEvent;

import java.util.Comparator;

public class ImpressionEventComparator implements Comparator<ImpressionEvent> {
    @Override
    public int compare(ImpressionEvent impressionEvent, ImpressionEvent t1) {
        if(impressionEvent.getTimestamp() < t1.getTimestamp())
            return -1;
        if(impressionEvent.getTimestamp() > t1.getTimestamp())
            return 1;
        if(impressionEvent.getAdId() < t1.getAdId())
            return -1;
        if(impressionEvent.getAdId() > t1.getAdId())
            return 1;
        return 0;
    }
}

