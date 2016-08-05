package com.kritter.entity.vast.wrapper.two_dot_zero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kritter.entity.vast.wrapper.two_dot_zero.NonLinear;
import com.kritter.entity.vast.wrapper.two_dot_zero.Tracking;

import lombok.Setter;
@XmlRootElement(name="NonLinearAds")
public class NonLinearAds {
        @Setter@XmlElement(name="NonLinear")
        private NonLinear nonLinear;
        @Setter@XmlElement(name="TrackingEvents")
        private TrackingEvents trackingEvents;
}
