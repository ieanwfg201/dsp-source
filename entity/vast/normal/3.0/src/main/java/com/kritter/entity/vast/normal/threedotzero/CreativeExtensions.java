package com.kritter.entity.vast.normal.threedotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="CreativeExtensions")
public class CreativeExtensions {
    @Setter@XmlElement(name="CreativeExtension")
    private List<CreativeExtension> creativeExtension;
}
