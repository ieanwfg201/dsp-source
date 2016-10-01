package com.kritter.entity.vast.normal.twodotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
@XmlRootElement(name="NonLinearAds")
public class NonLinearAds {
    @Setter@Getter@XmlElement(name="NonLinearAd")
    private List<NonLinear> nonLinearAd;

}
