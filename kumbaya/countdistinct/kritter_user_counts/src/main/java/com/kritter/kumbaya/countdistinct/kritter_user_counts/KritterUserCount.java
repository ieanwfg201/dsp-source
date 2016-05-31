package com.kritter.kumbaya.countdistinct.kritter_user_counts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.adserving.thrift.struct.AdservingRequestResponse;
import com.kritter.adserving.thrift.struct.Impression;
import com.kritter.kumbaya.countdistinct.kritter_user_counts.common.AdservingThriftLogConverter;
import com.kritter.kumbaya.countdistinct.dimension_based_counting.DimensionCounting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


public class KritterUserCount {
    private static final Logger LOG = LoggerFactory.getLogger(KritterUserCount.class);
    public void extractAndPopulateFromAdservingLogLine(String logLine, DimensionCounting dimensionCounting,
            String delimiter){
        try{
            AdservingRequestResponse arr = AdservingThriftLogConverter.convert(logLine);
            if(arr == null || arr.getImpressions() == null
                    || arr.getEndUserIdentificationObject() == null
                    || arr.getEndUserIdentificationObject().getUniqueUserId() == null){
                return;
            }
            for(Impression imp:arr.getImpressions()){
                dimensionCounting.addElement(imp.getAdv_inc_id()+delimiter+imp.getCampaignId()+delimiter+imp.getAdId(), arr.getEndUserIdentificationObject().getUniqueUserId());
            }
        }catch(Exception e){
            LOG.error("Error decoding LogLine : {}", logLine);
        }
    }


    private void ls(FileStatus src, FileSystem srcFs, boolean recursive, List<String> fileList) throws Exception {
        final FileStatus[] items = shellListStatus(srcFs, src);
        if (items == null) {
        } else {
            for (int i = 0; i < items.length; i++) {
                FileStatus stat = items[i];
                if (recursive && stat.isDir()) {
                    ls(stat,srcFs, recursive, fileList);
                }else if(!stat.isDir()){
                    fileList.add(stat.getPath().toString().replace("file:",""));
                }
            }
        }
    }


    private FileStatus[] shellListStatus(FileSystem srcFs,
            FileStatus src) {
        if (!src.isDir()) {
            FileStatus[] files = { src };
            return files;
        }
        Path path = src.getPath();
        try {
            FileStatus[] files = srcFs.listStatus(path);
            return files;
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        return null;
    }
    private List<String> ls(FileSystem srcFs,String pattern,boolean recursive) throws Exception{
        Path srcPath =  new Path(pattern);
        FileStatus[] srcs = null;
        List<String> fileList = new LinkedList<String>();
        try{
            srcs = srcFs.globStatus(srcPath);
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        }

        if(srcs != null){
            for(int i=0; i<srcs.length; i++) {
                ls(srcs[i], srcFs, false, fileList);
            }
        }
        return fileList;

    }

    public void deserializeFromFile(String absoluteFilePath , DimensionCounting dimensionCounting,
            String logType, String delimiter) throws Exception{
        BufferedReader br = null;
        FileReader fr = null;
        try{
            File f = new File(absoluteFilePath);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            String str = null;
            if("adserving".equals(logType)){
                while((str = br.readLine())!= null){
                    if(!"".equals(str)){

                        extractAndPopulateFromAdservingLogLine(str, dimensionCounting, delimiter);
                    }
                }
            }
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        }finally{
            if(fr != null){
                fr.close();
            }
            if(br != null){
                br.close();
            }
        }
    }

    public void deserializeFromPathPattern(String filepath,DimensionCounting dimensionCounting, 
            String logType, String delimiter) throws Exception{
        Configuration conf = new Configuration();
        FileSystem srcFs = FileSystem.get(conf);
        List<String> fileList = ls(srcFs, filepath,true);
        for(String singlefilePath:fileList){
            deserializeFromFile(singlefilePath ,  dimensionCounting, logType, delimiter);
        }
        srcFs.close();
    }
    public void writeToFile(String outputFilePath, DimensionCounting dimensionCounting, String delimiter,
            String processingTime){
        String p = processingTime+" 00:00:00";
        BufferedWriter bw = null;
        FileWriter fw= null;
        try{
            File f = new File(outputFilePath);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            dimensionCounting.printCardinalitiesInBufferedWriter(bw, delimiter, p);
            bw.flush();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        }finally{
            try{
                if(bw != null){
                    bw.close();
                }
                if(fw != null){
                    fw.close();
                }
            }catch(Exception io){
                LOG.error(io.getMessage(),io);
            }
        }
    }
    public void uniqueusercount(String inputFilePattern, String outputFilePath, String delimiter, 
            String logType, String processingTime) throws Exception{
        DimensionCounting dimensionCounting = new DimensionCounting();
        deserializeFromPathPattern(inputFilePattern, dimensionCounting, logType, delimiter);
        writeToFile(outputFilePath, dimensionCounting, delimiter, processingTime);
    }

    public static void main(String args[])  throws Exception{
        if(args.length == 5){
            KritterUserCount auu = new KritterUserCount();
            auu.uniqueusercount(args[0], args[1], args[2], args[3], args[4]);
        }else{
            System.out.println("Incorrect Usage");
            System.exit(1);
        }
    }
}
