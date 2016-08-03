package com.kritter.ex_int.utils.comparator.common;

public class ShortArrayToIntegerArray {
    public  static Integer[] fetchIntegerArrayFromShortArray(Short[] array)
    {
        if(null == array || array.length <= 0)
            return null;

        Integer[] dest = new Integer[array.length];
        for(int i=0;i<array.length;i++)
        {
            dest[i] = array[i].intValue();
        }

        return dest;
    }
}
