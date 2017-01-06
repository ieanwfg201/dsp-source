package com.kritter.utils.amazon_glacier_upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadResult;
import com.amazonaws.services.glacier.model.UploadMultipartPartRequest;
import com.amazonaws.services.glacier.model.UploadMultipartPartResult;
import com.amazonaws.util.BinaryUtils;



public class UploadToGlacier {   
    private static final Logger LOG = LogManager.getLogger(UploadToGlacier.class);
    // This example works for part sizes up to 1 GB.
    public static String partSize = "1048576"; // 1 MB.
    public static AmazonGlacierClient client;

    
    public static void main(String args[]) throws Exception{
        if(args.length == 5){
            UploadToGlacier.uploadtoglacier(args[0], args[1], args[2],args[3], args[4]);
        }else{
            System.out.println("########################################### INCORRECT USAGE ############################################################");
            System.out.println("com.kritter.utils.amazon_glacier_upload.UploadToGlacier <accessKey> <secretKey> <archiveFilePath> <vaultName> <endpoint>");
            System.out.println("##########################################      Example     ############################################################");
            System.out.println("com.kritter.utils.amazon_glacier_upload.UploadToGlacier myaccessKey mysecretKey /home/rohan/a.tgz myvault https://glacier.us-west-2.amazonaws.com/");
            System.out.println("#########################################################################################################################");
        }
    }
    public static String uploadtoglacier(String accessKey, String secretKey, String archiveFilePath, String vaultName, String endpoint) throws IOException {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        
        //ProfileCredentialsProvider credentials = new ProfileCredentialsProvider();

        client = new AmazonGlacierClient(awsCredentials);
        client.setEndpoint(endpoint);
       
        try {
            System.out.println("Uploading an archive.");
            String uploadId = initiateMultipartUpload(vaultName);
            String checksum = uploadParts(uploadId, archiveFilePath, vaultName);
            String archiveId = CompleteMultiPartUpload(uploadId, checksum, archiveFilePath, vaultName);
            System.out.println("Completed an archive. ArchiveId: " + archiveId);
            return archiveId;

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.err.println(e);
            return null;
        }

    }

    private static String initiateMultipartUpload(String vaultName) {
        // Initiate
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest()
        .withVaultName(vaultName)
        .withArchiveDescription("my archive " + (new Date()))
        .withPartSize(partSize);            

        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);

        System.out.println("ArchiveID: " + result.getUploadId());
        return result.getUploadId();
    }

    private static String uploadParts(String uploadId, String archiveFilePath, String vaultName) throws AmazonServiceException, NoSuchAlgorithmException, AmazonClientException, IOException {

        int filePosition = 0;
        long currentPosition = 0;
        byte[] buffer = new byte[Integer.valueOf(partSize)];
        List<byte[]> binaryChecksums = new LinkedList<byte[]>();

        File file = new File(archiveFilePath);
        FileInputStream fileToUpload = new FileInputStream(file);
        String contentRange;
        int read = 0;
        while (currentPosition < file.length())
        {
            read = fileToUpload.read(buffer, filePosition, buffer.length);
            if (read == -1) { break; }
            byte[] bytesRead = Arrays.copyOf(buffer, read);

            contentRange = String.format("bytes %s-%s/*", currentPosition, currentPosition + read - 1);
            String checksum = TreeHashGenerator.calculateTreeHash(new ByteArrayInputStream(bytesRead));
            byte[] binaryChecksum = BinaryUtils.fromHex(checksum);
            binaryChecksums.add(binaryChecksum);
            System.out.println(contentRange);

            //Upload part.
            UploadMultipartPartRequest partRequest = new UploadMultipartPartRequest()
            .withVaultName(vaultName)
            .withBody(new ByteArrayInputStream(bytesRead))
            .withChecksum(checksum)
            .withRange(contentRange)
            .withUploadId(uploadId);               

            UploadMultipartPartResult partResult = client.uploadMultipartPart(partRequest);
            System.out.println("Part uploaded, checksum: " + partResult.getChecksum());

            currentPosition = currentPosition + read;
        }
        fileToUpload.close();
        String checksum = TreeHashGenerator.calculateTreeHash(binaryChecksums);
        return checksum;
    }

    private static String CompleteMultiPartUpload(String uploadId, String checksum, String archiveFilePath, String vaultName) throws NoSuchAlgorithmException, IOException {

        File file = new File(archiveFilePath);

        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest()
        .withVaultName(vaultName)
        .withUploadId(uploadId)
        .withChecksum(checksum)
        .withArchiveSize(String.valueOf(file.length()));

        CompleteMultipartUploadResult compResult = client.completeMultipartUpload(compRequest);
        return compResult.getLocation();
    }
}
