package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="AdParameters")
public class AdParameters {
    @Setter@Getter@XmlValue
    private String str;
}
