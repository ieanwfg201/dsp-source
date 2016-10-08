package com.kritter.entity.vast.wrapper.two_dot_zero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="TrackingEvents")
public class TrackingEvents {
    @Setter@Getter@XmlElement(name="Tracking")
    private List<Tracking> tracking;


}
