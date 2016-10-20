package com.kritter.billing.ad_budget.input;

import com.google.common.io.Files;
import com.kritter.billing.loglineconverter.postimpression.PostImpressionConverter;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class InputReader {
    private final Logger logger;

    private final PostImpressionConverter postImpressionConverter;

    public InputReader(String loggerName, PostImpressionConverter postImpressionConverter) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.postImpressionConverter = postImpressionConverter;
    }

    public static List<File> moveFilesAndReturnDestHandle(String filePath, String[] fileNameArray, String destPath)
            throws IOException {
        LinkedList<File> destFileList = new LinkedList<File>();

        for(String fileName : fileNameArray) {
            File fromFile = new File(filePath + File.separator + fileName);
            if(fromFile.isFile() && fromFile.exists() && !fileName.startsWith(".")) {
                File toFile = new File(destPath + File.separator + fileName);
                Files.move(fromFile, toFile);
                destFileList.add(toFile);
            }
        }

        return destFileList;
    }

    public static void moveFilesFromSourceToDestDirectory(String sourceDirPath, String destDirPath) throws IOException {
        if(sourceDirPath == null) {
            throw new IOException("Source directory is null");
        }
        if(destDirPath == null) {
            throw new IOException("Destination directory is null");
        }

        File sourceDir = new File(sourceDirPath);
        if(!sourceDir.isDirectory()) {
            throw new IOException("Source path does not correspond to directory. Path : " + sourceDirPath);
        }

        String[] fileNameArray = sourceDir.list();
        moveFilesAndReturnDestHandle(sourceDirPath, fileNameArray, destDirPath);
    }

    public void readLogFile(File postImpressionLogFile,
                            List<PostImpressionRequestResponse> postImpressionRequestResponses) {
        if(postImpressionLogFile == null) {
            logger.error("Input file provided is null");
            throw new RuntimeException("Input file provided is null");
        }

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(postImpressionLogFile));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                PostImpressionRequestResponse postImpressionRequestResponse = null;
                try {
                    postImpressionRequestResponse = postImpressionConverter.convert(line);
                } catch (Exception e) {
                    // Does nothing, since no exception is thrown
                }
                postImpressionRequestResponses.add(postImpressionRequestResponse);
            }
        } catch (IOException ioe) {
            logger.error("IO Exception occurred while reading log file : {}, exception : {}",
                    postImpressionLogFile.getName(), ioe);
            throw new RuntimeException(ioe);
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException ioe) {
                logger.error("Exception closing the buffered reader : {}", ioe);
            }
        }

        logger.debug("Number of logs in {} = {}", postImpressionLogFile, postImpressionRequestResponses.size());
    }

    public void readLogsFromDir(String dirPath,
                                List<PostImpressionRequestResponse> postImpressionRequestResponses) {
        if(dirPath == null) {
            logger.error("null directory path passed");
            throw new RuntimeException("null directory path passed");
        }

        File directory = new File(dirPath);
        // Verify that the directory is indeed a directory
        if(!directory.isDirectory()) {
            logger.error("Path {} does not correspond to a directory.", dirPath);
            throw new RuntimeException("Path " + dirPath + " does not correspond to a directory.");
        }

        File[] fileList = directory.listFiles();
        if(fileList != null) {
            for (File file : fileList) {
                readLogFile(file, postImpressionRequestResponses);
            }
        }
    }
}
