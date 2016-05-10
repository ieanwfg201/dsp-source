package com.kritter.bidder.online.entity;

import java.util.Map;
import java.util.HashMap;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;

/**
 * This class is used to keep bidder generated data for served entity,
 * used in online algorithm to calculate bids for exchange.
 */
@Getter
@ToString
@EqualsAndHashCode(of={"entityId"})
public class BidderDataDBEntity implements IUpdatableEntity<Integer>
{

    @Getter
    private Integer entityId;

    private Map<Integer, Double> demandEntityAlphaMapping;
    private boolean markedForDeletion;
    private Long modificationTime;
    private static final String CONTROL_A = String.valueOf((char) 1);
    private static final String CONTROL_B = String.valueOf((char)2);
    private static final String PREFIX = "a";

    public BidderDataDBEntity(Integer entityId,
                              Map<Integer, Double> demandEntityAlphaMapping,
                              Long modificationTime)
    {
        this.entityId = entityId;
        this.demandEntityAlphaMapping = demandEntityAlphaMapping;
        this.markedForDeletion = false;
        this.modificationTime = modificationTime;
    }

    @Override
    public Long getModificationTime()
    {
        return modificationTime;
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return markedForDeletion;
    }

    @Override
    public Integer getId()
    {
        return entityId;
    }

    public Double getAlphaValueForDemandEntity(Integer demandEntityId)
    {
        if(null != demandEntityAlphaMapping)
        {
            return demandEntityAlphaMapping.get(demandEntityId);
        }

        return null;
    }

    public static BidderDataDBEntity populateEntityUsingData(
                                                             Logger logger,
                                                             Integer id,
                                                             String data,
                                                             Long modificationTime
                                                            ) throws RuntimeException
    {
        Map<Integer, Double> demandEntityAlphaMapping = new HashMap<Integer, Double>();
        if(null == data || "".equals(data))
            return new BidderDataDBEntity(id,null,modificationTime);

        data = data.trim();
        String[] dataLines = data.split("\n");
        for(String dataLine : dataLines)
        {
            if(!dataLine.startsWith(PREFIX))
            {
                logger.debug("Dataline:{} is of no use for online bidder.Skipping...",dataLine);
                continue;
            }


            String dataPartsFirst[] = dataLine.split(CONTROL_A);

            if(null == dataPartsFirst || dataPartsFirst.length != 2)
            {
                throw new RuntimeException("The data line is null or size is not 2, Error Data Line: " + dataLine);
            }

            String dataParts[] = dataPartsFirst[1].split(CONTROL_B);

            if(null==dataParts || 2 != dataParts.length)
            {
                throw new RuntimeException("The data line is null or size is not 2, Error Data Line: " + dataLine);
            }

            try
            {
                Integer demandEntityId = Integer.valueOf(dataParts[0]);
                Double alphaValue = Double.valueOf(dataParts[1]);

                demandEntityAlphaMapping.put(demandEntityId, alphaValue);
            }
            catch (NumberFormatException nfe)
            {
                throw new RuntimeException("NumberFormatException in getting integer and double values",nfe);
            }
        }

        return new BidderDataDBEntity(id, demandEntityAlphaMapping, modificationTime);
    }
}
