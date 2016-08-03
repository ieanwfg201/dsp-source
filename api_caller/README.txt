1. com.kritter.api_caller.core.api.Api interface defines methods that can be utilized by an implementation to query a database or remote web server.

2. For querying database , fetching result set , updating some table, or inserting data into table, use com.kritter.api_caller.core.api.DatabaseApi
   class, input provided for execution is the class DatabaseRequestEntity, which has db fields required to be set in a sql query, query is part of 
   this class. isQueryForModification to enable update query, columnNamesToRead for column names to read. DBTableColumnValueWithType contains column
   with its type, type is provided as integer code by using java.sql.Types class.

3. For querying a remote webserver, use class WebserviceApi, input is WebServiceRequestEntity, which is same as HttpRequest of http_client module,
   response is WebServiceResponseEntity which is same as HttpResponse of http_client module. The underlying http client is SynchronousHttpClient
   of http_client module, which works on simple tcp principles defined in Java, has been tested robustly for high load and does not lead to any
   connection leak or high socket usage, this api does not have ability to make https calls, that would require additional Oauth module.
   Also only synchronous calls can be made using this api implementation.

ApiPool is the provision for storing api instances in case we require to store different api instances against their signature names and use it
inside some application.
