package com.kritter.kumbaya.countdistinct.hyperloglogplusplus;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.kritter.kumbaya.countdistinct.common.IKritterCountDistinct;

import lombok.Getter;
import lombok.Setter;

public class HyperLogLogPlusPlus implements IKritterCountDistinct {
    @Getter@Setter
    private HyperLogLogPlus hll = null;
    @Getter@Setter
    private int precision = 14;
    @Getter@Setter
    private int sparsePrecision = 25;
    
    public HyperLogLogPlusPlus(Integer p, Integer sp){
        if(p != null){
            precision = p;
        }
        if(sp != null){
            sparsePrecision = sp;
        }
        init();
    }
    @Override
    public void init() {
        if(hll == null){
            hll = new HyperLogLogPlus(getPrecision(),getSparsePrecision());
        }
    }

    @Override
    public void addElement(String element) {
        hll.offer(element);
        
    }

    @Override
    public long getCount() {
        return hll.cardinality();
    }
    
    /*public static void main(String args[]){
        HyperLogLogPlusPlus hllpp = new HyperLogLogPlusPlus(null, null);
        long a = System.currentTimeMillis();
        int count=1000000000;
        for(int i=0;i<count;i++){
            hllpp.addElement("wd2qwd"+i);
            
        }
        long estimate = hllpp.getCount();
        double se = count * (1.04 / Math.sqrt(Math.pow(2, 14)));
        long expectedCardinality = count;

        System.out.println("Expect estimate: " + estimate + " is between " + (expectedCardinality - (3 * se)) + " and " + (expectedCardinality + (3 * se)));
        double err = (Math.abs(estimate - count) / (double) count) * 100;
        System.out.println("Percentage error  " + err);
        long b = System.currentTimeMillis();
        System.out.println("Time in millis " + (b-a));
    }*/

}
