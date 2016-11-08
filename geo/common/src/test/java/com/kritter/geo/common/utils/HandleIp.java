package com.kritter.geo.common.utils;

import com.kritter.geo.common.entity.*;

import java.io.*;
import java.util.*;

/**
 * ip
 * Created by hamlin on 16-10-21.
 */
@SuppressWarnings("StringBufferReplaceableByString")
public class HandleIp {
    private static final String CITY_INPUT_DATA_DELIMITER = String.valueOf((char) 1);
    // 读入字典文件，并用code作为key，创建map
    public static Map<String, WordBookEntity> WORD_BOOK_ENTITY_MAP = getWordBookMap("/var/data/kritter/location/test/wordbook.txt");
    public static List<SourceFileEntity> SOURCE_FILE_ENTITIES = new ArrayList<>();

    static {
        // 读源文件
        readIPRangResource();
    }


    public static void main(String[] args) throws IOException {
        // 保存对象
        List<KritterCityInput> kritterCityInputList = new ArrayList<KritterCityInput>();
        List<KritterProvinceInput> kritterProvinceInputList = new ArrayList<KritterProvinceInput>();
        List<KritterCountryInput> kritterCountryInputList = new ArrayList<KritterCountryInput>();

        for (SourceFileEntity sourceFileEntity : SOURCE_FILE_ENTITIES) {
            String startIP = sourceFileEntity.getStartIP();
            String endIP = sourceFileEntity.getEndIP();
            String cityCode = sourceFileEntity.getCode();
            WordBookEntity bookEntity = WORD_BOOK_ENTITY_MAP.get(cityCode);
            String cityName = bookEntity.getCity();
            String provinceName = bookEntity.getProvince();
            String provinceCode = cityCode.substring(0, 6) + "0000";

            if (!provinceName.equals("全球") && (cityName.contains("市") || !cityCode.equalsIgnoreCase(provinceCode))) {
                kritterCityInputList.add(new KritterCityInput(startIP, endIP, "CN", provinceCode, provinceName, cityCode, cityName));
            }
            if (!provinceName.equals("全球") && !provinceName.contains("中国")) {
                kritterProvinceInputList.add(new KritterProvinceInput(startIP, endIP, "CN", provinceCode, provinceName));
            }


            if (!"全球".equals(provinceName)) {
                kritterCountryInputList.add(new KritterCountryInput(startIP, endIP, "CN", "china"));
            }


        }
        // kritter_city.csv          → 18615297,18620414,27
        // kritter_city_input.csv    → 223.246.128.1223.246.254.254CN0015安徽00150015宣城
        // kritter_country.csv       → 1634566272,1634566398,1
        // kritter_country_input.csv → 254.254.228.1254.254.228.127CNChina
        // kritter_state.csv         → 1634566272,1634566398,5
        // kritter_state_input.csv   → 254.254.228.1254.254.228.127CN0010河南
        // 写到文件
        BufferedWriter cityWriter = new BufferedWriter(new FileWriter(new File("/var/data/kritter/location/test/kritter_city_input.csv")));
        for (KritterCityInput input : kritterCityInputList) {
            cityWriter.write(new StringBuilder().append(input.getStartIP()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getEndIP()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getCountry()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getProvinceCode()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getProvinceName()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getCityCode()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getCityName()).toString());
            cityWriter.newLine();
        }
        cityWriter.flush();
        cityWriter.close();


        BufferedWriter provinceWriter = new BufferedWriter(new FileWriter(new File("/var/data/kritter/location/test/kritter_state_input.csv")));
        for (KritterProvinceInput input : kritterProvinceInputList) {
            provinceWriter.write(new StringBuilder().append(input.getStartIP()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getEndIP()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getCountry()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getProvinceCode()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getProvinceName()).toString());
            provinceWriter.newLine();
        }
        provinceWriter.flush();
        provinceWriter.close();


        BufferedWriter countryWriter = new BufferedWriter(new FileWriter(new File("/var/data/kritter/location/test/kritter_country_input.csv")));
        for (KritterCountryInput input : kritterCountryInputList) {
            countryWriter.write(new StringBuilder().append(input.getStartIP()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getEndIP()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getCountryCode()).append(CITY_INPUT_DATA_DELIMITER)
                    .append(input.getCountryName()).toString());
            countryWriter.newLine();
        }
        countryWriter.flush();
        countryWriter.close();
        //5，将对象转需要的格式，写到硬盘
    }

    private static void readIPRangResource() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/var/data/kritter/location/test/superadmin_20151226.csv"))) {
            String line;
            while (null != (line = reader.readLine())) {
                String[] split = line.split(",");
                SOURCE_FILE_ENTITIES.add(new SourceFileEntity(split[0], split[1], split[2]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    private static Map<String, WordBookEntity> getWordBookMap(String path) {
        Map<String, WordBookEntity> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while (null != (line = reader.readLine())) {
                if (Objects.equals(line.trim(), ""))
                    continue;
                String[] split = line.split(",");
                map.put(split[2], new WordBookEntity(split[0], split[1], split[2]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

        return map;
    }
}