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

    public static ExternalUserId fromString(String userIdStr) {
        // Split the given user id string on the delimiter
        String[] tokens = userIdStr.split(delimiter);
        if(tokens.length < 2 || tokens.length > 3) {
            throw new RuntimeException("User id string provided is malformed : " + userIdStr);
        }

        String userIdTypeName = tokens[0];
        String sourceId = null;
        String userId;
        if(tokens.length == 3) {
            sourceId = tokens[1];
            userId = tokens[2];
        } else {
            userId = tokens[1];
        }

        ExternalUserIdType userIdType = ExternalUserIdType.getEnum(userIdTypeName);
        if(userIdType == null) {
            throw new RuntimeException("No user id type for user id in user id string : " + userIdTypeName);
        }

        if(userIdType.isSourceRequired() && sourceId == null) {
            throw new RuntimeException("User id provided is " + userIdStr + ". User id type " + userIdType.name() +
                    " requires source id but no source id provided. Can't decipher the given user id.");
        }

        if(sourceId == null)
            sourceId = "-1";

        return new ExternalUserId(userIdType, Integer.parseInt(sourceId), userId);
    }
}
