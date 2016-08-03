package com.kritter.utils.edgecast_upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import sun.net.ftp.FtpLoginException;

public class UploadToEdgecast {
    private static Logger m_logger = Logger.getLogger(UploadToEdgecast.class);
    public static boolean upload(String ftpHostname, String ftpPort, String inputFile, 
            String username, String password ) throws Exception {
        File firstLocalFile = new File(inputFile);
        return upload(ftpHostname, ftpPort, firstLocalFile, username, password);
    }
    public static boolean upload(String ftpHostname, String ftpPort, File inputFile, 
            String username, String password ) throws Exception {
        FTPClient ftpClient = null;
        InputStream inputStream = null;
        try{
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHostname, Integer.parseInt(ftpPort));
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {

                System.out.println("FTP server refused connection.");
                throw new FtpLoginException("FTP server refused connection.");

            }
            boolean logged = ftpClient.login(username, password);
            if (!logged) {
                ftpClient.disconnect();
                System.out.println("Could not login to the server.");
                throw new FtpLoginException("Could not login to the server.");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            File firstLocalFile = inputFile;
            
            String firstRemoteFile = firstLocalFile.getName();
            inputStream = new FileInputStream(firstLocalFile);
 
            //System.out.println("Start uploading first file");
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
            if (done) {
            //    System.out.println("The first file is uploaded successfully.");
                return true;
            }
            return false;
        }catch(Exception e){
            //e.printStackTrace();
            m_logger.error(e.getMessage(),e);
            return false;
        }finally{
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (Exception e) {
                m_logger.error(e.getMessage(),e);
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        
    }
    
    public static void main(String args[]) throws Exception{
        //UploadToEdgecast.upload("host", "port", "/home/rohan/banners/AT_EN_168x28.jpg", 
        //        "username", "password");
    }
}
