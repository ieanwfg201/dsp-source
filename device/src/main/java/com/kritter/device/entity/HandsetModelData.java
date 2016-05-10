package com.kritter.device.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.device.util.DeviceUtils;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * This class is used to keep handset model data in-memory.
 * The key would be function(model_name,manufacturer_id).
 */
@ToString
public class HandsetModelData implements IUpdatableEntity<String>
{
    private Integer modelId;
    private Integer manufacturerId;
    @Getter
    private String manufacturerName;
    private String modelName;
    private String modifiedBy;
    private Timestamp modifiedOn;
    private static final Timestamp MODIFIED_ON_NOW = new Timestamp(System.currentTimeMillis());
    private final static String COMMA = ",";
    private final static String APOSTROPHE = "'";
    private final static String KEY_DELIMITER = ":";
    private final static String MODEL_QUERY_PREFIX =
            "select (select manufacturer_id from handset_manufacturer where LOWER(manufacturer_name) = '";

    public HandsetModelData(String manufacturerName, String modelName, String modifiedBy) throws Exception
    {
        if (null == manufacturerName || null == modelName)
            throw new Exception("HandsetModelData could not be initialized as one of the model id " +
                                "or manufacturer name is null");

        this.manufacturerName = manufacturerName;
        this.modelName = modelName;
        this.modifiedBy = modifiedBy;
        this.modifiedOn = MODIFIED_ON_NOW;
    }

    public HandsetModelData(Integer modelId, Integer manufacturerId,
                            String modelName, String modifiedBy,
                            Timestamp modifiedOn) throws Exception
    {
        if (null == modelId || null == manufacturerId || null == modelName)
            throw new Exception("HandsetModelData could not be initialized as one of the " +
                                "modelid or manufacturerid or modelname is null");

        this.modelId = modelId;
        this.manufacturerId = manufacturerId;
        this.modelName = modelName;
        this.modifiedBy = modifiedBy;
        this.modifiedOn = modifiedOn;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash
                + (this.modelName != null ? this.modelName.hashCode() : 0)
                + (this.modelId != null ? this.modelId : 0)
                + (this.manufacturerId != null ? this.manufacturerId : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj || getClass() != obj.getClass())
            return false;

        HandsetModelData externalObject = (HandsetModelData) obj;

        return this.modelName.toLowerCase().equals(externalObject.modelName.toLowerCase()) &&
                this.manufacturerName.toLowerCase().equals(externalObject.manufacturerName.toLowerCase());
    }

    public String prepareSQLRowForModelData(boolean firstRecord) {

        StringBuffer sb = new StringBuffer();
        if(firstRecord)
            sb.append(MODEL_QUERY_PREFIX);
        else
        {
            sb.append(" UNION ");
            sb.append(MODEL_QUERY_PREFIX);
        }

        sb.append(this.manufacturerName.toLowerCase());
        sb.append(APOSTROPHE);
        sb.append(")");
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.modelName));
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(this.modifiedBy);
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(this.modifiedOn);
        sb.append(APOSTROPHE);
        return sb.toString();
    }

    public Integer getModelId() {
        return modelId;
    }

    public Integer getManufacturerId() {
        return manufacturerId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Timestamp getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public Long getModificationTime() {
        return modifiedOn.getTime();
    }

    @Override
    public boolean isMarkedForDeletion() {
        return false;
    }

    public static String createId(String modelName, Integer manufacturerId) {
        StringBuffer key = new StringBuffer(modelName.toLowerCase());
        key.append(KEY_DELIMITER);
        key.append(manufacturerId);
        return key.toString();
    }

    @Override
    public String getId() {
        StringBuffer key = new StringBuffer(modelName.toLowerCase());
        key.append(KEY_DELIMITER);
        key.append(manufacturerId);
        return key.toString();
    }
}

