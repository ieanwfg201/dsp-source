package com.kritter.geo.common.test;

import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.geo.common.cache.CityUiIdAndCityTableMappingCache;
import com.kritter.geo.common.cache.CountryUiIdAndCountryTableMappingCache;
import com.kritter.geo.common.cache.StateUiIdAndStateTableMappingCache;
import com.kritter.geo.common.entity.City;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.SourceFileEntity;
import com.kritter.geo.common.entity.State;
import com.kritter.geo.common.entity.reader.CityDetectionCache;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.StateDetectionCache;
import com.kritter.geo.common.utils.HandleIp;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hamlin on 16-11-7.
 */
public class OptimadGeoTest {
    private static CityDetectionCache cityDetectionCache = null;
    private static StateDetectionCache stateDetectionCache = null;
    private static CountryDetectionCache countryDetectionCache = null;

    private static int cantFindCity = 0;
    private static int cantFindPrivince = 0;
    private static int cantFindCountry = 0;

    private static int codeErrorCity = 0;
    private static int codeErrorPrivince = 0;
    private static int codeErrorCountry = 0;

//    @Before
    public void init() throws InitializationException {
        Map<String, String> map = new HashMap<>();
        map.put("madhouse", "/var/data/kritter/location/country/kritter_city.csv");
        String[] strs = new String[]{"madhouse"};
        cityDetectionCache = new CityDetectionCache("loggerName", map, strs, 1800000l);

        Map<String, String> stateMap = new HashMap<>();
        stateMap.put("madhouse", "/var/data/kritter/location/country/kritter_state.csv");
        stateDetectionCache = new StateDetectionCache("loggerName", stateMap, strs, 1800000l);

        Map<String, String> countryMap = new HashMap<>();
        countryMap.put("madhouse", "/var/data/kritter/location/country/kritter_country.csv");
        countryDetectionCache = new CountryDetectionCache("loggerName", countryMap, strs, 1800000l);
    }

//    @Test
    public void compareTest() throws Exception {

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 20, 10l, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(200000));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/var/data/kritter/location/test/result.txt")))) {
            long taskCount = 0l;
            for (final SourceFileEntity sourceFileEntity : HandleIp.SOURCE_FILE_ENTITIES) {
                poolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!sourceFileEntity.getStartIP().equalsIgnoreCase("0.0.0.0") && !sourceFileEntity.getEndIP().equalsIgnoreCase("0.255.255.255")) {
                            try {
                                queryIpRange(sourceFileEntity, writer);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                taskCount++;
            }
            int queueSize;
            long completedTaskCount = 0l;
            while ((queueSize = poolExecutor.getQueue().size()) > 0 ||
                    (completedTaskCount = poolExecutor.getCompletedTaskCount()) != taskCount) {
                System.out.println("queueSize: " + queueSize);
                System.out.println("taskCount: " + taskCount + ", completedTaskCount: " + completedTaskCount);
                Thread.sleep(2000);
            }

            poolExecutor.shutdown();

            writer.append("can not find city number:" + cantFindCity);
            writer.newLine();
            writer.append("can not find privince number :" + cantFindPrivince);
            writer.newLine();
            writer.append("can not find country number :" + cantFindCountry);
            writer.newLine();
            writer.append("city code error number :" + codeErrorCity);
            writer.newLine();
            writer.append("privince code error number :" + codeErrorPrivince);
            writer.newLine();
            writer.append("country code error number :" + codeErrorCountry);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }


    public void queryIpRange(SourceFileEntity sourceFileEntity, BufferedWriter writer) throws Exception {
        String startIP = sourceFileEntity.getStartIP();
        String endIP = sourceFileEntity.getEndIP();


        // should be code
        String sourceFileEntityCode = sourceFileEntity.getCode();
        // 非中国地区直接返回
        if (!sourceFileEntityCode.substring(0, 4).equalsIgnoreCase("1156")) {
            return;
        }

        String cityCode = sourceFileEntityCode;
        String stateCode = cityCode.substring(0, 6) + "0000";
        String countryCode = stateCode.substring(0, 1) + "000000000";

        boolean positionToCity = false;
        boolean positionToState = false;
        boolean positionToCountry = false;

        if (!sourceFileEntityCode.substring(6, 10).equalsIgnoreCase("0000")) {
            positionToCity = true;
        } else if (!sourceFileEntityCode.substring(4, 6).equalsIgnoreCase("00")) {
            positionToState = true;
        } else {
            positionToCountry = true;
        }


        // 如果国家是1000000000，改为CN，因为数据库中保存的code是CN
        if (countryCode.equalsIgnoreCase("1000000000"))
            countryCode = "CN";


        // 判断ip合法性
        if (!validateIP(startIP) || !validateIP(endIP)) {
            System.out.println("ip范围不合法: start ip：" + startIP + ", end ip：" + endIP);
            return;
        }

        String[] startSplit = startIP.split("\\.");
        int startIP_one = Integer.parseInt(startSplit[0]);
        int startIP_two = Integer.parseInt(startSplit[1]);
        int startIP_three = Integer.parseInt(startSplit[2]);
        int startIP_four = Integer.parseInt(startSplit[3]);

        String[] endSplit = endIP.split("\\.");
        int endIP_one = Integer.parseInt(endSplit[0]);
        int endIP_two = Integer.parseInt(endSplit[1]);
        int endIP_three = Integer.parseInt(endSplit[2]);
        int endIP_four = Integer.parseInt(endSplit[3]);


        for (; startIP_one <= endIP_one; startIP_one++) {
            for (; startIP_two <= endIP_two; startIP_two++) {
                for (; startIP_three <= endIP_three; startIP_three++) {
                    for (; startIP_four <= endIP_four; startIP_four++) {
                        String ip = new StringBuilder().append(startIP_one + "." + startIP_two + "." + startIP_three + "." + startIP_four).toString();
                        String cityCodeByDB = null;
                        City city = this.cityDetectionCache.findCityForIpAddress(ip);
                        String stateCodeByDB = null;
                        State state = this.stateDetectionCache.findStateForIpAddress(ip);
                        String countryCodeByDB = null;
                        Country country = this.countryDetectionCache.findCountryForIpAddress(ip);
                        if (positionToCity) {
                            if (city == null) {
                                this.cantFindCity++;
                                writer.append(ip + ":can not find city");
                                writer.newLine();
                            } else {
                                cityCodeByDB = CityUiIdAndCityTableMappingCache.getCityCode(city.getCityId());
                                if (StringUtils.isEmpty(cityCodeByDB) || !cityCodeByDB.trim().equalsIgnoreCase(cityCode.trim())) {
                                    this.codeErrorCity++;
                                    writer.append(ip + ":city code error,query result:" + cityCodeByDB + ",should be:" + cityCode);
                                    writer.newLine();
                                }
                            }
                        }
                        if (positionToCity || positionToState) {
                            if (state == null) {
                                this.cantFindPrivince++;
                                writer.append(ip + ":can not find privince");
                                writer.newLine();
                            } else {
                                stateCodeByDB = StateUiIdAndStateTableMappingCache.getStateCode(state.getStateId());
                                if (StringUtils.isEmpty(stateCodeByDB) || !stateCodeByDB.trim().equalsIgnoreCase(stateCode.trim())) {
                                    this.codeErrorPrivince++;
                                    writer.append(ip + ":privince code error,query result:" + stateCodeByDB + ",should be:" + stateCode);
                                    writer.newLine();
                                }
                            }
                        }
                        if (positionToCity || positionToState || positionToCountry) {
                            if (country == null) {
                                this.cantFindCountry++;
                                writer.append(ip + ":can not find country");
                                writer.newLine();
                            } else {
                                countryCodeByDB = CountryUiIdAndCountryTableMappingCache.getCountryCode(country.getCountryInternalId());
                                if (StringUtils.isEmpty(countryCodeByDB) || !countryCodeByDB.trim().equalsIgnoreCase(countryCode.trim())) {
                                    this.codeErrorCountry++;
                                    writer.append(ip + ":country code error,query result:" + countryCodeByDB + ",should be:" + countryCode);
                                    writer.newLine();
                                }
                            }
                        }
                    }
                    writer.flush();
                    startIP_four = 0;
                }
                startIP_three = 0;
            }
            startIP_two = 0;
        }
        startIP_one = 0;
    }

    public boolean validateIP(String ip) {
        if (StringUtils.isNotEmpty(ip)) {
            ip = ip.replaceAll(" ", "");
            int pointNum = 0;
            int spaceNum = 0;
            for (int i = 0; i < ip.length(); i++) {
                if (ip.charAt(i) == '.') {
                    pointNum++;
                    spaceNum = 0;
                } else if (ip.charAt(i) < '0' && ip.charAt(i) > '9') {
                    return false;
                } else {
                    spaceNum++;
                    if (spaceNum > 3) {
                        return false;
                    }
                }
            }
            return pointNum == 3;
        }
        return false;
    }
}
