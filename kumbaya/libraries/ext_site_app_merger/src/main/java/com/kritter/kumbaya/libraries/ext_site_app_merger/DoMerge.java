package com.kritter.kumbaya.libraries.ext_site_app_merger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.kritter.kumbaya.libraries.ext_site_app_merger.entity.DBActivityEnum;
import com.kritter.kumbaya.libraries.ext_site_app_merger.entity.SummaryEvent;
import com.kritter.utils.dbconnector.DBConnector;


public class DoMerge {

    private static final String delimiter = "";
    private static final int min_req = 10;
    private static HashMap<String, SummaryEvent> summaryMap = new HashMap<String, SummaryEvent>();
    private static FileStatus[] shellListStatus(FileSystem srcFs,
            FileStatus src) {
        if (!src.isDir()) {
            FileStatus[] files = { src };
            return files;
        }
        Path path = src.getPath();
        try {
            FileStatus[] files = srcFs.listStatus(path);
            return files;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static List ls(FileSystem srcFs,String pattern,boolean recursive) throws Exception{
        Path srcPath =  new Path(pattern);
        FileStatus[] srcs = null;
        List<String> fileList = new LinkedList<String>();
        try{
            srcs = srcFs.globStatus(srcPath);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(srcs != null){
            for(int i=0; i<srcs.length; i++) {
                ls(srcs[i], srcFs, false, fileList);
            }
        }
        return fileList;

    }
    private static void ls(FileStatus src, FileSystem srcFs, boolean recursive, List fileList) throws Exception {
        final FileStatus[] items = shellListStatus(srcFs, src);
        if (items == null) {
        } else {
            for (int i = 0; i < items.length; i++) {
                FileStatus stat = items[i];
                if (recursive && stat.isDir()) {
                    ls(stat,srcFs, recursive, fileList);
                }else if(!stat.isDir()){
                    fileList.add(stat.getPath().toString().replace("file:",""));
                }
            }
        }
    }

    public static void deserialize(String s) throws Exception{
        if(s != null){
            String ssplit[] = s.split(delimiter);
            if(ssplit.length>=8){
                if("".equals(ssplit[0].trim()) || !("".equals(ssplit[6].trim()) || ("-1".equals(ssplit[6].trim())) || ("0".equals(ssplit[6].trim()))) ){
                    return;
                }
                SummaryEvent summaryEvent = new SummaryEvent();

                summaryEvent.setSiteId(Integer.parseInt(ssplit[0].trim()));
                if("".equals(ssplit[1].trim())){
                    summaryEvent.setSupply_source_type(1);
                }else{
                    summaryEvent.setSupply_source_type(Integer.parseInt(ssplit[1].trim()));
                }
                summaryEvent.setExt_supply_url(ssplit[2]);
                summaryEvent.setExt_supply_id(ssplit[3]);
                summaryEvent.setExt_supply_name(ssplit[4]);
                summaryEvent.setExt_supply_domain(ssplit[5]);
                summaryEvent.setExt_supply_attr_internal_id(-1);
                if("".equals(ssplit[7].trim())){
                    summaryEvent.setReq(0);
                }else{
                    summaryEvent.setReq(Integer.parseInt(ssplit[7].trim()));
                }
                if(ssplit.length>8 && !"".equals(ssplit[8].trim())){
                    summaryEvent.setOsId(Integer.parseInt(ssplit[8].trim()));
                }
                /** Checking for app supply source */
                if(summaryEvent.getSupply_source_type() == 3){
                    summaryMap.put(summaryEvent.getSiteId()+delimiter+summaryEvent.getExt_supply_id()+delimiter+summaryEvent.getOsId(), summaryEvent);
                }else{
                    /*osid as Others==2 for non app*/
                    summaryMap.put(summaryEvent.getSiteId()+delimiter+summaryEvent.getExt_supply_id()+delimiter+2, summaryEvent);
                }
            }
        }
    }
    public static void readFromFile(String absoluteFilePath ) throws Exception{
        BufferedReader br = null;
        FileInputStream fr = null;
        GZIPInputStream gzipfile = null;
        Reader decoder = null;
        try{
            File f = new File(absoluteFilePath);
            fr = new FileInputStream(f);
            gzipfile = new GZIPInputStream(fr);
            decoder = new InputStreamReader(gzipfile);
            br = new BufferedReader(decoder);
            String str = null;
            while((str = br.readLine())!= null){
                if(!"".equals(str)){
                    deserialize(str);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(fr != null){
                fr.close();
            }
            if(gzipfile != null){
                gzipfile.close();
            }
            if(br != null){
                br.close();
            }
        }
    }
    public static void readFromPathPattern(String patternPath ) throws Exception{
        Configuration conf = new Configuration();
        FileSystem srcFs = FileSystem.get(conf);
        List<String> fileList = ls(srcFs, patternPath,true);
        for(String singlefilePath:fileList){
            readFromFile(singlefilePath );
        }
        srcFs.close();
    }
    public static void insertinext(Connection con,SummaryEvent summaryEvent,boolean approved){
        if(con == null){
            return;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement("insert into " +
                    "ext_supply_attr(site_inc_id,ext_supply_id,ext_supply_name,ext_supply_domain,req,supply_source_type,ext_supply_url,osId,approved)" +
                    " values(?,?,?,?,?,?,?,?,?)");
            pstmt.setInt(1, summaryEvent.getSiteId());
            pstmt.setString(2, summaryEvent.getExt_supply_id());
            pstmt.setString(3, summaryEvent.getExt_supply_name());
            //System.out.println(summaryEvent.getExt_supply_name());
            pstmt.setString(4, summaryEvent.getExt_supply_domain());
            pstmt.setInt(5, summaryEvent.getReq());
            pstmt.setInt(6, summaryEvent.getSupply_source_type());
            pstmt.setString(7, summaryEvent.getExt_supply_url());
            pstmt.setInt(8, summaryEvent.getOsId());
            pstmt.setBoolean(9, approved);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void updateinext(Connection con,SummaryEvent summaryEvent,
            Date date){
        if(con == null){
            return;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement("update " +
                    "ext_supply_attr set site_inc_id=?,ext_supply_id=?,ext_supply_name=?,ext_supply_domain=?,req=?" +
                    ",supply_source_type=?,ext_supply_url=?,last_modified=?,osId=? where id=?");
            pstmt.setInt(1, summaryEvent.getSiteId());
            pstmt.setString(2, summaryEvent.getExt_supply_id());
            pstmt.setString(3, summaryEvent.getExt_supply_name());
            pstmt.setString(4, summaryEvent.getExt_supply_domain());
            pstmt.setInt(5, summaryEvent.getReq());
            pstmt.setInt(6, summaryEvent.getSupply_source_type());
            pstmt.setString(7, summaryEvent.getExt_supply_url());
            pstmt.setTimestamp(8, new java.sql.Timestamp(date.getTime()));
            pstmt.setInt(9, summaryEvent.getOsId());
            pstmt.setInt(10, summaryEvent.getId());
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void insertOrUpdate(Connection con, boolean approved) throws Exception{
        if(con == null || summaryMap.size()<1){
            return;
        }
        boolean autoCommitMode = con.getAutoCommit();
        try{
            con.setAutoCommit(false);
            Date date = new Date();
            for(String key:summaryMap.keySet()){
                SummaryEvent summaryEvent = summaryMap.get(key);
                if(summaryEvent != null){
                    if(summaryEvent.getDbActivity() == DBActivityEnum.INSERT){
                        insertinext(con, summaryEvent, approved);
                    }else if(summaryEvent.getDbActivity() == DBActivityEnum.UPDATE){
                        updateinext(con, summaryEvent,date);
                    }
                }
            }
            con.commit();
        }catch(Exception e){
            e.printStackTrace();
            con.rollback();
            throw new Exception(e);
        }finally{
            con.setAutoCommit(autoCommitMode);
        }
    }

    public static void readFromDBAndMerge( String dbtype,String dbhost, String dbport, String dbname, String dbuser, String dbpwd, boolean approvedIn){
        Connection con = null;
        Statement stmnt = null;
        try{
            con = DBConnector.getConnection(dbtype,dbhost, dbport, dbname, dbuser, dbpwd);
            stmnt = con.createStatement();
            ResultSet rset = stmnt.executeQuery("select * from ext_supply_attr");
            while(rset.next()){
                int site_inc_id = rset.getInt("site_inc_id");
                String  ext_supply_id = rset.getString("ext_supply_id");
                int osId = rset.getInt("osId");
                String key = site_inc_id+delimiter+ext_supply_id+delimiter+osId;
                if(summaryMap.get(key) != null){
                    boolean approved = rset.getBoolean("approved");
                    boolean unapproved = rset.getBoolean("unapproved");
                    if(approved && !unapproved){
                        summaryMap.put(key, null);
                    }else{
                        int id = rset.getInt("id");
                        SummaryEvent s = summaryMap.get(key);
                        if(s !=null){
                            if(s.getReq()<min_req){
                                summaryMap.put(key,null);
                            }else{
                                s.setApproved(approved);
                                s.setUnapproved(unapproved);
                                s.setDbActivity(DBActivityEnum.UPDATE);
                                s.setId(id);
                            }
                        }
                    }

                }
            }
            insertOrUpdate(con, approvedIn);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(stmnt != null){
                try {
                    stmnt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(con != null){
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    
    public static void domerge( String dbtype,String dbhost, String dbport, String dbname, String dbuser, String dbpwd, String patternPath, boolean approved){
        try{
        readFromPathPattern(patternPath );
        readFromDBAndMerge(dbtype, dbhost, dbport, dbname, dbuser, dbpwd, approved);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String args[]){
        //domerge("MYSQL", "localhost", "3306", "kritter", "root", "password", "/home/rohan/testdata/kritter/manual/daily_externalsite.gz/part*");
        if(args.length != 8){
            System.out.println("InCorrect Usage ");
        }else{
            boolean approved = false;
                if("true".equals(args[7])){
                    approved=true;
                }
            domerge(args[0], args[1], args[2], args[3], args[4], args[5], args[6], approved);
        }
    }
}
