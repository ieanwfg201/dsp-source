package com.kritter.utils.common.url;

/**
 * This class keeps utility methods to be used inside classes storing and fetching url fields.
 */
public class URLFieldUtils
{
    public static int[] fetchBitArrayForInteger(int value,short maxSizeOfBitArray)
    {
        int[] bitSet = new int[maxSizeOfBitArray];

        for(int i = maxSizeOfBitArray - 1 ; i >= 0 ; i --)
        {
            int positionValue = value & (1 << i);

            if(positionValue != 0)
                bitSet[maxSizeOfBitArray - 1 - i] = 1;
        }

        return bitSet;
    }

    public static int[] fetchBitArrayForLong(long value,short maxSizeOfBitArray)
    {
        int[] bitSet = new int[maxSizeOfBitArray];

        for(int i = maxSizeOfBitArray - 1 ; i >= 0 ; i --)
        {
            long positionValue = value & (1L << i);

            if(positionValue != 0)
                bitSet[maxSizeOfBitArray - 1 - i] = 1;
        }

        return bitSet;
    }

    public static int[] fetchBitArrayForShort(short value,short maxSizeOfBitArray)
    {
        int[] bitSet = new int[maxSizeOfBitArray];

        for(int i = maxSizeOfBitArray - 1 ; i >= 0 ; i --)
        {
            short positionValue = (short)(value & (1 << i));

            if(positionValue != 0)
                bitSet[maxSizeOfBitArray - 1 - i] = 1;
        }

        return bitSet;
    }

    public static int[] fetchBitArrayForFloat(float value,short maxSizeOfBitArray)
    {
        int valueToUse = Float.floatToIntBits(value);

        int[] bitSet = new int[maxSizeOfBitArray];

        for(int i = maxSizeOfBitArray - 1 ; i >= 0 ; i --)
        {
            int positionValue = valueToUse & (1 << i);

            if(positionValue != 0)
                bitSet[maxSizeOfBitArray - 1 - i] = 1;
        }

        return bitSet;
    }

    public static int  fetchIntegerFromByteArray(byte[] b)
    {
        int value = 0;
        for(byte byteValue : b)
            value = (value << 8) + (byteValue & 0xFF);

        return value;
    }

    public static int fetchIntegerFromByte(byte b)
    {
        int value = 0;
        value = (value << 8) + (b & 0xFF);
        return value;
    }

    public static short fetchShortFromByteArray(byte[] b)
    {
        short value = 0;
        for(byte byteValue : b)
            value = (short)((value << 8) + (byteValue & 0xFF));

        return value;
    }

    public static long fetchLongFromByteArray(byte[] b)
    {
        long value = 0;
        for(byte byteValue : b)
            value = (value << 8) + (byteValue & 0xFF);

        return value;
    }

    public static byte[] fetchByteArrayFromBitArrayWithoutShifting(int bitArray[]) {
        byte[] value = new byte[(bitArray.length + 7) / 8];
        int byteTracker = 0;

        byte byteValue = 0;

        int counter = 0;
        for(int bitValue : bitArray) {
            byteValue = (byte) ((byteValue << 1) + (byte) bitValue);

            ++counter;
            if(counter % 8 == 0) {
                value[byteTracker++] = byteValue;
                byteValue = 0;
            }
        }
        for(int i = 0; i < (8 - (counter % 8)) % 8; ++i)
            byteValue <<= 1;
        if(counter % 8 != 0)
            value[byteTracker] = byteValue;

        return value;
    }

    public static byte[] fetchByteArrayFromBitArray(int bitArray[])
    {
        byte[] value = new byte[(bitArray.length + 7) / 8];
        int byteTracker = 0;

        byte byteValue = 0;

        int counter = (8 - (bitArray.length % 8)) % 8;
        for(int bitValue : bitArray) {
            byteValue = (byte) ((byteValue << 1) + (byte) bitValue);

            if((++counter) % 8 == 0) {
                value[byteTracker++] = byteValue;
                byteValue = 0;
            }
        }

        return value;
    }

    public static int[] fetchBitArrayFromByteArray(byte[] byteArray)
    {
        int[] result = new int[byteArray.length * 8];
        int counter = 0;

        for(int i = 0 ; i < byteArray.length ; i ++)
        {
            int[] bitArrayForByte = new int[8];
            byte b = byteArray[i];
            for(int j = 7; j >= 0; --j) {
                bitArrayForByte[j] = b & 0x01;
                b >>= 1;
            }

            for(int j = 0 ; j < bitArrayForByte.length ; j++)
            {
                result[counter ++] = bitArrayForByte[j];
            }
        }

        return result;
    }

    public static int fetchAbsoluteValueForInteger(int value,short size)
    {
        if(value == (1 << size) - 1 )
        {
            return -1;
        }

        return value;
    }

    public static short fetchAbsoluteValueForShort(short value,short size)
    {
        if(value == (short)((1 << size) - 1 ))
        {
            return -1;
        }

        return value;
    }

    public static long fetchAbsoluteValueForLong(long value,short size)
    {
        if(value == ((1L << size) - 1 ))
        {
            return -1;
        }

        return value;
    }

    public static boolean isIntegerVariableFitting(int value,short size)
    {
        if(value == -1)
            return true;

        if(size >= 32)
            return true;

        if((value >> size) != 0)
            return false;

        return true;
    }

    public static boolean isShortVariableFitting(short value,short size)
    {
        if(value == -1)
            return true;

        if(size >= 16)
            return true;

        if((value >> size) != 0)
            return false;

        return true;
    }

    public static boolean isLongVariableFitting(long value,short size)
    {
        if(value == -1)
            return true;

        if(size >= 64)
            return true;

        if((value >> size) != 0)
            return false;

        return true;
    }

    private static void printBitArray(int[] bits) {
        for(int i = 0; i < bits.length; ++i) {
            System.out.print(bits[i]);
            if(i != bits.length - 1)
                System.out.print(",");
        }
        System.out.println();
    }

    public static void setBitSetToStorage(int startIndex,int size,int[] value,int[] bitSetData)
    {
        int counter = 0;

        for(int i = startIndex;i < (startIndex + size); i++)
        {
            bitSetData[i] = value[counter];
            counter ++;
        }
    }

    public static int[] fetchBitArrayFromParentArray(int[] parentArray,int startIndex,int size)
    {
        int[] result = new int[size];
        int counter = 0;

        for(int i = startIndex ; i < ( startIndex + size ) ; i ++)
        {
            result[counter ++] = parentArray[i];
        }

        return result;
    }
}
