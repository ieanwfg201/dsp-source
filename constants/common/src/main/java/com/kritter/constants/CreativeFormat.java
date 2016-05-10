package com.kritter.constants;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


public enum CreativeFormat
{
    TEXT(1, "Text", "Text Ad"),
    BANNER(2, "Banner" , "Banner Ad"),
    RICHMEDIA(3, "Rich Media", "Rich Media Ad"),
    VIDEO(4, "Video", "Video Ad"),
    Native(51, "Native", "Native Ad");

    @Getter private int code;
    @Getter private String label;
    @Getter private String description;

    private static Map<Integer, CreativeFormat> codeToTerminationReasonMapping;

    private CreativeFormat(int code, String label, String description)
    {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public static CreativeFormat getCreativeFormats(int i)
    {
        if(codeToTerminationReasonMapping == null)
            initMapping();
        return codeToTerminationReasonMapping.get(i);
    }

    private static void initMapping()
    {
        codeToTerminationReasonMapping = new HashMap<Integer, CreativeFormat>();
        for (CreativeFormat s : values())
            codeToTerminationReasonMapping.put(s.code, s);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("CreativeFormat");
        sb.append("{code=").append(code);
        sb.append(", label='").append(label).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
