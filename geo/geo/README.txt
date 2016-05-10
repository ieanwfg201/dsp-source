The geo application is responsible to look for custom data (some data format 
that an agency provides) and for any other third party data such as maxmind.

All the common classes required for , 
1. building any database inmemory from some sql database and 
2. from reading third party datasource files (based on if file's timestamp
   has been changed or file has been modified since last read).

The logical flow for the entire application sequentially would be:
1. Read datasource files from any third party datasource in whatever format
   they are available.
2. Use each individual datasource and form following datsets in sequence as:
   a. key(integer_id, datsource_name) -> value(country_code and country_name) , last_updated.
   
   b. CSV form of each country data row as iprange(in long values, 
      e.g: [12312323,2311232323,country_integer_id_from_sql_store])
   
   c. Initialize and form CountryDetectionUsingCountryCodeClass, so that country_id can be looked
      up using just the country code.

   d. key(integer_id, country_id, datasource_name) -> value(operator_name,operator_organization), last_updated.
   
   d. CSV form of each operator data row as [ iprange(in long values),country_id,operator_id ] 
      e.g: [12312321323,2323123123123,country_integer_id_from_sql_store,operator_id_from_sql_store]


Note: Each third party datasource is individually read and processed in their separate modules like
      maxmind,someother paid source or any custom data the agency provides.
      The common module (geo) would keep responsibility of common utilities and classes required.
      To package for a different client, each packaging module could be written by means of pom 
      file, wherein what all third party datasources are required to be kept in sql database ,can
      be included and given out as part of dsp codebase for installation. 
