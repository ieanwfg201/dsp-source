package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.Setter;

@XmlRootElement(name="IFrameResource")
public class IFrameResource {
    @Setter@XmlValue
    private String str;
}
