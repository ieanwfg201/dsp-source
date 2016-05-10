package com.kritter.core.workflow;

import java.util.HashMap;
import java.util.UUID;

import com.kritter.utils.uuid.IUUIDGenerator;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * The application context object that flows through the whole application.
 * This contains the object containing the context for the whole request.
 * This class is not thread-safe. To be used within a single thread.
 */
public class Context {
    /**
     * Unique identifier for this context object. Must be unique across all
     * requests.
     */
    @Getter private UUID uuid;
    private IUUIDGenerator uuidGenerator;
    private HashMap<String, Object> map;
    /**
     * Status of the particular request.
     */
    @Getter
    @Setter
    private boolean terminated;

    public Context(boolean useMacAddressImplForUUID) {

        if(useMacAddressImplForUUID)
            this.uuidGenerator = new UUIDGenerator();
        else
            this.uuidGenerator = new com.kritter.utils.uuid.rand.UUIDGenerator();

        this.uuid = this.uuidGenerator.generateUniversallyUniqueIdentifier();
        this.map = new HashMap<String, Object>();
        this.terminated = false;
    }

    /**
     * @param key String for a value is set in the context object
     * @return Value corresponding to the supplied key, null if absent
     */
    public Object getValue(String key) {
        return map.get(key);
    }

    /**
     * @param key Key against which value is to be stored. If key already exists, the value is overwritten
     * @param value
     */
    public void setValue(String key, Object value) {
        map.put(key, value);
    }
}
