package com.kritter.common.caches.account.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.constants.DemandPreference;
import com.kritter.constants.DemandType;
import com.kritter.entity.account.AccountDef;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * This class keeps Account Entity
 */
public class AccountEntity extends AccountDef implements IUpdatableEntity<String>
{

    private final Timestamp updateTime;


    public AccountEntity(int id, String guid, int demandType, int demandPreference
            , int qps, int timeout, Timestamp updateTime, int currency, boolean test, Short[] btype, String demand_url) throws IOException
    {
        this.accountId = id;
        this.guid = guid;
        this.demandType = DemandType.getEnum(demandType);
        this.demandPreference = DemandPreference.getEnum(demandPreference);
        this.qps = qps;
        this.timeout = timeout;
        this.updateTime = updateTime;
        this.currency=currency;
        this.test=test;
        this.btype=btype;
        this.demand_url = demand_url;
    }
    @Override
    public Long getModificationTime()
    {
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }
    public String getId()
    {
        return this.guid;
    }
}
