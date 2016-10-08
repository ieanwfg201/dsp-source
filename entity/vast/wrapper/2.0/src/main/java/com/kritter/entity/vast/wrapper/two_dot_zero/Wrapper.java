package com.kritter.entity.vast.wrapper.two_dot_zero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Wrapper")
public class Wrapper {
    @Setter@Getter@XmlElement(name="AdSystem")
    private AdSystem adSystem;
    @Setter@Getter@XmlElement(name="VASTAdTagURI")
    private VASTAdTagURI vastAdTagURI;
    @Setter@Getter@XmlElement(name="Error")
    private Error error;
    @Setter@Getter@XmlElement(name="Impression")
    private List<Impression> impression;
    @Setter@Getter@XmlElement(name="Creatives")
    private Creatives creatives;
    @Setter@Getter@XmlElement(name="Extensions")
    private Extensions extensions;
}
