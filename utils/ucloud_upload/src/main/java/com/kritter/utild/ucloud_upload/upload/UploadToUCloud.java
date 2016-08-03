package com.kritter.utild.ucloud_upload.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.Header;
import org.apache.log4j.Logger;

import com.kritter.utild.ucloud_upload.ufile.UFileClient;
import com.kritter.utild.ucloud_upload.ufile.UFileRequest;
import com.kritter.utild.ucloud_upload.ufile.UFileResponse;
import com.kritter.utild.ucloud_upload.ufile.sender.PutSender;



public class UploadToUCloud {
    private static Logger m_logger = Logger.getLogger(UploadToUCloud.class);
    public static void main(String args[]) {
        //UploadToUCloud.uploadToUCloud(ucloudPublicKey, ucloudPrivateKey, proxySuffix,
        //        downloadProxySuffix, file, bucketName);
        
    }
    public static boolean uploadToUCloud(String ucloudPublicKey,String ucloudPrivateKey, String proxySuffix,
            String downloadProxySuffix, String absoluteFilePath, String bucketName) {
        return uploadToUCloud(ucloudPublicKey, ucloudPrivateKey, proxySuffix, downloadProxySuffix, 
                new File(absoluteFilePath), bucketName);
    }

    public static boolean uploadToUCloud(String ucloudPublicKey,String ucloudPrivateKey, String proxySuffix,
            String downloadProxySuffix, File file, String bucketName) {
        UFileClient ufileClient = null;
        try {
            String key = file.getName();

            // String filePath = "";
            String filePath = file.getAbsolutePath();
            UFileRequest request = new UFileRequest();
            request.setBucketName(bucketName);
            request.setKey(key);
            request.setFilePath(filePath);

            m_logger.debug("PutFile BEGIN ...");
            ufileClient = new UFileClient();
            ufileClient.loadConfig(ucloudPublicKey, ucloudPrivateKey, proxySuffix, downloadProxySuffix);
            putFile(ufileClient, request);
            m_logger.debug("PutFile END ...\n\n");
            return true;
        }catch(Exception e){
            m_logger.error(e.getMessage(),e);
            return false;
        }finally {
            ufileClient.shutdown();
        }



    }

    private static void putFile(UFileClient ufileClient, UFileRequest request) {
        PutSender sender = new PutSender();
        sender.makeAuth(ufileClient, request);

        UFileResponse response = sender.send(ufileClient, request);
        if (response != null) {

            m_logger.debug("status line: " + response.getStatusLine());

            Header[] headers = response.getHeaders();
            for (int i = 0; i < headers.length; i++) {
                m_logger.debug("header " + headers[i].getName() + " : " + headers[i].getValue());
            }

            m_logger.debug("body length: " + response.getContentLength());

            InputStream inputStream = response.getContent();
            if (inputStream != null) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String s = "";
                    while ((s = reader.readLine()) != null) {
                        m_logger.debug(s);
                    }

                } catch (IOException e) {
                    m_logger.error(e.getMessage(),e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            m_logger.error(e.getMessage(),e);
                        }
                    }
                }
            }
        }
    }


}
