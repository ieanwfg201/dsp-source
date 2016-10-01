package com.kritter.entity.vast.normal.threedotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="MediaFiles")
public class MediaFiles {
    @Setter@Getter@XmlElement(name="MediaFile")
    private List<MediaFile> mediaFile;
}
