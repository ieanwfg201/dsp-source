package com.kritter.api.entity.account;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AccountTest {
    @Test
    public void t() throws IOException{
        Account account = new Account();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(account);
        System.out.print(jsonNode.toString());
        account = new Account();
        account.setUserid("helllooo");
        
        objectMapper = new ObjectMapper();
        jsonNode = objectMapper.valueToTree(account);
        System.out.print(jsonNode.toString());
        
        objectMapper = new ObjectMapper();
        Account account1 = null; 
        account1 = objectMapper.treeToValue(jsonNode, Account.class);
        Assert.assertEquals(account1, account);
    }
       
}
