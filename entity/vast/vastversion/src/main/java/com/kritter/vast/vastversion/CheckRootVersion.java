package com.kritter.vast.vastversion;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="VAST")
public class CheckRootVersion {
	    
	@Getter@Setter@XmlAttribute(name="version")
	private String version;
	@Getter@Setter@XmlElement(name="Ad")
    private CheckAd ad;

}
