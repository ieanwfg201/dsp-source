Compile:
     mvn -e clean install -Pthrift-0.9 -Pproto-2.4.1

For region codes and their names (states),

http://www.maxmind.com/download/geoip/misc/region_codes.csv


To find state and city for a given ip address use GeoCityLegacy database available as CSV files.

Two files are available in the distribution
1.GeoIPCity-***-Blocks.csv
2.GeoIPCity-***-Location.csv

The first one gives location id against given ip number and the second one the data for that location id
However to find state (region) value above region_codes.csv files should be used.

To download complete list from maxmind is:
Geo-108 (CSV country)
Geo-124 (CSV isp)

and these two are optional:
--------------------------------
|Geo-134 (CSV state and city)  |
--------------------------------
|GeoIP2-Connection-Type - netspeed, 3g,4g,etc.|
--------------------------------

File paths for state city database :
 1. /var/data/kritter/location/country/maxmind_block_id_input.csv , this has blockid against ipranges, using which city-state info is looked up.
 2. /var/data/kritter/location/country/maxmind_state_city_input.csv, this has state city database info against blockid. Geo-134 from maxmind website.
 3. /var/data/kritter/location/country/maxmind_state_code_input.csv, this has country and state code with state name info, available from 
    http://www.maxmind.com/download/geoip/misc/region_codes.csv
 4. /var/data/kritter/location/country/maxmind_state_city.csv , this will have final detection data,iprange with ids which are detected.
 5. /var/data/kritter/location/country/maxmind_state_city_prep.csv, this is preparation file that is used in-between final database is constructed.
 6. /var/data/kritter/location/country/maxmind_country_input.csv , this is the input file from Geo-108 csv database,remove first two lines.
 7. /var/data/kritter/location/country/maxmind_isp_input.csv, this is the input file form Geo-124 csv database, remove first two lines.
 8. /var/data/kritter/location/country/maxmind_netspeed_input.mmdb
