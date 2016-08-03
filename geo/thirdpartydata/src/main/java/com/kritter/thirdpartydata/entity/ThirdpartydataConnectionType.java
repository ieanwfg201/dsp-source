package com.kritter.thirdpartydata.entity;

import java.math.BigInteger;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class ThirdpartydataConnectionType {
    
    @Getter@Setter
    private BigInteger endIp;
    @Getter@Setter
    private int connectionTypeInt;
}
