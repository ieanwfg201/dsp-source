package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="CompanionClickThrough")
public class CompanionClickThrough {
    @Setter@Getter@XmlValue
    private String str;
}
