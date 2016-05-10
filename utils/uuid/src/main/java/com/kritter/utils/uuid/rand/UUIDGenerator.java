package com.kritter.utils.uuid.rand;

import com.kritter.utils.uuid.IUUIDGenerator;

import java.util.Random;
import java.util.UUID;

/**
 *         This class is a utility class for generating time based universally
 *         unique identifier string. The unique id can be utilised at many
 *         places to assign to any database entity or for generation of
 *         impression UIDs.
 * 
 *         This class is the version 1, where impression UIDs may be generated
 *         plainly without injecting any other information.
 * 
 *         The time based uuid has two parts, two long values
 * 
 *         1.mostSignificantLong
 * 
 *         2.leastSignificantLong
 * 
 *         mostSignificantLong contains following unsigned fields:
 * 
 *         0xFFFFFFFF00000000 time_low
 * 
 *         0x00000000FFFF0000 time_mid
 * 
 *         0x000000000000F000 version
 * 
 *         0x0000000000000FFF time_hi
 * 
 *         The least significant long consists of the following unsigned fields:
 * 
 *         0xC000000000000000 variant
 * 
 *         0x3FFF000000000000 clock_seq
 * 
 *         0x0000FFFFFFFFFFFF node
 * 
 *         Clock sequence and Node values are populated with a random number.
 * 
 *         Instead of using most significant 12 bits from timestamp long using
 *         random number 12 bits for more randomness, for now(as per the current
 *         time) the timestamp value is confined within 41 bits however we are
 *         giving space upto 48 bits.Since 12 bits used from timestamp were all
 *         zero so filling them up by random number.
 */

public class UUIDGenerator implements IUUIDGenerator{

	private static final long TIME_LOW = 0xFFFFFFFF00000000L;

	private static final long TIME_MID = 0x00000000FFFF0000L;

	private static final long VERSION = 0x000000000000F000L;

	private static final long TIME_HI = 0x0000000000000FFFL;

	private static final long VARIANT = 0xC000000000000000L;

	private static final long CLOCK_SEQ = 0x3FFF000000000000L;

	private static final long NODE = 0x0000FFFFFFFFFFFFL;

	private static final long UUID_VERSION = 1;

	/**
	 * these constants defines three separate parts of long timestamp.Before
	 * using current timestamp the timestamp value is broken into these three
	 * parts.
	 */
	private static final long TIME_32_LSB = 0x00000000FFFFFFFFL;

	private static final long TIME_16_MID = 0x0000FFFF00000000L;

	private static final long TIME_12_MSB = 0x0FFF000000000000L;

	/**
	 * Singleton random number generator. The seed value is also defined along
	 * with it.This seed has to be totally unique and very different from other
	 * seed whose class code is owned by a different server.
	 */

	private static long seedForRandomNumberGenerator = 123456789123456789L;

	private static Random randomNumberGenerator = new Random(
			seedForRandomNumberGenerator);

	/**
	 * This method would create a universally unique identifier.
	 */
	public UUID generateUniversallyUniqueIdentifier() {

		long currentTime = System.currentTimeMillis();

		/**
		 * Take lowest 32 bits first, shift left by 32 bits to get into right
		 * position then perform binary and operation.
		 */
		long timeLow = (((currentTime & TIME_32_LSB) << 32) & TIME_LOW);

		/**
		 * Take 16 bits of middle , right shift by 16 so that and operation with
		 * TIME_MID yields appropriate bits in position.
		 * */
		long timeMid = (((currentTime & TIME_16_MID) >> 16) & TIME_MID);

		/**
		 * version is 2 for time based uuid.
		 */
		long version = ((UUID_VERSION << 12) & VERSION);

		/**
		 * Calculate time high. Take 12 most significant bits right shift by 48
		 * bits so that and operation with TIME_HI yields appropriate bits in
		 * position.
		 * 
		 * New implementation : Instead of taking 12 bits of time use random
		 * number to fill these 12 bits
		 */

		long time12MSBReplacement = randomNumberGenerator.nextLong();

		// long timeHigh = (((currentTime & TIME_12_MSB) >> 48) & TIME_HI);
		long timeHigh = (((time12MSBReplacement & TIME_12_MSB) >> 48) & TIME_HI);

		/**
		 * Prepare a random long number and use it to populate clock sequence
		 * and node values. Keep variant as it is.
		 */

		long randomNumberLong = randomNumberGenerator.nextLong();

		long clockSequence = ((randomNumberLong << 48) & CLOCK_SEQ);

		long node = (randomNumberLong & NODE);

		/**
		 * Now construct most significant long and least significant long for
		 * the time based uuid.
		 */
		long mostSignificantLong = (timeLow | timeMid | version | timeHigh);

		long leastSignificantLong = ((VARIANT) | (clockSequence) | (node));

		return new UUID(leastSignificantLong, mostSignificantLong);
	}

	public static long extractTimeInLong(String uuidString) {

		UUID uuid = UUID.fromString(uuidString);

		long mostSignificantBits = uuid.getLeastSignificantBits();

		long lowSigBitsValue = ((mostSignificantBits & TIME_LOW) >>> 32);

		long midBitsValue = ((mostSignificantBits & TIME_MID) << 16);

		return  (lowSigBitsValue | midBitsValue);
	}
}