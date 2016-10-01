package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kritter.entity.vast.wrapper.two_dot_zero.NonLinear;

import lombok.Getter;
import lombok.Setter;
@XmlRootElement(name="NonLinearAds")
public class NonLinearAds {
        @Setter@Getter@XmlElement(name="NonLinear")
        private NonLinear nonLinear;
        @Setter@Getter@XmlElement(name="TrackingEvents")
        private TrackingEvents trackingEvents;
}
