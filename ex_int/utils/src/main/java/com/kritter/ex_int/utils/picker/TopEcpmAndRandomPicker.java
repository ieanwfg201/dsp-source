package com.kritter.ex_int.utils.picker;

import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.ex_int.utils.comparator.EcpmValueComparator;

import java.util.*;

/**
 * Created by oneal on 2017/3/1.
 */
public class TopEcpmAndRandomPicker implements AdPicker {
    @Override
    public ResponseAdInfo pick(Set<ResponseAdInfo> responseAdInfoList, Random randomPicker) {

        Comparator<ResponseAdInfo> comparator = new EcpmValueComparator();

        //sort and pick the one with highest ecpm value.
        List<ResponseAdInfo> list = new ArrayList<ResponseAdInfo>();
        list.addAll(responseAdInfoList);
        Collections.sort(list, comparator);

        ResponseAdInfo responseAdInfoToUse = list.get(0);
        responseAdInfoToUse = RandomPicker.pickRandomlyOneOfTheResponseAdInfoWithHighestSameEcpmValues
                (responseAdInfoToUse,list, randomPicker);
        return responseAdInfoToUse;
    }
}
