package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="VideoClick")
public class VideoClicks {
    
    @Setter@Getter@XmlElement(name="ClickThrough")
    private ClickThrough clickThrough;
    @Setter@Getter@XmlElement(name="ClickTracking")
    private ClickTracking clickTracking;
    @Setter@Getter@XmlElement(name="CustomClick")
    private CustomClick customClick;
    
}
