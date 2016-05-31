package com.kritter.kumbaya.countdistinct.dimension_based_counting;

import java.io.BufferedWriter;
import java.util.HashMap;

import com.kritter.kumbaya.countdistinct.hyperloglogplusplus.HyperLogLogPlusPlus;

import lombok.Getter;
import lombok.Setter;

public class DimensionCounting {
    @Getter@Setter
    private HashMap<String, HyperLogLogPlusPlus> dimensionMap = null;
    @Getter@Setter
    private HashMap<String, Long> cardinalityMap = null;
    
    public DimensionCounting(){
        init();
    }
    
    private void init(){
        if(this.dimensionMap == null){
            dimensionMap = new HashMap<String, HyperLogLogPlusPlus>();
        }
    }
    
    public void addElement(String dimension, String id){
        HyperLogLogPlusPlus hllpp = dimensionMap.get(dimension);
        if( hllpp == null){
            hllpp = new HyperLogLogPlusPlus(null, null);
            hllpp.addElement(id);
            dimensionMap.put(dimension, hllpp);
        }else{
            hllpp.addElement(id);
        }
    }
    public void populateCardinalityMap(){
        cardinalityMap = new HashMap<String, Long>();
        for(String str:dimensionMap.keySet()){
            cardinalityMap.put(str, dimensionMap.get(str).getCount());
        }
    }
    public void printCardinalities(){
        for(String str:dimensionMap.keySet()){
            System.out.println(str+" : "+dimensionMap.get(str).getCount());
        }        
    }
    public void printCardinalitiesInBufferedWriter(BufferedWriter bw, String delimiter, String processingTime) throws Exception{
        for(String str:dimensionMap.keySet()){
            bw.write(processingTime);
            bw.write(delimiter);
            bw.write(str);
            bw.write(delimiter);
            bw.write(dimensionMap.get(str).getCount()+"\n");
        }        
    }
    /*public static void main(String args[]){
        DimensionCounting dc = new DimensionCounting();
        dc.addElement("d1", "1");
        dc.addElement("d2", "1");
        dc.addElement("d2", "12");
        dc.printCardinalities();
    }*/
}
