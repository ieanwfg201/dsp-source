package com.kritter.constants;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains itunes category mapping to IAB categories.
 */
public class ITunesCategoryMappingToIAB
{

    public static String[] fetchIABCategoryArrayForITunesContentCategory(String iTunesContentCategory)
    {
        if(null == iTunesContentCategory)
            return null;

        return categoryMappings.get(iTunesContentCategory.toLowerCase());
    }

    private static final Map<String,String[]> categoryMappings = new HashMap<String,String[]>();

    static
    {

        categoryMappings.put("Books".toLowerCase(),new String[]{"IAB1","IAB1-1"});
        categoryMappings.put("Business".toLowerCase(),new String[]{"IAB3"});
        categoryMappings.put("Education".toLowerCase(),new String[]{"IAB5"});
        categoryMappings.put("Entertainment".toLowerCase(),new String[]{"IAB1"});
        categoryMappings.put("Finance".toLowerCase(),new String[]{"IAB13"});
        categoryMappings.put("Games".toLowerCase(),new String[]{"IAB9","IAB9-30"});
        categoryMappings.put("Healthcare and Fitness".toLowerCase(),new String[]{"IAB7"});
        categoryMappings.put("Lifestyle".toLowerCase(),new String[]{"IAB14"});
        categoryMappings.put("Medical".toLowerCase(),new String[]{"IAB7"});
        categoryMappings.put("Music".toLowerCase(),new String[]{"IAB1","IAB1-6"});
        categoryMappings.put("Navigation".toLowerCase(),new String[]{"IAB20"});
        categoryMappings.put("News".toLowerCase(),new String[]{"IAB12"});
        categoryMappings.put("Photography".toLowerCase(),new String[]{"IAB9","IAB9-23"});
        categoryMappings.put("Productivity".toLowerCase(),new String[]{"IAB3"});
        categoryMappings.put("Reference".toLowerCase(),new String[]{"IAB24"});
        categoryMappings.put("Social Networking".toLowerCase(),new String[]{"IAB24"});
        categoryMappings.put("Sports".toLowerCase(),new String[]{"IAB17"});
        categoryMappings.put("Travel".toLowerCase(),new String[]{"IAB20"});
        categoryMappings.put("Utilities".toLowerCase(),new String[]{"IAB3"});
        categoryMappings.put("Weather".toLowerCase(),new String[]{"IAB15","IAB15-10"});
    }
}
