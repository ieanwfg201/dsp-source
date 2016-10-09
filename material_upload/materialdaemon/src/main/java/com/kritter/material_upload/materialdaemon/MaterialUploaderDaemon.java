package com.kritter.material_upload.materialdaemon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.kritter.material_upload.uploader.MaterialUploader;

public class MaterialUploaderDaemon {
    
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
    
    
    public void doUpload(String conf_path){
        configure_logger(conf_path);
        read_properties(conf_path);
        MaterialUploader mUploader = new MaterialUploader();
        mUploader.setProperties(this.properties);
        int sleep_interval = Integer.parseInt(properties.getProperty("sleepintervalinms"));
        while(true){
        	mUploader.materialupload();
            try {
                Thread.sleep(sleep_interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
    }
    
    public static void main(String args[]){
        if(args.length != 1){
            System.out.println("Incorrect Usage");
            System.exit(0);
        }
        MaterialUploaderDaemon uploadDaemon = new MaterialUploaderDaemon();
        uploadDaemon.doUpload(args[0]);
        //dobilling.doBilling("usr/share/kritter/material_upload/uploader/conf/current");
    }
}
