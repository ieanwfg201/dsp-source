package com.kritter.utils.amazon_s3_upload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.kritter.constants.VideoMimeTypes;

public class UploadToS3 {
    public static void upload(String existingBucketName,String accessKey, String secretKey,String keyName, String filePath) throws Exception {
        File file = new File(filePath);
        upload( existingBucketName, accessKey, secretKey, keyName, file);
    }
    
    private static void setContentType(String contentType,InitiateMultipartUploadRequest initRequest){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        initRequest.setObjectMetadata(objectMetadata);

    }
    public static void upload(String existingBucketName,String accessKey, String secretKey,String keyName, File file) throws Exception {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = new AmazonS3Client(awsCredentials);        

        // Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
        List<PartETag> partETags = new ArrayList<PartETag>();

        // Step 1: Initialize.
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(existingBucketName, keyName);
        String extension = FilenameUtils.getExtension(file.getName());
       // if(extension.equals(VideoMimeTypes.MPEG4.getExtension()))
        if(VideoMimeTypes.MPEG4.getExtension().contains(extension)){
        	setContentType(VideoMimeTypes.MPEG4.getMime(), initRequest);
        }else if(VideoMimeTypes.Flash.getExtension().contains(extension)){
        	setContentType(VideoMimeTypes.Flash.getMime(), initRequest);
        }else if(VideoMimeTypes.iPhoneIndex.getExtension().contains(extension)){
        	setContentType(VideoMimeTypes.iPhoneIndex.getMime(), initRequest);
        }else if(VideoMimeTypes.iPhoneSegment.getExtension().contains(extension)){
        	setContentType(VideoMimeTypes.iPhoneSegment.getMime(), initRequest);
        }else if(VideoMimeTypes.Mobile3GP.getExtension().contains(extension)){
        	setContentType(VideoMimeTypes.Mobile3GP.getMime(), initRequest);
        }else if(VideoMimeTypes.QuickTime.getExtension().contains(extension)){
        	setContentType(VideoMimeTypes.QuickTime.getMime(), initRequest);
        }else if(VideoMimeTypes.AVInterleave.getExtension().contains(extension)){
        	setContentType(VideoMimeTypes.AVInterleave.getMime(), initRequest);
        }else if(VideoMimeTypes.WindowsMedia.getExtension().contains(extension)){
        	setContentType(VideoMimeTypes.WindowsMedia.getMime(), initRequest);
        }
        InitiateMultipartUploadResult initResponse = 
                               s3Client.initiateMultipartUpload(initRequest);
        

        long contentLength = file.length();
        long partSize = 5242880; // Set part size to 5 MB.

        try {
            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Last part can be less than 5 MB. Adjust part size.
                partSize = Math.min(partSize, (contentLength - filePosition));
                
                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                    .withBucketName(existingBucketName).withKey(keyName)
                    .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                    .withFileOffset(filePosition)
                    .withFile(file)
                    .withPartSize(partSize);

                // Upload part and add response to our list.
                partETags.add(
                        s3Client.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new 
                         CompleteMultipartUploadRequest(
                                    existingBucketName, 
                                    keyName, 
                                    initResponse.getUploadId(), 
                                    partETags);
            
            s3Client.completeMultipartUpload(compRequest);
        } catch (Exception e) {
            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    existingBucketName, keyName, initResponse.getUploadId()));
        }
    }
    
    public static void main(String args[]) throws Exception{
        //UploadToS3.upload("dsppayload", "<accesskeyid>", "<access secret key>","img/AT_EN_168x28.jpg","/home/rohan/banners/AT_EN_168x28.jpg");
    }
}
