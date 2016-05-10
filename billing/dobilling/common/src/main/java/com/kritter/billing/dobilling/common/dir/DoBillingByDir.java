package com.kritter.billing.dobilling.common.dir;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.billing.dobilling.common.file.DoBillingByFile;

public class DoBillingByDir {
    private static final Logger LOG = LoggerFactory.getLogger(DoBillingByDir.class);
    
    public void doBilling(String dirAbsPath, String inprocessDestination,
            String doneDestination, String failDestination,
            String dbtype,String dbhost, String dbport, String dbname, String dbuser, String dbpwd, int nofFiles){
        if(dirAbsPath == null){
            return;
        }
        File file = new File(dirAbsPath);
        if(file.isDirectory()){
            String[] fileList = file.list();
            int len = 0;
            if(fileList != null && (len = fileList.length) > 0){
                DoBillingByFile dbf = new DoBillingByFile();
                int tmplen = len;
                if(nofFiles < len){
                    tmplen = nofFiles;
                }
                dbf.doBilling(dirAbsPath, fileList, inprocessDestination, doneDestination, 
                        failDestination, dbtype, dbhost, dbport, dbname, dbuser, dbpwd,tmplen);
            }
        }
    }
    
    public static void main(String args[]){
        /*DoBillingByDir dbd = new DoBillingByDir();
        dbd.doBilling("/home/rohan/testdata/kritter/forbilling/input", "/home/rohan/testdata/kritter/forbilling/inprocess", 
                "/home/rohan/testdata/kritter/forbilling/done", "failed", "MYSQL", "localhost", "3306", "kritter", "root", "password", 1);
                */
    }
}
