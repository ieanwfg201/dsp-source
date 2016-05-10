package com.kritter.api.entity.account_budget;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class Account_budget {
    /** mandatory guid of account @see com.kritter.api.entity.account.Account*/
    private String account_guid = null;
    /** mandatory internal balance */
    private double internal_balance = 0;
    /** mandatory internal burn */
    private double internal_burn = 0;
    /** mandatory advertiser balance */
    private double adv_balance = 0;
    /** mandatory advertiser burn */
    private double adv_burn = 0;
    /** mandatory id of account modifying the entity @see com.kritter.api.entity.account.Account */
    private int modified_by = -1;
    /** auto populated */
    private long created_on = 0;
    /** auto populated */
    private long last_modified = 0;
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        long temp;
        temp = Double.doubleToLongBits(adv_balance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(adv_burn);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (created_on ^ (created_on >>> 32));
        temp = Double.doubleToLongBits(internal_balance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(internal_burn);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + (int) (last_modified ^ (last_modified >>> 32));
        result = prime * result + modified_by;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Account_budget other = (Account_budget) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (Double.doubleToLongBits(adv_balance) != Double
                .doubleToLongBits(other.adv_balance))
            return false;
        if (Double.doubleToLongBits(adv_burn) != Double
                .doubleToLongBits(other.adv_burn))
            return false;
        if (created_on != other.created_on)
            return false;
        if (Double.doubleToLongBits(internal_balance) != Double
                .doubleToLongBits(other.internal_balance))
            return false;
        if (Double.doubleToLongBits(internal_burn) != Double
                .doubleToLongBits(other.internal_burn))
            return false;
        if (last_modified != other.last_modified)
            return false;
        if (modified_by != other.modified_by)
            return false;
        return true;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public double getInternal_balance() {
        return internal_balance;
    }
    public void setInternal_balance(double internal_balance) {
        this.internal_balance = internal_balance;
    }
    public double getInternal_burn() {
        return internal_burn;
    }
    public void setInternal_burn(double internal_burn) {
        this.internal_burn = internal_burn;
    }
    public double getAdv_balance() {
        return adv_balance;
    }
    public void setAdv_balance(double adv_balance) {
        this.adv_balance = adv_balance;
    }
    public double getAdv_burn() {
        return adv_burn;
    }
    public void setAdv_burn(double adv_burn) {
        this.adv_burn = adv_burn;
    }
    public int getModified_by() {
        return modified_by;
    }
    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
    }
    public long getCreated_on() {
        return created_on;
    }
    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }
    public long getLast_modified() {
        return last_modified;
    }
    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static Account_budget getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Account_budget entity = objectMapper.readValue(str, Account_budget.class);
        return entity;
    }


    
}
