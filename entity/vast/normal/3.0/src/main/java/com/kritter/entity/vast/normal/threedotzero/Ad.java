package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Setter;

@XmlRootElement(name="Ad")
public class Ad {
    @Setter@XmlAttribute(name="id")
    private String id="3.0";
    @Setter@XmlAttribute(name="sequence")
    private String sequence="1";
    @Setter@XmlElement(name="Inline")
    private Inline inline;

}
