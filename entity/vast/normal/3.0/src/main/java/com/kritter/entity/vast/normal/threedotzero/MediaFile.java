package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="MediaFile")
public class MediaFile {
    @Setter@Getter@XmlAttribute(name="id")
    private String id;
    @Setter@Getter@XmlAttribute(name="delivery")
    private String delivery;
    @Setter@Getter@XmlAttribute(name="type")
    private String type;
    @Setter@Getter@XmlAttribute(name="bitrate")
    private String bitrate;
    @Setter@Getter@XmlAttribute(name="minBitrate")
    private String minBitrate;
    @Setter@Getter@XmlAttribute(name="maxBitrate")
    private String maxBitrate;
    @Setter@Getter@XmlAttribute(name="width")
    private int width;
    @Setter@Getter@XmlAttribute(name="height")
    private int height;
    @Setter@Getter@XmlAttribute(name="scalable")
    private String scalable;
    @Setter@Getter@XmlAttribute(name="mantainAspectRatio")
    private String mantainAspectRatio;
    @Setter@Getter@XmlAttribute(name="codec")
    private String codec;
    @Setter@Getter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
