package com.kritter.material_upload.uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;

import com.kritter.naterial_upload.youku.executor.YoukuUploadExecutor;
import com.kritter.utils.dbconnector.DBConnector;

import lombok.Getter;
import lombok.Setter;

public class MaterialUploader {
    
	@Getter@Setter
    private Properties properties = null;
    public void configure_logger(String conf_path){
        FileInputStream fi = null;
        try{
            File file = new File(conf_path+System.getProperty("file.separator")+"log4j.properties");
            fi = new FileInputStream(file);
            PropertyConfigurator.configure(fi);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(fi != null){
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void read_properties(String conf_path){
        FileInputStream fi = null;
        try{
            if(properties == null){
                properties = new Properties();
            }
            File file = new File(conf_path+System.getProperty("file.separator")+"material.properties");
            fi = new FileInputStream(file);
            properties.load(fi);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(fi != null){
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    public void materialupload(){
        String split[] =  properties.get("adxbasedexchanges_prefix").toString().split(",");
        Connection con = null;
        try{
        	con = DBConnector.getConnection(properties.get("dbtype").toString(),properties.get("dbhost").toString(), 
        		properties.get("dbport").toString(), properties.get("dbname").toString(), 
        		properties.get("dbuser").toString(), properties.get("dbpwd").toString());
        	for(String str:split){
        		if("youku".equals(str)){
        			System.out.println("YOUKU BEGIN");
        			YoukuUploadExecutor yue = new YoukuUploadExecutor();
        			yue.execute(properties, con);
        			System.out.println("YOUKU END");
        		}
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	if(con != null){
        		try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        }
    }
    
    
    
    public static void main(String args[]){
        if(args.length != 1){
            System.out.println("Incorrect Usage");
            System.exit(0);
        }
        MaterialUploader uploader = new MaterialUploader();
        //uploader.materialupload(args[0]);
        String confPath=args[0];
        //String confPath="/usr/share/kritter/material_upload/uploader/conf/current";
        uploader.configure_logger(confPath);
        uploader.read_properties(confPath);
        uploader.materialupload();

    }
}
