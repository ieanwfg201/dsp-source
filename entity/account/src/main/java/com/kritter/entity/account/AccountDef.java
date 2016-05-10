package com.kritter.entity.account;

import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.DemandPreference;
import com.kritter.constants.DemandType;
import com.kritter.constants.SupportedCurrencies;

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
    public int currency = SupportedCurrencies.USD.getCode();
    @Getter@Setter
    public boolean test = false;
    @Getter@Setter
    public Short[] btype = null;
    @Getter@Setter
    public String demand_url;
    
    public static AccountDef getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static AccountDef getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        AccountDef entity = objectMapper.readValue(str, AccountDef.class);
        return entity;

    }
}
