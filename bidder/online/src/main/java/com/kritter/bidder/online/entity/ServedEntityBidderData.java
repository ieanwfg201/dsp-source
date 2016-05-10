package com.kritter.bidder.online.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class is used to keep bidder generated data for served entity,
 * used in online algorithm to calculate bids for exchange.
 */
@Getter
@ToString
@EqualsAndHashCode(of={"entityId"})
public class ServedEntityBidderData implements IUpdatableEntity<Integer>
{
    private Integer entityId;
    private Double alphaValue;
    private boolean markedForDeletion;
    private Long modificationTime;
    private static final String CONTROL_A = String.valueOf((char) 1);

    public ServedEntityBidderData(Integer entityId,
                                  Double alphaValue,
                                  boolean markedForDeletion,
                                  Long modificationTime)
    {
        this.entityId = entityId;
        this.alphaValue = alphaValue;
        this.markedForDeletion = markedForDeletion;
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

    public static ServedEntityBidderData populateEntityUsingData(String dataLine,
                                                                 boolean markedForDeletion,
                                                                 Long modificationTime) throws RuntimeException
    {
        String dataParts[] = dataLine.split(CONTROL_A);

        if(null==dataParts || 2 != dataParts.length)
        {
            throw new RuntimeException("The data line is null or size is not 2, Error Data Line.");
        }

        try
        {
            Integer entityId = Integer.valueOf(dataParts[0]);
            Double alphaValue = Double.valueOf(dataParts[1]);

            return new ServedEntityBidderData(entityId,alphaValue,markedForDeletion,modificationTime);
        }
        catch (NumberFormatException nfe)
        {
            throw new RuntimeException("NumberFormatException in getting integer and double values",nfe);
        }
    }
}
