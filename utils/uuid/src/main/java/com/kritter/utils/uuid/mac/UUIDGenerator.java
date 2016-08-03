package com.kritter.utils.uuid.mac;

import com.kritter.utils.uuid.IUUIDGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *         This class is a utility class for generating time based universally
 *         unique identifier string. The unique id can be utilised at many
 *         places to assign to any database entity or for generation of
 *         impression UIDs.
 *
 *         This class is the version 1.
 *
 *         The UUID is 16 bytes(128 bits). Layout of the UUID is as follows :
 *         Byte 1 : Version (1 byte)
 *         Byte 2-7 : Machine mac address (6 bytes)
 *         Byte 8-13 : Time stamp in millis (6 bytes for timestamp). Even though
 *          Java returns the system time in long (8 bytes), 6 bytes should be
 *          more than ample for all practical purposes.
 *         Byte 14-16 : Atomic counter to maintain uniqueness across threads. (3
 *          bytes)
 *          The counter gets incremented for every UUID being generated.
 *
 *         3 bytes for the counter means that a single machine can generate 2^24,
 *         approximately 16 million UUID's every milli-second before repetition.
 *
 */

public class UUIDGenerator extends com.kritter.utils.uuid.rand.UUIDGenerator implements IUUIDGenerator{

    private static final int VERSION = 1;
    private static Long MAC_ADDRESS = null;
    private static final AtomicInteger COUNTER = new AtomicInteger();

    public UUIDGenerator() {
        MAC_ADDRESS = getMacAddress();
    }

    private static long getMacAddress() {

        long macAddress = 0;

        try {

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            byte[] mac = null;
            while(interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                mac = networkInterface.getHardwareAddress();
                if(mac != null)
                    break;
            }

            if(mac == null) {
                return -1;
                // throw new RuntimeException("No network device found on this machine. Unable to extract" +
                        // "mac address");
            }

            for(int i = 0; i < 6; ++i) {
                long ithByte = mac[i];
                macAddress = (macAddress << 8) | ((ithByte << 56) >>> 56);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return macAddress;
    }

    /**
     * This method would create a universally unique identifier.
     *
     * @return
     */
    public UUID generateUniversallyUniqueIdentifier() {
        if(MAC_ADDRESS == null) {
            return super.generateUniversallyUniqueIdentifier();
        }

        long msbBits = 0;
        long lsbBits = 0;

        msbBits |= VERSION;
        msbBits = (msbBits << 56) | (MAC_ADDRESS << 8);

        long currentTime = System.currentTimeMillis();
        msbBits |= ((currentTime << 16) >>> 56);

        lsbBits |= (currentTime << 24);

        int counter = COUNTER.incrementAndGet();
        counter &= 0xFFFFFF;
        lsbBits |=  counter;

        return new UUID(msbBits, lsbBits);
    }

    public static long extractTimeInLong(String uuidString) {
        if(MAC_ADDRESS == null) {
            return com.kritter.utils.uuid.rand.UUIDGenerator.extractTimeInLong(uuidString);
        }

        UUID uuid = UUID.fromString(uuidString);

        long msbBits = uuid.getMostSignificantBits();
        long lsbBits = uuid.getLeastSignificantBits();

        long currentTime = ((msbBits << 56) >>> 16);
        currentTime |= (lsbBits >> 24);
        return currentTime;
    }
}
