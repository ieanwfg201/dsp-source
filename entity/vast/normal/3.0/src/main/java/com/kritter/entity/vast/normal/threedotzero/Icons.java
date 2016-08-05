package com.kritter.entity.vast.normal.threedotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="Icons")
public class Icons {
    @Setter@XmlElement(name="Icon")
    private List<Icon> icon;
}
