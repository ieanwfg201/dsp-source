package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="NonLinear")
public class NonLinear {
    @Setter@XmlAttribute(name="id")
    private String id;
    @Setter@XmlAttribute(name="width")
    private String width;
    @Setter@XmlAttribute(name="height")
    private String height;
    @Setter@XmlAttribute(name="expandedWidth")
    private String expandedWidth;
    @Setter@XmlAttribute(name="expandedHeight")
    private String expandedHeight;
    @Setter@XmlAttribute(name="scalable")
    private String scalable;
    @Setter@XmlAttribute(name="maintainAspectRatio")
    private String maintainAspectRatio;
    @Setter@XmlAttribute(name="minSuggestedDuration")
    private String minSuggestedDuration;
    @Setter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@XmlAttribute(name="NonLinearClickTracking")
    private NonLinearClickTracking nonLinearClickTracking;
}
