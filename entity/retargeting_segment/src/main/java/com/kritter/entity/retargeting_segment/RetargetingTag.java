package com.kritter.entity.retargeting_segment;

public class RetargetingTag {

    public static String getSegemntTag(String url,int segmentid){
        String str = "<!-- Retargeting Pixel - DO NOT MODIFY -->"+
                "<script src=\""+url+"?seg="+segmentid+"\" type=\"text/javascript\"></script>"+
                "<!-- End of Retargeting Pixel -->";
        return str;
    }
    
}
