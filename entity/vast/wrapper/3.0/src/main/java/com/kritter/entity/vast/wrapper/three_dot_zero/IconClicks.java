package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="IconClicks")
public class IconClicks {
    @Setter@XmlElement(name="IconClickThrough")
    private IconClickThrough iconClickThrough;
    @Setter@XmlElement(name="IconClickTracking")
    private IconClickTracking iconClickTracking;
}
