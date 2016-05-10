package com.kritter.device.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.constants.DeviceType;
import com.kritter.device.util.DeviceUtils;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * This class acts as entity to hold parent data for a device to be used
 * inside the DSP for various purposes.
 */
public class HandsetMasterData implements IUpdatableEntity<HandsetDataTopLevelKey>
{
    @Getter
    private Long internalId;

    @Getter @Setter
    private boolean isBot;

    @Getter
    private HandsetDataTopLevelKey handsetDataTopLevelKey;

    @Getter @Setter
    private Integer manufacturerId;

    @Getter
    private String manufacturerName;

    @Getter @Setter
    private Integer modelId;

    @Getter
    private String modelName;

    @Getter
    private String marketingName;

    @Getter @Setter
    private Integer deviceOperatingSystemId;

    @Getter
    private String osName;

    @Getter
    private String deviceOperatingSystemVersion;

    @Getter @Setter
    private Integer deviceBrowserId;

    @Getter
    private String browserName;

    @Getter
    private String deviceBrowserVersion;

    @Getter
    private String deviceCapabilityJSON;

    // associated java object for capability json.
    @Getter
    private HandsetCapabilities handsetCapabilityObject;

    /*A device can be said Javascript enabled only if the following features are reliably supported:
      alert, confirm, access form elements (dynamically set/modify values), setTimeout, setInterval,
      document.location.If a device fails one of these tests, mark as false (i.e. crippled javascript
      is not enough to be marked as javascript-enabled)*/
    @Getter @Setter
    private boolean isDeviceJavascriptCompatible;

    @Getter @Setter
    private DeviceType deviceType;

    @Getter
    private String modifiedBy;

    @Getter
    private Timestamp modifiedOn;

    private static final Timestamp MODIFIED_ON_NOW = new Timestamp(
            System.currentTimeMillis());

    private final static String COMMA = ",";
    private final static String APOSTROPHE = "'";
    private final static String EMPTY_JSON = "{}";

    // jackson json object mapper to convert json to java object.
    private static ObjectMapper jacksonObjectMapper = new ObjectMapper();

    // use this constructor for insertion of data into database.
    public HandsetMasterData(HandsetDataTopLevelKey handsetDataTopLevelKey,
                             Integer manufacturerId,String manufacturerName,
                             Integer modelId,String modelName,String marketingName,
                             Integer deviceOperatingSystemId,String osName,
                             String deviceOperatingSystemVersion,
                             Integer deviceBrowserId,String browserName,
                             String deviceBrowserVersion, String deviceCapabilityJSON,
                             String modifiedBy) throws Exception
    {
        if (       null == handsetDataTopLevelKey
                || null == manufacturerName
                || null == modelName
                || null == osName
                || null == browserName)

            throw new Exception(
                    "HandsetMasterData could not be initialized as one of the mandatory"
                            + " parameters is null");

        this.handsetDataTopLevelKey = handsetDataTopLevelKey;
        this.manufacturerId = manufacturerId;
        this.manufacturerName = manufacturerName;
        this.modelId = modelId;
        this.modelName = modelName;
        this.marketingName = marketingName;
        this.deviceOperatingSystemId = deviceOperatingSystemId;
        this.osName = osName;
        this.deviceOperatingSystemVersion = deviceOperatingSystemVersion;
        this.deviceBrowserId = deviceBrowserId;
        this.browserName = browserName;
        this.deviceBrowserVersion = deviceBrowserVersion;
        this.deviceCapabilityJSON = deviceCapabilityJSON;
        this.modifiedBy = modifiedBy;
        this.modifiedOn = MODIFIED_ON_NOW;

    }

    public HandsetMasterData(Long internalId,HandsetDataTopLevelKey handsetDataTopLevelKey,Integer manufacturerId,
                             Integer modelId, String marketingName,Integer deviceOperatingSystemId,
                             String deviceOperatingSystemVersion, Integer deviceBrowserId,
                             String deviceBrowserVersion, String deviceCapabilityJSON,
                             String modifiedBy, Timestamp modifiedOn) throws Exception
    {
        if (null == internalId || null == handsetDataTopLevelKey
                || null == manufacturerId || null == modelId
                || null == marketingName || null == deviceOperatingSystemId
                || null == deviceOperatingSystemVersion
                || null == deviceBrowserId || null == deviceBrowserVersion)
            throw new Exception(
                    "HandsetMasterData could not be initialized as one of the mandatory"
                            + " parameters is null");

        this.internalId = internalId;
        this.handsetDataTopLevelKey = handsetDataTopLevelKey;
        this.manufacturerId = manufacturerId;
        this.modelId = modelId;
        this.marketingName = marketingName;
        this.deviceOperatingSystemId = deviceOperatingSystemId;
        this.deviceOperatingSystemVersion = deviceOperatingSystemVersion;
        this.deviceBrowserId = deviceBrowserId;
        this.deviceBrowserVersion = deviceBrowserVersion;
        this.deviceCapabilityJSON = deviceCapabilityJSON;

        if(null != this.deviceCapabilityJSON)
            this.handsetCapabilityObject = jacksonObjectMapper.readValue(
                                                                         this.deviceCapabilityJSON,
                                                                         new TypeReference<HandsetCapabilities>(){}
                                                                        );
        this.modifiedBy = modifiedBy;
        this.modifiedOn = modifiedOn;
    }

    public HandsetMasterData(Integer manufacturerId, Integer modelId, String marketingName,
                             Integer deviceOperatingSystemId, String deviceOperatingSystemVersion,
                             Integer deviceBrowserId, String deviceBrowserVersion,
                             HandsetCapabilities handsetCapabilities) throws Exception {
        // Special value of -1, to be handled during fraud check
        this.internalId = -1L;
        this.manufacturerId = manufacturerId;
        this.modelId = modelId;
        this.marketingName = marketingName;
        this.deviceOperatingSystemId = deviceOperatingSystemId;
        this.deviceOperatingSystemVersion = deviceOperatingSystemVersion;
        this.deviceBrowserId = deviceBrowserId;
        this.deviceBrowserVersion = deviceBrowserVersion;
        this.handsetCapabilityObject = handsetCapabilities;
    }

    @Override
    public int hashCode()
    {
        return this.handsetDataTopLevelKey.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || getClass() != obj.getClass())
            return false;

        HandsetMasterData masterData = (HandsetMasterData) obj;
        return this.handsetDataTopLevelKey
                .equals(masterData.handsetDataTopLevelKey);
    }

    public String prepareSQLRowForMasterData() {

        StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.handsetDataTopLevelKey
                        .getExternalId()));
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.handsetDataTopLevelKey
                        .getSource()));
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(this.handsetDataTopLevelKey.getVersion());
        sb.append(COMMA);
        sb.append(this.manufacturerId);
        sb.append(COMMA);
        sb.append(this.modelId);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.marketingName));
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(this.deviceOperatingSystemId);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.deviceOperatingSystemVersion));
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(this.deviceBrowserId);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(DeviceUtils
                .convertTextDataToDBInsertionCapable(this.deviceBrowserVersion));
        sb.append(APOSTROPHE);
        sb.append(COMMA);

        if (null == this.deviceCapabilityJSON)
            sb.append(EMPTY_JSON);
        else {
            sb.append(APOSTROPHE);
            sb.append(this.deviceCapabilityJSON);
            sb.append(APOSTROPHE);
        }

        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(this.modifiedBy);
        sb.append(APOSTROPHE);
        sb.append(COMMA);
        sb.append(APOSTROPHE);
        sb.append(this.modifiedOn);
        sb.append(APOSTROPHE);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Long getModificationTime()
    {
        return modifiedOn.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public HandsetDataTopLevelKey getId()
    {
        return handsetDataTopLevelKey;
    }
}
