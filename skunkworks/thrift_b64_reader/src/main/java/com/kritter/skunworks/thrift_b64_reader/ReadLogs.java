package com.kritter.skunworks.thrift_b64_reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.util.List;
import java.util.LinkedList;


public class ReadLogs {
    
    private static void ls(FileStatus src, FileSystem srcFs, boolean recursive, List fileList) throws Exception {
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

    private static FileStatus[] shellListStatus(FileSystem srcFs,
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
                    e.printStackTrace();
        }
        return null;
    }
    private static List ls(FileSystem srcFs,String pattern,boolean recursive) throws Exception{
        Path srcPath =  new Path(pattern);
        FileStatus[] srcs = null;
        List<String> fileList = new LinkedList<String>();
        try{
            srcs = srcFs.globStatus(srcPath);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(srcs != null){
            for(int i=0; i<srcs.length; i++) {
                ls(srcs[i], srcFs, false, fileList);
            }
        }
        return fileList;

    }
   
    public static void deserialize(String s, String className) throws Exception{
        TDeserializer tde =  new TDeserializer(new TBinaryProtocol.Factory());
        Base64 decoder = new Base64(0);
        TBase t = (TBase) Class.forName(className).newInstance();
        tde.deserialize(t, decoder.decodeBase64(s.getBytes()));
        System.out.println(t);
        System.out.println("############################");
    }
    public static void deserializeFromFile(String absoluteFilePath , String className) throws Exception{
        BufferedReader br = null;
        FileReader fr = null;
        try{
            File f = new File(absoluteFilePath);
            fr = new FileReader(f);
            System.out.println("FILENAME ###:"+f.getName());
            br = new BufferedReader(fr);
            String str = null;
            int count =1;
            while((str = br.readLine())!= null){
                if(!"".equals(str)){
                    System.out.print(count+"## ");
                    deserialize(str, className);
                }
                count++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(fr != null){
                fr.close();
            }
            if(br != null){
                br.close();
            }
        }
    }
    public static void deserializeFromPathPattern(String filepath , String className) throws Exception{
        Configuration conf = new Configuration();
        FileSystem srcFs = FileSystem.get(conf);
        List<String> fileList = ls(srcFs, filepath,true);
        for(String singlefilePath:fileList){
            deserializeFromFile(singlefilePath , className);
        }
        srcFs.close();
    }

    public static void main(String args[]) throws Exception{
        if(args.length != 2){
            System.out.println("INCORRECT USAGE");
        }else{
            deserializeFromPathPattern(args[0], args[1]);
        }
    }

}
