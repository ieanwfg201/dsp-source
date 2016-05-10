package com.kritter.entity.user.userid;

import com.kritter.constants.ExternalUserIdType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Class to represent user ids that we get from outside. Multiple different user ids could map to the same user,
 * thereby generating a single internal user id
 */
@Getter
@Setter
@EqualsAndHashCode
public class ExternalUserId {
    public static final String delimiter = ":";
    /**
     * Type of user id. One of the enum values.
     */
    private ExternalUserIdType idType;
    /**
     * Id of source from which the user id was obtained
     */
    private int source;
    /**
     * String representing the user id
     */
    private String userId;

    public ExternalUserId(ExternalUserIdType idType, int source, String userId) {
        this.idType = idType;
        this.source = source;
        this.userId = userId;
    }

    /**
     * Converts this external user id to a string.
     * If the id type requires source, then the string looks like
     * idtype-name{delimiter}source{delimiter}userId
     * If the id type doesn't require source, the string looks like
     * idtype-name{delimiter}userId
     *
     * @return String representation of the external user id
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.idType.getTypeName());
        builder.append(delimiter);
        if(idType.isSourceRequired()) {
            builder.append(source);
            builder.append(delimiter);
        }
        builder.append(this.userId);
        return builder.toString();
    }
}
