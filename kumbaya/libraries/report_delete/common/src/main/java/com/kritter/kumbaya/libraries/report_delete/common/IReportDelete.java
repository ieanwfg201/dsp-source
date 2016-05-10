package com.kritter.kumbaya.libraries.report_delete.common;

public interface IReportDelete {
    void delete( String dbhost, String dbuser, String dbpwd,
            String dbname,  String dbport, String dbtype, String tablename, int hours) throws Exception;

    void delete( String dbhost, String dbuser, String dbpwd,
            String dbname,  String dbport, String dbtype, String tablename, String impression_time) throws Exception;
}
