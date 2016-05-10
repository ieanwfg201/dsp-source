Important notes:
1. in pom Exclude logback jar , as that offends slf4j's StaticLoggerBinder class
2. use slf4j-api 1.7.5 version or higher, lower ones do not have some method LocationAwareLogger.debug method.
3. while coding secondary index, if secondary index value is null which means universal targeting, put in ISecondaryIndex set
   the corresponding instance of secondary index with value as null(does not matter), as isAllTargeted is set and it takes care of it.
4. In workflow, before assigning cache refresh to any timer thread, call refresh once, so that workflow is ready.

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

            <groupId>com.kritter.adserving</groupId>
            <artifactId>request</artifactId>

            <groupId>com.kritter.adserving</groupId>
            <artifactId>ad_shortlisting</artifactId>

            <groupId>com.kritter.adserving</groupId>
            <artifactId>creative_formatting</artifactId>

            <groupId>com.kritter.thrift</groupId>
            <artifactId>structs</artifactId>

Create database by name adserver (also mentioned in jndi in server.xml), then source data_model/sql/consolidated.sql to create all schemas required.

There may be more deeper dependencies they will be figured out for each module by running mvn install.

Since this is war file, take war file from target, and put into webapps folder of tomcat.

Inside server.xml of tomcat put

        <Context path="/pre" docBase="pre" debug="5" reloadable="true" crossContext="true">
            <Resource auth="Container" driverClassName="com.mysql.jdbc.Driver" maxActive="20" maxIdle="5" maxWait="1000"
                    name="jdbc/AdserverDatabase" password="pass123" type="javax.sql.DataSource"
                    url="jdbc:mysql://localhost/adserver?useUnicode=true&amp;characterEncoding=UTF-8&amp;characterSetResults=UTF-8"
                    username="root"/>
        </Context>

For configuration files for this war file put following.

CONF_DIR/adserving/workflow.xml
CONF_DIR/adserving/log4j.properties

These are mentioned in web.xml of this war file present inside WEB-INF folder.The entries should adhere to CONF_DIR value.
Also put all other xml files resources/beans/*.xml inside CONF_DIR/adserving/ folder.

Access application by using 

wget "http://localhost:8080/adserving?ua=Apple-iPhone/501.347&site-id=test_site_guid" --header='KRITTER_INVENTORY_SRC:2' --header='REMOTE_ADDR: 117.97.87.6' -O 1






