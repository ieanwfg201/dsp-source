package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="MediaFile")
public class MediaFile {
    @Setter@XmlAttribute(name="id")
    private String id;
    @Setter@XmlAttribute(name="delivery")
    private String delivery;
    @Setter@XmlAttribute(name="type")
    private String type;
    @Setter@XmlAttribute(name="bitrate")
    private String bitrate;
    @Setter@XmlAttribute(name="width")
    private int width;
    @Setter@XmlAttribute(name="height")
    private int height;
    @Setter@XmlAttribute(name="scalable")
    private String scalable;
    @Setter@XmlAttribute(name="maintainAspectRatio")
    private String maintainAspectRatio;
    @Setter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@XmlValue@XmlCDATA
    private String str;
}
