1. For country data loading from any third party datasource, the required format is:
   startip<CTRL-A>endip<CTRL-A>countrycode<CTRL-A>countryname 

2. For ISP data loading from any third party datasource, the required format is:
   startip<CTRL-A>endip<CTRL-A>ispname

   Note: entries to ui_targeting_isp are populated via isp_mappings table, so ispname 
         available from above datasource needs to be mapped using isp_mappings table.

3. State and City database loading using third party datasource, the required format is:
   startip<CTRL-A>endip<CTRL-A>countrycode<CTRL-A>statecode<CTRL-A>statename<CTRL-A>citycode<CTRL-A>cityname
