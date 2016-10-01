package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="VideoClicks")
public class VideoClicks {
    @Setter@Getter@XmlElement(name="ClickTracking")
    private ClickTracking clickTracking;
    @Setter@Getter@XmlElement(name="CustomClick")
    private CustomClick customClick;
    
}
