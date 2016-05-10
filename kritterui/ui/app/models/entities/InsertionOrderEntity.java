package models.entities;

import org.springframework.beans.BeanUtils;

import play.data.validation.Constraints.Required;

import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.constants.IOStatus;

public class InsertionOrderEntity {
    @Required
    private String order_number = null;
    @Required
    private String account_guid = null;
    @Required
    private String name = null;
    
    @Required
    private double total_value = 0;
    private int modified_by = -1;
    private long created_on = 0;
    private long last_modified = 0;
    private IOStatus status = IOStatus.NEW;
    private String comment = null;
    private String account_name = null;
    @Required
    private int belongs_to = 1;
    
    
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
    
    public int getBelongs_to() {
        return belongs_to;
    }
    public void setBelongs_to(int belongs_to) {
        this.belongs_to = belongs_to;
    }
    public Insertion_Order getEntity(){
    	Insertion_Order io = new Insertion_Order();
    	BeanUtils.copyProperties(this, io);
    	return io;
    }
  
}
