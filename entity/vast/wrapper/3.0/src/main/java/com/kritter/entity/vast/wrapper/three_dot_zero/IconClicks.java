package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="IconClicks")
public class IconClicks {
    @Setter@Getter@XmlElement(name="IconClickThrough")
    private IconClickThrough iconClickThrough;
    @Setter@Getter@XmlElement(name="IconClickTracking")
    private IconClickTracking iconClickTracking;
}
