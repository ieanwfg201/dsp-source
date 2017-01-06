package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum VolumeNormalizationMode {
	None(0,"None"),
	AdVolumeAverageNormalizedtoContent(1,"AdVolumeAverageNormalizedtoContent"),
	AdVolumePeakNormalizedtoContent(2,"AdVolumePeakNormalizedtoContent"),
	AdLoudnessNormalizedtoContent(3,"AdLoudnessNormalizedtoContent"),
	CustomVolumeNormalization(4,"CustomVolumeNormalization");
    
    private int code;
    private String name;
    private static Map<Integer, VolumeNormalizationMode> map = new HashMap<Integer, VolumeNormalizationMode>();
    static {
        for (VolumeNormalizationMode val : VolumeNormalizationMode.values()) {
            map.put(val.code, val);
        }
    }
    private VolumeNormalizationMode(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static VolumeNormalizationMode getEnum(int i){
        return map.get(i);
    }
}
