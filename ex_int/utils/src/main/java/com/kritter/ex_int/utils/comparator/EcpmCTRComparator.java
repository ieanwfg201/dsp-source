package com.kritter.ex_int.utils.comparator;

import com.kritter.entity.reqres.entity.ResponseAdInfo;

import java.util.Comparator;

/**
 * Created by oneal on 2017/3/1.
 */
public class EcpmCTRComparator implements Comparator<ResponseAdInfo> {

    // TODO this value will be from campaign later
    private static double EXPECTED_CTR = 0.01;

    private double ecpmWeight = 0.5;
    private double baseECPM;

    public EcpmCTRComparator(double ecpmWeight, double baseECPM) {
        this.ecpmWeight = ecpmWeight;
        this.baseECPM = baseECPM;
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

        double scoreFirst = (responseAdInfoFirst.getEcpmValue() / responseAdInfoFirst.getHardBid()) * this.ecpmWeight
                + (responseAdInfoFirst.getCtrValue() / EXPECTED_CTR) * ( 1 - this.ecpmWeight);

        double scoreSecond = (responseAdInfoSecond.getEcpmValue() / responseAdInfoSecond.getHardBid()) * this.ecpmWeight
                + (responseAdInfoSecond.getCtrValue() / EXPECTED_CTR) * ( 1 - this.ecpmWeight);

        if(scoreFirst > scoreSecond)
            return -1;

        if(scoreFirst < scoreSecond)
            return 1;

        return 0;
    }
}
