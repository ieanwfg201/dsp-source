package com.kritter.api.entity.account_user;

import com.kritter.constants.AccountUserType;
import com.kritter.constants.StatusIdEnum;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * This class models a user who operates upon an account
 * which is persisted in the account table.
 */
public class AccountUser
{
    /** mandatory int internal id of account {default at the time of creation} */
    @Getter
    @Setter
    private int id = -1;

    /** mandatory String guid of account {default at the time of creation} */
    @Getter @Setter
    private String guid = null;

    /** mandatory @see  com.kritter.constants.StatusIdEnum */
    @Getter @Setter
    private StatusIdEnum status = StatusIdEnum.Pending;

    /** mandatory*/
    @Getter @Setter
    private AccountUserType type_id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private AccountUserType accountUserType;

    /*This field is used to define an account access rules which are defined inside table account_access*/
    @Getter @Setter
    private String accountAccessReference;

    /*This field is used to define account ids where this user has got access of different types, which are
    * defined by the field accountAccessReference*/
    @Getter @Setter
    private String accountReference;

    @Getter @Setter
    private Timestamp createdOn;

    @Getter @Setter
    private Timestamp lastModified;

    @Getter @Setter
    private boolean isDeprecated;

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        AccountUser externalObject = (AccountUser) obj;

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
