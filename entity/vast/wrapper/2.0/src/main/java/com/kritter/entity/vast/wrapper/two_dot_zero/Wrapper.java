package com.kritter.entity.vast.wrapper.two_dot_zero;

import java.util.List;

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
    private List<Creative> creatives;
    @Setter@XmlElement(name="Extensions")
    private List<Extension> extensions;
}
