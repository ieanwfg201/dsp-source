package com.kritter.entity.vast.normal.twodotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;
@XmlRootElement(name="CompanionAds")
public class CompanionAds {
    @Setter@XmlElement(name="Companion")
    private List<Companion> companion;

}
