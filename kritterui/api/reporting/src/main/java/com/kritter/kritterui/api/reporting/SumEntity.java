package com.kritter.kritterui.api.reporting;

public class SumEntity {
    private int type = 0; /*0 int|1 long|2 double*/ 
    private long sumLong = 0L;
    private int sumInt = 0;
    private double sumDouble = 0;
    public SumEntity(int type){
        this.type = type;
    }
    public long getSumLong() {
        return sumLong;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setSumLong(long sumLong) {
        this.sumLong = sumLong;
    }
    public int getSumInt() {
        return sumInt;
    }
    public void setSumInt(int sumInt) {
        this.sumInt = sumInt;
    }
    public double getSumDouble() {
        return sumDouble;
    }
    public void setSumDouble(double sumDouble) {
        this.sumDouble = sumDouble;
    }
}
