package com.kritter.common.site.entity;

import com.kritter.constants.SITE_PASSBACK_CONTENT_TYPE;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created with IntelliJ IDEA.
 * User: chahar
 * Date: 18/5/15
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoFillPassbackContent
{
    @JsonProperty("priority")
    @Setter
    private int priority;

    @JsonIgnore
    public int getPriority()
    {
        return priority;
    }

    @JsonProperty("content")
    @Setter
    private String passBackContent;

    @JsonIgnore
    public String getPassBackContent()
    {
        return passBackContent;
    }

    @JsonProperty("type")
    @Setter
    private short passBackContentType;

    @JsonIgnore
    public short getPassBackContentType()
    {
        return passBackContentType;
    }

    public static void main(String s[]) throws Exception
    {
        NoFillPassbackContent n1 = new NoFillPassbackContent();
        n1.setPassBackContent("http://google.com");
        n1.setPassBackContentType(SITE_PASSBACK_CONTENT_TYPE.PASSBACK_URL.getCode());
        n1.setPriority(1);

        NoFillPassbackContent n2 = new NoFillPassbackContent();
        n2.setPassBackContent("<html>ad comes here<html>");
        n2.setPassBackContentType(SITE_PASSBACK_CONTENT_TYPE.PASSBACK_DIRECT_PAYLOAD.getCode());
        n2.setPriority(2);

        NoFillPassbackContent[] a = new NoFillPassbackContent[2];
        a[0]= n1;
        a[1]= n2;

        ObjectMapper o = new ObjectMapper();

        System.out.println(o.writeValueAsString(a));
    }
}
