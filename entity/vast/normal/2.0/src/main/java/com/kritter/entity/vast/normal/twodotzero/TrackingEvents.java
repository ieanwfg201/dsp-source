package com.kritter.entity.vast.normal.twodotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;
@XmlRootElement(name="TrackingEvents")
public class TrackingEvents {
    @Setter@XmlElement(name="Tracking")
    private List<Tracking> trackingEvents;

}
