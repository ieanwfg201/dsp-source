package com.kritter.entity.vast.wrapper.three_dot_zero;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="NonLinear")
public class NonLinear {
    @Setter@Getter@XmlAttribute(name="id")
    private String id;
    @Setter@Getter@XmlAttribute(name="width")
    private String width;
    @Setter@Getter@XmlAttribute(name="height")
    private String height;
    @Setter@Getter@XmlAttribute(name="expandedWidth")
    private String expandedWidth;
    @Setter@Getter@XmlAttribute(name="expandedHeight")
    private String expandedHeight;
    @Setter@Getter@XmlAttribute(name="scalable")
    private String scalable;
    @Setter@Getter@XmlAttribute(name="maintainAspectRatio")
    private String maintainAspectRatio;
    @Setter@Getter@XmlAttribute(name="minSuggestedDuration")
    private String minSuggestedDuration;
    @Setter@Getter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@Getter@XmlAttribute(name="NonLinearClickTracking")
    private List<NonLinearClickTracking> nonLinearClickTracking;
}
