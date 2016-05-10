Compile:
    mvn clean install

    There are dependencies on various modules, they need to be compiled first before this is compiled.
    list is:
                <groupId>com.kritter.utils</groupId>
                <artifactId>dbutils</artifactId>

                <groupId>com.kritter.abstraction</groupId>
                <artifactId>cache-abstraction</artifactId>

                <groupId>com.kritter.core</groupId>
                <artifactId>workflow</artifactId>

                <groupId>com.kritter.utils</groupId>
                <artifactId>common</artifactId>

                <groupId>com.kritter.common</groupId>
                <artifactId>demand</artifactId>

                <groupId>com.kritter.common</groupId>
                <artifactId>site</artifactId>

                <groupId>com.kritter</groupId>
                <artifactId>handset</artifactId>

                <groupId>com.kritter.location</groupId>
                <artifactId>detector</artifactId>

                <groupId>com.kritter.thrift</groupId>
                <artifactId>structs</artifactId>

Create database by name adserver (also mentioned in jndi in server.xml a.k.a postimpression.xml), then source data_model/sql/consolidated.sql to create all schemas required.

There may be more deeper dependencies they will be figured out for each module by running mvn install.

Since this is war file, take war file from target, and put into webapps folder of tomcat.

Inside server.xml a.k.a postimpression.xml of tomcat put

        <Context path="/post" docBase="post" debug="5" reloadable="true" crossContext="true">
            <Resource auth="Container" driverClassName="com.mysql.jdbc.Driver" maxActive="20" maxIdle="5" maxWait="1000"
                    name="jdbc/AdserverDatabase" password="pass123" type="javax.sql.DataSource"
                    url="jdbc:mysql://localhost/adserver?useUnicode=true&amp;characterEncoding=UTF-8&amp;characterSetResults=UTF-8"
                    username="root"/>
        </Context>

For configuration files for this war file put following.

The conf file can be changed via current link in conf


Access application by using http://localhost/post/clk , following events should be there.

        TRACKING_URL_FROM_THIRD_PARTY("/trk/"),
        THIRD_PARTY_CLICK_ALIAS_URL("/tclk/"),
        IN_HOUSE_CLICK("/clk/");
        IN_HOUSE_CSC("/csc/");
        IN_HOUSE_WIN_NOTIFICATION("/win/");
        TODO: add more event urls like video engagement etc.

---------------------------------------------------------------------------------------------------------------------------------------------------------
										NOTES
---------------------------------------------------------------------------------------------------------------------------------------------------------

1.
        //For fraud check logic in postimpression,check for deviceId first,
        //if not equal then check for manufacturer and model to be equal.
        //Reason being just in case new handset dataloading happens first
        //in postimpression we will have a new deviceid detected for the 
        //same user agent however the manufacturer and model id would be
        //the same, hence the postimpression can anytime in terms of new 
        //handset data loading, also in case of if adserving handset data
        //loading takes place first then its not an issue as in that case
        //also device id could be different(if postimpression has not 
        //loaded its handset data yet however the individual manufacturer
        //and model ids would be same if handset detected at all in 
        //postimpression server.)
        //In online system ,as we have right now,depending upon the 
        //frequency at which data loads takes place, we wait for two times
        //that frequency after putting files in postimpression server(if 
        //postimpression server is different from adserving.)Otherwise
        //in case of same server the synch issue couldbe very less or not 
        //at all.

2.
	* If uri version is changed as a result of adserving push of change in uri
     	* then we can implement the new uri parsing here and release postimpression
     	* application first.After which adserving push can happen so that uri parsing
     	* does not break.
------------------------------------------------------------------------------------------------------------------------------------------------------------
