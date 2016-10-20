package com.kritter.billing.ad_budget.utils;

import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GeneralUtils {
    public static void configureLogger(String confPath) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(confPath));
            PropertyConfigurator.configure(fileInputStream);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Properties readProperties(String confPath) {
        FileInputStream fileInputStream = null;
        Properties properties = new Properties();
        try {
            fileInputStream = new FileInputStream(new File(confPath));
            properties.load(fileInputStream);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return properties;
    }
}
