package com.kritter.api.entity.parent_account;


import com.kritter.constants.ParentAccountType;
import com.kritter.constants.StatusIdEnum;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * This class models a parent account in our DSP system, in practical
 * purpose this would be a business entity owning our software.
 */
public class ParentAccount
{
    /** mandatory int internal id of account {default at the time of creation} */
    @Getter @Setter
    private int id = -1;

    /** mandatory String guid of account {default at the time of creation} */
    @Getter @Setter
    private String guid = null;

    /** mandatory @see  com.kritter.constants.StatusIdEnum */
    @Getter @Setter
    private StatusIdEnum status = StatusIdEnum.Pending;

    /** mandatory */
    @Getter @Setter
    private ParentAccountType type_id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private Timestamp createdOn;

    @Getter @Setter
    private Timestamp lastModified;

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        ParentAccount externalObject = (ParentAccount) obj;

        if(
           this.guid.equals(externalObject.guid) &&
           this.id == externalObject.id
          )
            return true;

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + (this.id)
                         + (this.status.getCode())
                         + (this.guid != null ? this.guid.hashCode() : 0);

        return hash;
    }
}
