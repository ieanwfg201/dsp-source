package com.kritter.api.upload_to_cdn.everest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;


import com.kritter.api.upload_to_cdn.IUploadToCDN;

public class BulkUpload {
    private static boolean postToEvrest(File originalOutputFile, String destFileUuid){
        boolean cdn_upload_success = false;
        IUploadToCDN uploadTOCDN = new UploadToCDN();
        String url = "<exrest url>";
        HashMap<String, String> postParams =  new HashMap<String, String>();
        postParams.put("account", "<accountname>");
        postParams.put("j_security_username", "<account username>");
        postParams.put("j_security_password", "<account username>");
        cdn_upload_success = uploadTOCDN.upload(url, null, postParams, originalOutputFile, destFileUuid); 
        return cdn_upload_success ;
    }

    public static void main(String args[]) throws Exception{
        File f = new File("/home/rohan/m/a.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String str = null;
        while((str = br.readLine()) != null){
            String str1="/home/rohan/m/"+str;
            System.out.println(str1);
            String uuid = str.substring(str.lastIndexOf("/")+1);;
            postToEvrest(new File(str1), uuid);
        }
       // postToEvrest(new File("/home/rohan/m/public/banners/original/./0122000b-3d8b-bd01-4cc2-5bb6d8000003/0122000b-3d8b-bd01-4de3-141278000180.gif"), "0122000b-3d8b-bd01-4de3-141278000180.gif");
    }
}
