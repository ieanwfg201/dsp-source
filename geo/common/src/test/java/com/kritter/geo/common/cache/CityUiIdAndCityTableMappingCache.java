package com.kritter.geo.common.cache;

import com.kritter.utils.dbconnector.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hamlin on 16-11-7.
 */
public class CityUiIdAndCityTableMappingCache extends MappingCache {

    /**
     * SELECT uc.id AS ui_city_id,uc.city_name AS uc_city_name,c.`city_name` AS c_city_name,uc.entity_id_set AS entity_id_set,c.`city_code` AS city_code FROM city AS c,(SELECT city_name,id,REPLACE(REPLACE(entity_id_set,'[',''),']','') AS entity_id_set FROM ui_targeting_city WHERE id <> -1) AS uc WHERE c.id = uc.entity_id_set;
     * <p>
     * SELECT uc.id AS ui_state_id,uc.state_name AS uc_state_name,c.`state_name` AS c_state_name,uc.entity_id_set AS entity_id_set,c.`state_code` AS state_code FROM state AS c,(SELECT state_name,id,REPLACE(REPLACE(entity_id_set,'[',''),']','') AS entity_id_set FROM ui_targeting_state WHERE id <> -1) AS uc WHERE c.id = uc.entity_id_set;
     * <p>
     * SELECT uc.id AS ui_country_id,uc.country_name AS uc_country_name,c.`country_name` AS c_country_name,uc.entity_id_set AS entity_id_set,c.`country_code` AS country_code FROM country AS c,(SELECT country_name,id,REPLACE(REPLACE(entity_id_set,'[',''),']','') AS entity_id_set FROM ui_targeting_country WHERE id <> -1) AS uc WHERE c.id = uc.entity_id_set;
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */


    private static Map<Integer, String> mappingCache = new HashMap<>();

    static {
        buildCache();
    }


    private static void buildCache() {
        try (Connection con = DBConnector.getConnection(dbtype, dbhost, dbport, dbname, dbuser, dbpwd)) {
            PreparedStatement prepareStatement = con.prepareStatement("SELECT uc.id AS ui_city_id,uc.city_name AS uc_city_name,c.`city_name` AS c_city_name,uc.entity_id_set AS entity_id_set,c.`city_code` AS city_code FROM city AS c,(SELECT city_name,id,REPLACE(REPLACE(entity_id_set,'[',''),']','') AS entity_id_set FROM ui_targeting_city WHERE id <> -1) AS uc WHERE c.id = uc.entity_id_set;");
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                mappingCache.put(resultSet.getInt("entity_id_set"), resultSet.getString("city_code"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

    }

    public static String getCityCode(Integer cityUiId) {
        return mappingCache.get(cityUiId);
    }
}
