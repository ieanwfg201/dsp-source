package com.kritter.billing.dobilling.common.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.kritter.billing.loglineconverter.postimpression.PostImpressionConverter;
import com.kritter.billing.transaction.PerformTransaction;
import com.kritter.constants.MarketPlace;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;
import com.kritter.postimpression.thrift.struct.PostImpressionTerminationReason;
import com.kritter.billing.dbconnector.DBConnector;

public class DoBillingByFile {

    private static final Logger LOG = LoggerFactory.getLogger(DoBillingByFile.class);


    private void moveFiles(List<File> fileAbsPathArray, String destPath) throws IOException{
        for(File fromFile:fileAbsPathArray){
            if(fromFile.isFile() && fromFile.exists()){
                String fileName= fromFile.getName();
                File toFile = new File( destPath+System.getProperty("file.separator")+fileName);
                Files.move(fromFile, toFile);
            }
        }
    }
    private List<File> moveFilesNreturnDestHandle(String filePath,String[] fileNameArray, String destPath,int noOfFiles) throws IOException{
        int i=0;
        LinkedList<File> destFileList = new LinkedList<File>();
        for(String fileName:fileNameArray){
            File fromFile = new File(filePath+System.getProperty("file.separator")+fileName);
            if(fromFile.isFile() && fromFile.exists() && !fileName.startsWith(".")){
                File toFile = new File( destPath+System.getProperty("file.separator")+fileName);
                Files.move(fromFile, toFile);
                destFileList.add(toFile);
            }
            i++;
            if(noOfFiles>0 && i>=noOfFiles){
                break;
            }
                
        }
        return destFileList;
    }

    public void doBilling(String filePath,String[] fileNameArray, String inprocessDestination,
            String doneDestination, String failDestination,
            String dbtype,String dbhost, String dbport, String dbname, String dbuser, String dbpwd,int noOfFiles){
        if(filePath == null || fileNameArray == null || inprocessDestination == null){
            return;
        }
        boolean failed = false;
        int stage = 0;
        Connection con = null;
        List<File> inprocessFileList = null;
        try{
            inprocessFileList = moveFilesNreturnDestHandle(filePath,fileNameArray, inprocessDestination,-1);
            stage = 1;
            ArrayList<PostImpressionRequestResponse> pirrList = new ArrayList<PostImpressionRequestResponse>();
            PostImpressionConverter pic = new PostImpressionConverter();
            BufferedReader br = null;
            FileReader fr = null;
            for(File inProcessFile:inprocessFileList){
                try{
                    fr = new FileReader(inProcessFile);
                    br = new BufferedReader(fr);
                    String str = null;
                    while((str = br.readLine()) != null){
                        PostImpressionRequestResponse pirr = pic.convert(str);
                        if(pirr != null && pirr.getStatus() == PostImpressionTerminationReason.HEALTHY_REQUEST){
                            if(pirr.getEvent() == PostImpressionEvent.RENDER && pirr.getMarketplace_id() == MarketPlace.CPM.getCode()){
                                pirrList.add(pirr);
                            }else if(pirr.getEvent() == PostImpressionEvent.CLICK && pirr.getMarketplace_id() == MarketPlace.CPC.getCode()){
                                pirrList.add(pirr);
                            }else if(pirr.getEvent() == PostImpressionEvent.CONVERSION && pirr.getMarketplace_id() == MarketPlace.CPD.getCode()){
                                pirrList.add(pirr);
                            }else if(pirr.getEvent() == PostImpressionEvent.WIN_NOTIFICATION){
                                pirrList.add(pirr);
                            }else if(pirr.getEvent() == PostImpressionEvent.INT_EXCHANGE_WIN){
                                pirrList.add(pirr);
                            }else if(pirr.getEvent() == PostImpressionEvent.BEVENT_CSCWIN){
                                pirrList.add(pirr);
                            }else{
                                LOG.debug("NON BILLABLE EVENT: {} ",  pirr.toString());
                            }
                        }
                    }
                }catch(Exception fe){
                    failed = true;
                    LOG.error(fe.getMessage(), fe);
                }finally{
                    if(fr!=null){
                        fr.close();
                    }
                    if(br!=null){
                        br.close();
                    }
                }
            }
            stage = 2;
            con = DBConnector.getConnection(dbtype,dbhost, dbport, dbname, dbuser, dbpwd);
            PerformTransaction.performUpdates(con, pirrList);
            stage = 3;
            moveFiles(inprocessFileList, doneDestination);
            stage = 4;
        }catch(Exception e){
            failed = true;
            LOG.error(e.getMessage(),e);
        }finally{
            if(failed && stage > 0 && stage < 4){
                try {
                    moveFiles(inprocessFileList, failDestination);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    LOG.error(e.getMessage(),e);
                }
            }
            if(con != null){
                try {
                    con.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }
}
