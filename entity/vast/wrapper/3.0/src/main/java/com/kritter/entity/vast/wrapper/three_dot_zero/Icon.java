package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Icon")
public class Icon {
    @Setter@Getter@XmlAttribute(name="program")
    private String program;
    @Setter@Getter@XmlAttribute(name="width")
    private String width;
    @Setter@Getter@XmlAttribute(name="height")
    private String height;
    @Setter@Getter@XmlAttribute(name="xPosition")
    private String xPosition;
    @Setter@Getter@XmlAttribute(name="yPosition")
    private String yPosition;
    @Setter@Getter@XmlAttribute(name="duration")
    private String duration;
    @Setter@Getter@XmlAttribute(name="offset")
    private String offset;
    @Setter@Getter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@Getter@XmlElement(name="StaticResource")
    private StaticResource staticResource;
    @Setter@Getter@XmlElement(name="IFrameResource")
    private IFrameResource iframeResource;
    @Setter@Getter@XmlElement(name="HTMLResource")
    private HTMLResource htmlResource;
    @Setter@Getter@XmlElement(name="IconClicks")
    private IconClicks iconClicks;
    @Setter@Getter@XmlElement(name="IconViewTracking")
    private IconViewTracking iconViewTracking;    
}
