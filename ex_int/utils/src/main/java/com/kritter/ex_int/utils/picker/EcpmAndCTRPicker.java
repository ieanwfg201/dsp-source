package com.kritter.ex_int.utils.picker;

import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.ex_int.utils.comparator.EcpmCTRComparator;

import java.util.*;

/**
 * Created by oneal on 2017/3/1.
 */
public class EcpmAndCTRPicker implements AdPicker {

    private Random randomPicker = new Random();

    private double ecpmWeight;

    public EcpmAndCTRPicker(double ecpmWeight) {
        this.ecpmWeight = ecpmWeight;
    }

    @Override
    public ResponseAdInfo pick(Set<ResponseAdInfo> responseAdInfoSet) {

        if (responseAdInfoSet.size() == 1) {
            return responseAdInfoSet.iterator().next();
        }

        double baseECPM = responseAdInfoSet.iterator().next().getEcpmValue();
        Comparator<ResponseAdInfo> comparator = new EcpmCTRComparator(this.ecpmWeight, baseECPM);
        List<ResponseAdInfo> list = new ArrayList<ResponseAdInfo>();
        list.addAll(responseAdInfoSet);
        Collections.sort(list, comparator);

        ResponseAdInfo responseAdInfoToUse = pickResponseAdWithTheSameScore(randomPicker, comparator, list);

        return responseAdInfoToUse;
    }

    private ResponseAdInfo pickResponseAdWithTheSameScore(Random randomPicker, Comparator<ResponseAdInfo> comparator, List<ResponseAdInfo> list) {
        int sameHighestScore = 0;
        ResponseAdInfo responseAdInfoToUse = list.get(0);
        for (ResponseAdInfo responseAdInfo : list) {
            if (comparator.compare(responseAdInfoToUse, responseAdInfo) == 0) {
                sameHighestScore++;
            } else {
                break;
            }
        }

        int index = randomPicker.nextInt(sameHighestScore);
        if(index >= 0)
            responseAdInfoToUse = list.get(index);
        return responseAdInfoToUse;
    }

}



