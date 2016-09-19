package com.kritter.thirdpartydata.loader.check;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import com.kritter.geo.common.utils.GeoDetectionUtils;
public class CheckThirdPartyData {
	public ConcurrentSkipListMap<BigInteger, CheckEntity> map=null;
	public void init(String filePath,String delimiter,int outIndex){
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            map=null;
            map = new ConcurrentSkipListMap<BigInteger, CheckEntity>();
            String str;
            while((str = br.readLine()) != null){
                String strSplit[] =  str.split(delimiter);
                if(strSplit.length>3){
                    try{
                        if (strSplit[0].contains(":")){
                            BigInteger startIp = GeoDetectionUtils.fetchBigIntValueForIPV6(strSplit[0]);
                            BigInteger endIp = GeoDetectionUtils.fetchBigIntValueForIPV6(strSplit[1]);
                            CheckEntity check = new CheckEntity();
                            check.setVal(strSplit[outIndex]);
                            check.setEndIp(endIp);
                            map.put(startIp, check);
                        }else{
                            // ipv4
                            long startIp = GeoDetectionUtils.fetchLongValueForIPV4(strSplit[0]);
                            long endIp = GeoDetectionUtils.fetchLongValueForIPV4(strSplit[1]);
                            CheckEntity check = new CheckEntity();
                            check.setVal(strSplit[outIndex]);
                            check.setEndIp(BigInteger.valueOf(endIp));
                            map.put(BigInteger.valueOf(startIp), check);
                        }

                    }catch(Exception e){
                    	e.printStackTrace();
                    }
                }
            }
        } catch (Exception ioe) {
        	ioe.printStackTrace();
        }finally{
            if(fr != null){
                try {
                    fr.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        }
	}
    public void getval(String ipAddress) {
    	System.out.print(ipAddress+" -> ");
        try{
            BigInteger ip;
            if (ipAddress.contains(":")){
                ip = GeoDetectionUtils.fetchBigIntValueForIPV6(ipAddress);
            }else{
                // ipv4
                ip = BigInteger.valueOf(GeoDetectionUtils.fetchLongValueForIPV4(ipAddress));
            }
            Map.Entry<BigInteger, CheckEntity> entry = map.floorEntry(ip);
            if (entry != null && ip.compareTo(entry.getValue().getEndIp()) <1) {
            	System.out.println(entry.getValue().getVal());
            	return;
            }
            System.out.println("NOT FOUND");
        }catch(Exception e){
            System.out.println("NOT FOUND");
            e.printStackTrace();
        }
    }

    public static void main(String arg[]){
    	String filePath="/home/rohan/madlocation/var/data/kritter/location/country/kritter_country_input.csv";
    	String delimiter="";
    	CheckThirdPartyData c = new CheckThirdPartyData();
    	c.init(filePath, delimiter,2);
    	System.out.println("CHECKING COUNTRY");
    	c.getval("0.10.216.34");
    	c.getval("59.108.49.35");
    	c.getval("180.166.210.30");
    	c.getval("219.137.150.25");
    	c.getval("58.252.192.100");
    	c.getval("219.135.127.25");
    	c.getval("218.13.181.149");
    	c.getval("61.144.207.225");
    	c.getval("59.36.28.160 ");
    	filePath="/home/rohan/madlocation/var/data/kritter/location/country/kritter_state_input.csv";
    	c.init(filePath, delimiter,4);
    	System.out.println("CHECKING STATE");
    	c.getval("0.10.216.34");
    	c.getval("59.108.49.35");
    	c.getval("180.166.210.30");
    	c.getval("219.137.150.25");
    	c.getval("58.252.192.100");
    	c.getval("219.135.127.25");
    	c.getval("218.13.181.149");
    	c.getval("61.144.207.225");
    	c.getval("59.36.28.160 ");
    	filePath="/home/rohan/madlocation/var/data/kritter/location/country/kritter_state_input.csv";
    	c.init(filePath, delimiter,6);
    	System.out.println("CHECKING CITY");
    	c.getval("0.10.216.34");
    	c.getval("59.108.49.35");
    	c.getval("180.166.210.30");
    	c.getval("219.137.150.25");
    	c.getval("58.252.192.100");
    	c.getval("219.135.127.25");
    	c.getval("218.13.181.149");
    	c.getval("61.144.207.225");
    	c.getval("59.36.28.160 ");
    	
    }
}
