package com.kritter.entity.vast.normal.twodotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;
@XmlRootElement(name="Creatives")
public class Creatives {
    @Setter@XmlElement(name="Creative")
    private List<Creative> creative;

}
