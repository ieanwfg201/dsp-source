package com.kritter.entity.vast.normal.twodotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="InLine")
public class InLine {
    @Setter@XmlElement(name="AdSystem")
    private AdSystem adSystem;
    @Setter@XmlElement(name="AdTitle")
    private AdTitle adTitle;
    @Setter@XmlElement(name="Description")
    private Description description;
    @Setter@XmlElement(name="Survey")
    private Survey survey;
    @Setter@XmlElement(name="Error")
    private Error error;
    @Setter@XmlElement(name="Impression")
    private Impression impression;
    @Setter@XmlElement(name="Creatives")
    private List<Creative> creatives;
    @Setter@XmlElement(name="Extensions")
    private List<Extension> extensions;

}
