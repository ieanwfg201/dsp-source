package com.kritter.entity.vast.normal.twodotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;
@XmlRootElement(name="MediaFiles")
public class MediaFiles {
    @Setter@XmlElement(name="MediaFile")
    private List<MediaFile> mediaFile;
}
