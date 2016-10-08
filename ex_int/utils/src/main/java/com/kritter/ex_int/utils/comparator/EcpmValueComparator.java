package com.kritter.ex_int.utils.comparator;

import java.util.Comparator;

import com.kritter.entity.reqres.entity.ResponseAdInfo;

public class  EcpmValueComparator implements Comparator<ResponseAdInfo> {
    @Override
    public int compare(ResponseAdInfo responseAdInfoFirst, ResponseAdInfo responseAdInfoSecond) {
        if(responseAdInfoFirst.getEcpmValue() > responseAdInfoSecond.getEcpmValue())
            return -1;

        if(responseAdInfoFirst.getEcpmValue() < responseAdInfoSecond.getEcpmValue())
            return 1;

        return 0;
	}
}
