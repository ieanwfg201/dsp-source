package com.kritter.entity.vast.normal.twodotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
@XmlRootElement(name="Extensions")
public class Extensions {
    @Setter@Getter@XmlElement(name="Extension")
    private List<Extension> extension;

}
