package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Setter;

@XmlRootElement(name="Icon")
public class Icon {
    @Setter@XmlAttribute(name="program")
    private String program;
    @Setter@XmlAttribute(name="width")
    private String width;
    @Setter@XmlAttribute(name="height")
    private String height;
    @Setter@XmlAttribute(name="xPosition")
    private String xPosition;
    @Setter@XmlAttribute(name="yPosition")
    private String yPosition;
    @Setter@XmlAttribute(name="duration")
    private String duration;
    @Setter@XmlAttribute(name="offset")
    private String offset;
    @Setter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@XmlElement(name="StaticResource")
    private StaticResource staticResource;
    @Setter@XmlElement(name="IFrameResource")
    private IFrameResource iFrameResource;
    @Setter@XmlElement(name="HTMLResource")
    private HTMLResource hTMLResource;
    @Setter@XmlElement(name="IconClicks")
    private IconClicks iconClicks;
    @Setter@XmlElement(name="IconViewTracking")
    private IconViewTracking iconViewTracking;
    

}
