package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="VAST")
public class VideoClicks {
    
    @Setter@XmlElement(name="ClickThrough")
    private ClickThrough clickThrough;
    @Setter@XmlElement(name="ClickTracking")
    private ClickTracking clickTracking;
    @Setter@XmlElement(name="CustomClick")
    private CustomClick customClick;
    
}
