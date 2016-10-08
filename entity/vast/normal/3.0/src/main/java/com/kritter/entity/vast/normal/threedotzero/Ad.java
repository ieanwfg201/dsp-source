package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Ad")
public class Ad {
	@Getter@Setter@XmlAttribute(name="id")
    private String id="3.0";
	@Getter@Setter@XmlAttribute(name="sequence")
    private String sequence="1";
	@Getter@Setter@XmlElement(name="InLine")
    private Inline inline;

}
