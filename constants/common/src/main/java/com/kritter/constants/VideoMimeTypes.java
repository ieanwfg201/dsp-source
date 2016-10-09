package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VideoMimeTypes {

    Flash(1,"video/x-flv",".flv"),
    MPEG4(2,"video/mp4",".mp4"),
    iPhoneIndex(3,"application/x-mpegURL",".m3u8"),
    iPhoneSegment(4,"video/MP2T",".ts"),
    Mobile3GP(5,"video/3gpp",".3gp"),
    QuickTime(6,"video/quicktime",".mov"),
    AVInterleave(7,"video/x-msvideo",".avi"),
    WindowsMedia(8,"video/x-ms-wmv",".wmv");
    
    private int code;
    private String mime;
    private String extension;
    
    private static Map<Integer, VideoMimeTypes> map = new HashMap<Integer, VideoMimeTypes>();
    static {
        for (VideoMimeTypes val : VideoMimeTypes.values()) {
            map.put(val.code, val);
        }
    }
    private VideoMimeTypes(int code,String mime, String extension){
        this.code = code;
        this.mime = mime;
        this.extension = extension;
    }

    public String getMime(){
        return this.mime;
    }
    public String getExtension(){
        return this.extension;
    }
    public int getCode(){
        return this.code;
    }
    
    public static VideoMimeTypes getEnum(int i){
        return map.get(i);
    }
    public static Integer getCodeFromName(String s){
    	for(Integer i:map.keySet()){
    		VideoMimeTypes m = map.get(i);
    		if(m.getMime().equals(s)){
    			return i;
    		}
    	}
    	return null;
    }
}
