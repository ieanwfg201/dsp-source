package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="Wrapper")
public class Wrapper {
    @Setter@XmlElement(name="AdSystem")
    private AdSystem adSystem;
    @Setter@XmlElement(name="VASTAdTagURI")
    private VASTAdTagURI vastAdTagURI;
    @Setter@XmlElement(name="Error")
    private Error error;
    @Setter@XmlElement(name="Impression")
    private Impression impression;
    @Setter@XmlElement(name="Creatives")
    private Creatives creatives;
    @Setter@XmlElement(name="Extensions")
    private Extensions extensions;
}
