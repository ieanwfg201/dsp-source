package com.kritter.ex_int.utils.comparator;

import com.kritter.entity.reqres.entity.ResponseAdInfo;

import java.util.Comparator;

/**
 * Created by oneal on 2017/3/1.
 */
public class EcpmCTRComparator implements Comparator<ResponseAdInfo> {

    // TODO this value will be from campaign later

    private double ecpmWeight = 0.5;
    private double baseECPM;
    private double defaultCtr;

    public EcpmCTRComparator(double ecpmWeight, double baseECPM, double defaultCtr) {
        this.ecpmWeight = ecpmWeight;
        this.baseECPM = baseECPM;
        this.defaultCtr = defaultCtr;
    }

    /**
     * calculate the score for each ad.
     *
     * <blockquote>
     * <p>w * [relative eCPM] + ( 1 - w ) * [relative ctr]</p>
     * <p>[relative eCPM] = eCPM / max internal bid</p>
     * <p>[relative ctr] = ctr / expected ctr</p>
     * </blockquote>
     *
     * @param responseAdInfoFirst
     * @param responseAdInfoSecond
     * @return
     */
    @Override
    public int compare(ResponseAdInfo responseAdInfoFirst, ResponseAdInfo responseAdInfoSecond) {

        Double firstAdExpectedCtr = responseAdInfoFirst.getExpectedCtr();
        if (firstAdExpectedCtr == null || firstAdExpectedCtr == 0){
            firstAdExpectedCtr=defaultCtr;
        }
        Double secondAdExpectedCtr = responseAdInfoSecond.getExpectedCtr();
        if (secondAdExpectedCtr == null || secondAdExpectedCtr == 0){
            secondAdExpectedCtr=defaultCtr;
        }

        double scoreFirst = (responseAdInfoFirst.getEcpmValue() / this.baseECPM) * this.ecpmWeight
                + (responseAdInfoFirst.getCtrValue() / firstAdExpectedCtr) * ( 1 - this.ecpmWeight);

        double scoreSecond = (responseAdInfoSecond.getEcpmValue() / this.baseECPM) * this.ecpmWeight
                + (responseAdInfoSecond.getCtrValue() / secondAdExpectedCtr) * ( 1 - this.ecpmWeight);

        if(scoreFirst > scoreSecond)
            return -1;

        if(scoreFirst < scoreSecond)
            return 1;

        return 0;
    }
}
