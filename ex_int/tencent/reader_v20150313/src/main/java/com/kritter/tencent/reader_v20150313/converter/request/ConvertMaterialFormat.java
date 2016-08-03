package com.kritter.tencent.reader_v20150313.converter.request;

import RTB.Tencent.Request.Impression.MaterialFormat;

public class ConvertMaterialFormat {
/**
        message MaterialFormat {
            optional uint32 width = 1; //Width of ad space, txt type represents the number of Chinese character.
            optional uint32 height = 2; //Height of ad space, which is 0 if the type is txt.
            optional string mimes = 3; //Display type which is allowed by material. All lowercase. Please separate multiple types with “,”. E.g. “swf,jgp,txt”.
        }
 */
    public static void convert(MaterialFormat materialFormat){
        /**
         * TODO - Figure out material format
         */
    }
}
