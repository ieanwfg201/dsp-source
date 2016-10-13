package com.kritter.vast.vastversion;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Ad")
public class CheckAd {
    @Getter@Setter@XmlElement(name="Wrapper")
    private CheckWrapper wrapper;
}
