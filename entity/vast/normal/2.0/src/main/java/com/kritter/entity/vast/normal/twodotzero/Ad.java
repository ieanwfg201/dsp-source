package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Ad")
public class Ad {
    @Setter@Getter@XmlAttribute(name="id")
    private String id;
    @Setter@Getter@XmlElement(name="InLine")
    private InLine inline;

}
