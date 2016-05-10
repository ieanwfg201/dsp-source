package com.kritter.api.entity.insertion_order;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class Insertion_Order_List {
    /** list of insertion orders @see com.kritter.api.entity.insertion_order.Insertion_Order*/
    private List<Insertion_Order> insertion_order_list = null;
    /** message @see com.kritter.api.entity.response.msg.Message */
    private Message msg = null;
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((insertion_order_list == null) ? 0 : insertion_order_list
                        .hashCode());
        result = prime * result + ((msg == null) ? 0 : msg.hashCode());
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
        Insertion_Order_List other = (Insertion_Order_List) obj;
        if (insertion_order_list == null) {
            if (other.insertion_order_list != null)
                return false;
        } else if (!insertion_order_list.equals(other.insertion_order_list))
            return false;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        return true;
    }
    public List<Insertion_Order> getInsertion_order_list() {
        return insertion_order_list;
    }
    public void setInsertion_order_list(List<Insertion_Order> insertion_order_list) {
        this.insertion_order_list = insertion_order_list;
    }
    public Message getMsg() {
        return msg;
    }
    public void setMsg(Message msg) {
        this.msg = msg;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Insertion_Order_List getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Insertion_Order_List entity = objectMapper.readValue(str, Insertion_Order_List.class);
        return entity;
    }
}
