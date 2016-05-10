package com.kritter.kumbaya.libraries.data_loader;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.util.List;
import java.util.LinkedList;

import com.kritter.utils.dbconnector.DBConnector;



public class LoadDataMonthly {
    private static Logger m_logger = Logger.getLogger(LoadDataMonthly.class);

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
                    m_logger.error("Exception in shellListStatus: Could not list "+path.toString(),e);
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
            //m_logger.error("srcFs.globStatus(srcPath) ");
        }

        if(srcs != null){
            for(int i=0; i<srcs.length; i++) {
                ls(srcs[i], srcFs, false, fileList);
            }
        }
        return fileList;

    }
    
    public void loadData(String filepath, String tablename, String delimiter, String dbhost, String dbuser, String dbpwd, 
            String dbname,  String dbport, String dbtype, String processing_time) throws Exception{
        
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = DBConnector.getConnection(dbtype, dbhost,  dbport, dbname,  dbuser,  dbpwd);
            boolean autoCommit  = conn.getAutoCommit();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            if((null != processing_time) && (!"".equals(processing_time))){
                System.out.println("delete from "+tablename+" where processing_time = '"+processing_time+"'");
                stmt.execute("delete from "+tablename+" where processing_time = '"+processing_time+"'");
            }
            Configuration conf = new Configuration();
            FileSystem srcFs = FileSystem.get(conf);
            List<String> fileList = ls(srcFs, filepath,true);
            for(String singlefilePath:fileList){
                 System.out.println("LOAD DATA LOCAL INFILE '"+singlefilePath+"' INTO TABLE "+tablename+" FIELDS TERMINATED BY '"+delimiter+"' ENCLOSED BY '\"' ESCAPED BY '\\\\'");
                 stmt.execute("LOAD DATA LOCAL INFILE '"+singlefilePath+"' INTO TABLE "+tablename+" FIELDS TERMINATED BY '"+delimiter+"' ENCLOSED BY '\"' ESCAPED BY '\\\\'");
            }
            conn.commit();
            conn.setAutoCommit(autoCommit);
        }catch(Exception e){
            e.printStackTrace();
            m_logger.error(e.getMessage(), e);
            conn.rollback();
            throw e;
        }finally{
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }
        
    }
    
    public static void main(String args[]) throws Exception{
        if(args.length == 11){
            LoadDataMonthly ld  = new LoadDataMonthly();
            ld.loadData(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]+" "+args[10]);
        }else{
            System.out.println("############## ERROR in USAGE #############");
            System.out.println(args.length);
            System.out.println(args[args.length - 1]);
            System.exit(1);
            
        }
    }

}
