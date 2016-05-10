package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum CreativeMacroQuote {

    None(0,""),
    SingleQuoted(1,"'"),
    DoubleQuoted(2,"\"");
    
    private int code;
    private String name;
    private static Map<Integer, CreativeMacroQuote> map = new HashMap<Integer, CreativeMacroQuote>();
    static {
        for (CreativeMacroQuote val : CreativeMacroQuote.values()) {
            map.put(val.code, val);
        }
    }
    private CreativeMacroQuote(int code,String name){
        this.code = code;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCode(){
        return this.code;
    }
    
    public static CreativeMacroQuote getEnum(int i){
        return map.get(i);
    }
}
