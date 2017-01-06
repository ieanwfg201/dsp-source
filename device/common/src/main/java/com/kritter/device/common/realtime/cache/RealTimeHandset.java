package com.kritter.device.common.realtime.cache;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class RealTimeHandset
{
    @Getter @Setter
    private String brandName;
    @Getter @Setter
    private String modelName;
    @Getter @Setter
    private String marketingName;
    @Getter @Setter
    private String deviceOs;
    @Getter @Setter
    private String deviceOsVersion;
    @Getter @Setter
    private String browserName;
    @Getter @Setter
    private String browserVersion;
    @Getter @Setter
    private boolean isTablet;
    @Getter @Setter
    private boolean isWirelessDevice;
    @Getter @Setter
    private boolean j2meMidp2;
    @Getter @Setter
    private String resolutionWidth;
    @Getter @Setter
    private String resolutionHeight;
    @Getter @Setter
    private boolean ajaxSupportJava;
    @Getter @Setter
    private boolean isBot;
    @Getter @Setter
    private String deviceType;

    private final MetricRegistry metricRegistry;
    private static final String USAGE_COUNTER_NAME = "u";
    private static final String DETECTION_TIME_COUNTER_NAME = "d";

    public RealTimeHandset()
    {
        this.metricRegistry = new MetricRegistry();
    }

    public void incrementUsageCount()
    {
        Counter counter = this.metricRegistry.counter(USAGE_COUNTER_NAME);
        counter.inc();
    }

    public long getUsageCount()
    {
        Counter counter = this.metricRegistry.counter(USAGE_COUNTER_NAME);
        return counter.getCount();
    }

    public void setDetectionTimeMillis(long detectionTimeMillis)
    {
        Counter counter = this.metricRegistry.counter(DETECTION_TIME_COUNTER_NAME);
        long time = counter.getCount();
        if(detectionTimeMillis > time)
        {
            counter.dec(time);
            counter.inc(detectionTimeMillis);
        }
    }

    public long getDetectionTimeMillis()
    {
        Counter counter = this.metricRegistry.counter(DETECTION_TIME_COUNTER_NAME);
        return counter.getCount();
    }
}