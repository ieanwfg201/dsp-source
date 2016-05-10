package com.kritter.api.entity.insertion_order;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.IOStatus;

public class Insertion_Order {
    /** mandatory Manually generated order number */
    private String order_number = null;
    /** mandatory Account guid of @see com.kritter.api.entity.account.Account to which 
     * io belongs*/
    private String account_guid = null;
    /** mandatory io name */
    private String name = null;
    /** mandatory io value */
    private double total_value = 0;
    /** mandatory id of account modifying the entity @see com.kritter.api.entity.account.Account */
    private int modified_by = -1;
    /** auto generated */
    private long created_on = 0;
    /** auto generated */
    private long last_modified = 0;
    /** mandatory io status @see  com.kritter.constants.IoStatus */
    private IOStatus status = IOStatus.NEW;
    /** optional */
    private String comment = null;
    /** optional */
    private String account_name = null;
    /** mandatory id of account creating  the entity @see com.kritter.api.entity.account.Account */
    private int created_by = 1;
    /** optional */
    private String created_by_name = "";
    /** mandatory id of account to whome the io belongs  the entity @see com.kritter.api.entity.account.Account */
    private int belongs_to = 1;
    /** optional */
    private String belongs_to_name = "";
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        result = prime * result
                + ((account_name == null) ? 0 : account_name.hashCode());
        result = prime * result + belongs_to;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + created_by;
        result = prime * result
                + ((created_by_name == null) ? 0 : created_by_name.hashCode());
        result = prime * result + (int) (created_on ^ (created_on >>> 32));
        result = prime * result
                + (int) (last_modified ^ (last_modified >>> 32));
        result = prime * result + modified_by;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((order_number == null) ? 0 : order_number.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        long temp;
        temp = Double.doubleToLongBits(total_value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Insertion_Order other = (Insertion_Order) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (account_name == null) {
            if (other.account_name != null)
                return false;
        } else if (!account_name.equals(other.account_name))
            return false;
        if (belongs_to != other.belongs_to)
            return false;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.equals(other.comment))
            return false;
        if (created_by != other.created_by)
            return false;
        if (created_by_name == null) {
            if (other.created_by_name != null)
                return false;
        } else if (!created_by_name.equals(other.created_by_name))
            return false;
        if (created_on != other.created_on)
            return false;
        if (last_modified != other.last_modified)
            return false;
        if (modified_by != other.modified_by)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (order_number == null) {
            if (other.order_number != null)
                return false;
        } else if (!order_number.equals(other.order_number))
            return false;
        if (status != other.status)
            return false;
        if (Double.doubleToLongBits(total_value) != Double
                .doubleToLongBits(other.total_value))
            return false;
        return true;
    }
    public String getOrder_number() {
        return order_number;
    }
    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getTotal_value() {
        return total_value;
    }
    public void setTotal_value(double total_value) {
        this.total_value = total_value;
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
    public IOStatus getStatus() {
        return status;
    }
    public void setStatus(IOStatus status) {
        this.status = status;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getAccount_name() {
        return account_name;
    }
    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }
    public int getCreated_by() {
        return created_by;
    }
    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }
    public String getCreated_by_name() {
        return created_by_name;
    }
    public void setCreated_by_name(String created_by_name) {
        this.created_by_name = created_by_name;
    }
    public int getBelongs_to() {
        return belongs_to;
    }
    public void setBelongs_to(int belongs_to) {
        this.belongs_to = belongs_to;
    }
    public String getBelongs_to_name() {
        return belongs_to_name;
    }
    public void setBelongs_to_name(String belongs_to_name) {
        this.belongs_to_name = belongs_to_name;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static Insertion_Order getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Insertion_Order entity = objectMapper.readValue(str, Insertion_Order.class);
        return entity;
    }
  
}
