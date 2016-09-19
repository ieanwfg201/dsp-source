package com.kritter.thirdpartydata.loader.check;

import java.math.BigInteger;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class CheckEntity {
    
    @Getter@Setter
    private BigInteger endIp;
    @Getter@Setter
    private String val;
}
