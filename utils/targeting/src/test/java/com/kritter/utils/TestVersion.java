package com.kritter.utils;

import com.kritter.utils.entity.Version;
import com.kritter.utils.entity.VersionRange;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * To be Modified, the versions need to be inclusive both minor and major.
 * TODO
 */
public class TestVersion
{
   /* @Test
    public void testVersion()
    {
        Version minorVer = new Version("1.2.1");
        Version majorVer = new Version("2.2.1");

        // minor and major are bounded. Different request version cases
        assertEquals(true, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("1.2.1")));
        assertEquals(true, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("1.2.5")));
        assertEquals(true, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("1.2.1")));
        assertEquals(false, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("2.2.1")));
        assertEquals(false, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("incorrect_request_version")));
        assertEquals(false, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version(null)));
        assertEquals(false, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("1.2")));
        assertEquals(true, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("2.2")));
        assertEquals(false, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("2.2.1.4")));
        assertEquals(true, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("2.2.0.4")));
        assertEquals(false, (new VersionRange(minorVer, majorVer)).checkIfVersionIsWithin(new Version("0")));

        // minor is bounded. Major is not
        assertEquals(false, (new VersionRange(minorVer, new Version("dont_care_minor_version"))).checkIfVersionIsWithin(new Version("incorrect_request_version")));
        assertEquals(false, (new VersionRange(minorVer, new Version(null))).checkIfVersionIsWithin(new Version("incorrect_request_version")));
        assertEquals(false, (new VersionRange(minorVer, new Version("dont_care_minor_version"))).checkIfVersionIsWithin(new Version(null)));
        assertEquals(false, (new VersionRange(minorVer, new Version(null))).checkIfVersionIsWithin(new Version(null)));
        assertEquals(false, (new VersionRange(minorVer, new Version(null))).checkIfVersionIsWithin(new Version("0.2.2")));
        assertEquals(true, (new VersionRange(minorVer, new Version(null))).checkIfVersionIsWithin(new Version("1.5.2")));
        assertEquals(true, (new VersionRange(minorVer, new Version(null))).checkIfVersionIsWithin(new Version("3.2.2")));
        assertEquals(true, (new VersionRange(minorVer, new Version(null))).checkIfVersionIsWithin(new Version("1.3")));
        assertEquals(true, (new VersionRange(minorVer, new Version(null))).checkIfVersionIsWithin(new Version("1.2.1.1")));
        assertEquals(true, (new VersionRange(minorVer, new Version(null))).checkIfVersionIsWithin(new Version("1.3.1")));

        // major is bounded. Minor is not (correct, null and incorrect values for request version)
        assertEquals(false, (new VersionRange(new Version("dont_care_minor_version"), majorVer)).checkIfVersionIsWithin(new Version("incorrect_request_version")));
        assertEquals(false, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version("incorrect_request_version")));
        assertEquals(false, (new VersionRange(new Version("dont_care_minor_version"), majorVer)).checkIfVersionIsWithin(new Version(null)));
        assertEquals(false, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version(null)));
        assertEquals(true, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version("0.0.2")));
        assertEquals(true, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version("1.2.2")));
        assertEquals(false, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version("3.2.2")));
        assertEquals(false, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version("3.2.2.4")));
        assertEquals(false, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version("4.2")));
        assertEquals(true, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version("0.2")));
        assertEquals(true, (new VersionRange(new Version(null), majorVer)).checkIfVersionIsWithin(new Version("1.2")));

        // major and minor are unbounded (correct, null and incorrect values for request version)
        assertEquals(true, (new VersionRange(new Version("dont_care_minor_version"), new Version("dont_care_major_version"))).checkIfVersionIsWithin(new Version("incorrect_request_version")));
        assertEquals(true, (new VersionRange(new Version("dont_care_minor_version"), new Version("dont_care_major_version"))).checkIfVersionIsWithin(new Version(null)));
        assertEquals(true, (new VersionRange(new Version("dont_care_minor_version"), new Version(null))).checkIfVersionIsWithin(new Version("incorrect_request_version")));
        assertEquals(true, (new VersionRange(new Version("dont_care_minor_version"), new Version(null))).checkIfVersionIsWithin(new Version(null)));
        assertEquals(true, (new VersionRange(new Version(null), new Version("dont_care_minor_version"))).checkIfVersionIsWithin(new Version("incorrect_request_version")));
        assertEquals(true, (new VersionRange(new Version(null), new Version("dont_care_minor_version"))).checkIfVersionIsWithin(new Version(null)));
        assertEquals(true, (new VersionRange(new Version(null), new Version(null))).checkIfVersionIsWithin(new Version("dont_care_minor_version")));
        assertEquals(true, (new VersionRange(new Version(null), new Version(null))).checkIfVersionIsWithin(new Version(null)));
        assertEquals(true, (new VersionRange(new Version(null), new Version(null))).checkIfVersionIsWithin(new Version("1.2.2")));
        assertEquals(true, (new VersionRange(new Version(null), new Version(null))).checkIfVersionIsWithin(new Version("5.2.2")));
        assertEquals(true, (new VersionRange(new Version(null), new Version(null))).checkIfVersionIsWithin(new Version("0.2.2")));
    }
    */
}
