package com.kritter.entity.account;

import com.kritter.constants.*;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class AccountDef
{
    @Getter@Setter
    public int accountId;
    @Getter@Setter
    public String guid;
    @Getter@Setter
    public DemandType demandType;
    @Getter@Setter
    public DemandPreference demandPreference;
    @Getter@Setter
    public int qps = 5;
    @Getter@Setter
    public int timeout = 100;
    @Getter@Setter
    public int currency = DefaultCurrency.defaultCurrency.getCode();
    @Getter@Setter
    public boolean test = false;
    @Getter@Setter
    public Short[] btype = null;

    /*In case of third party demand channel, url is used as bidder URL where
    * open rtb requests are sent.*/
    @Getter@Setter
    public String demand_url;

    /*Used in identifying what kind of demand channel this advertiser account is.*/
    @Getter @Setter
    private ThirdPartyDemandChannel thirdPartyDemandChannel;

    /*The open rtb version this third party demand channel will accept.*/
    @Getter @Setter
    private OpenRTBVersion openRTBVersion;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AccountDef getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static AccountDef getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        AccountDef entity = objectMapper.readValue(str, AccountDef.class);
        return entity;

    }
}
