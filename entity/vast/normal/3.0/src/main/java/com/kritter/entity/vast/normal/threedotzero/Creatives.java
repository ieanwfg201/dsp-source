package com.kritter.entity.vast.normal.threedotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Creatives")
public class Creatives {
    @Setter@Getter@XmlElement(name="Creative")
    private List<Creative> creative;
}
