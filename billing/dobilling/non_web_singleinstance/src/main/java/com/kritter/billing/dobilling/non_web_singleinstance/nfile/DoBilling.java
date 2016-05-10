package com.kritter.billing.dobilling.non_web_singleinstance.nfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.kritter.billing.dobilling.common.dir.DoBillingByDir;

public class DoBilling {
    
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
            File file = new File(conf_path+System.getProperty("file.separator")+"billing.properties");
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
    
    
    public void doBilling(String conf_path){
        configure_logger(conf_path);
        read_properties(conf_path);
        DoBillingByDir dbd = new DoBillingByDir();
        int sleep_interval = Integer.parseInt(properties.getProperty("sleep_interval_ms"));
        while(true){
            dbd.doBilling(properties.getProperty("dirAbsPath"), 
                properties.getProperty("inprocessDestination"), properties.getProperty("doneDestination"), 
                properties.getProperty("failDestination"), properties.getProperty("dbtype"), 
                properties.getProperty("dbhost"), properties.getProperty("dbport"), properties.getProperty("dbname"), 
                properties.getProperty("dbuser"), properties.getProperty("dbpwd"),Integer.parseInt(properties.getProperty("nofFiles")));
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
        DoBilling dobilling = new DoBilling();
        dobilling.doBilling(args[0]);
        //dobilling.doBilling("/usr/share/kritter/billing/dobilling/non_web_singleinstance/conf/current");
    }
}
