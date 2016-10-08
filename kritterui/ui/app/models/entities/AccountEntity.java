package models.entities;

import org.springframework.beans.BeanUtils;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

import com.kritter.api.entity.account.Account;
import com.kritter.constants.Account_Type;
import com.kritter.constants.Payment_type;
import com.kritter.constants.Payout;
import com.kritter.constants.StatusIdEnum;

import lombok.Getter;
import lombok.Setter;




public class AccountEntity {
    private int id = -1;
    private String guid = null;
    private StatusIdEnum status = StatusIdEnum.Pending;
    @Required
    private Account_Type type_id = null;
    @Required
    private String name = null;
    @Required
    private String userid = null;
    
    private String password = null;
    @Required
    @Email
    private String email = null;
    private String address = null;
    @Required
    private String country = null;
    @Required
    private String city = null;
    private int modified_by = -1; 
    private long created_on = -1;
    private long last_modified = -1;
   
    
    @Required 
    @Pattern("[0-9\\-()+]+")
    private String phone = null;
    @Required
    private String company_name = null;
    private Payment_type payment_type = Payment_type.NothingSelected;
    private String bank_transfer_beneficiary_name = null;
    private String bank_transfer_account_number = null;
    private String bank_transfer_bank_name = null;
    private String bank_transfer_bank_add = null;
    private String bank_transfer_branch_number = null;
    private String bank_transfer_vat_number = null;
    private String wire_beneficiary_name = null;
    private String wire_account_number = null;
    private String wire_bank_name = null;
    private String wire_transfer_bank_add = null;
    private String wire_swift_code = null;
    private String wire_iban = null;
    private String paypal_id = null;
    private String im = null;
    private String api_key = null;
    private int inventory_source = 0;
    private String billing_rules_json = Payout.default_payout_percent_str;
    private int demandtype = 0;
    private int demandpreference = 0;
    private int qps = 5;
    private int timeout = 200;
    private String demand_url = "";
    private int open_rtb_ver_required = 0;
    private int third_party_demand_channel_type = 0;
    private String billing_name = "";
    private String billing_email="";
    private String ext="";
    private boolean adxbased=false;
    @Getter@Setter
    private String contactdetail="";
    @Getter@Setter
    private String brand="";
    @Getter@Setter
    private String firstIndustryCode;
    @Getter@Setter
    private String secondIndustryCode;


    public String getDemand_url() {
        return demand_url;
    }
    public void setDemand_url(String demand_url) {
        this.demand_url = demand_url;
    }
    public int getOpen_rtb_ver_required() {
        return open_rtb_ver_required;
    }
    public void setOpen_rtb_ver_required(int open_rtb_ver_required) {
        this.open_rtb_ver_required = open_rtb_ver_required;
    }
    public int getThird_party_demand_channel_type() {
        return third_party_demand_channel_type;
    }
    public void setThird_party_demand_channel_type(int third_party_demand_channel_type) {
        this.third_party_demand_channel_type = third_party_demand_channel_type;
    }
    public int getQps() {
        return qps;
    }
    public void setQps(int qps) {
        this.qps = qps;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public int getDemandpreference() {
        return demandpreference;
    }
    public void setDemandpreference(int demandpreference) {
        this.demandpreference = demandpreference;
    }
    public int getDemandtype() {
        return demandtype;
    }
    public void setDemandtype(int demandtype) {
        this.demandtype = demandtype;
    }
    public String getPaypal_id() {
        return paypal_id;
    }
    public void setPaypal_id(String paypal_id) {
        this.paypal_id = paypal_id;
    }
 
    public Payment_type getPayment_type() {
        return payment_type;
    }
    public void setPayment_type(Payment_type payment_type) {
        this.payment_type = payment_type;
    }
    public String getBank_transfer_beneficiary_name() {
        return bank_transfer_beneficiary_name;
    }
    public void setBank_transfer_beneficiary_name(
            String bank_transfer_beneficiary_name) {
        this.bank_transfer_beneficiary_name = bank_transfer_beneficiary_name;
    }
    public String getBank_transfer_account_number() {
        return bank_transfer_account_number;
    }
    public void setBank_transfer_account_number(String bank_transfer_account_number) {
        this.bank_transfer_account_number = bank_transfer_account_number;
    }
    public String getBank_transfer_bank_name() {
        return bank_transfer_bank_name;
    }
    public void setBank_transfer_bank_name(String bank_transfer_bank_name) {
        this.bank_transfer_bank_name = bank_transfer_bank_name;
    }
    public String getBank_transfer_bank_add() {
        return bank_transfer_bank_add;
    }
    public void setBank_transfer_bank_add(String bank_transfer_bank_add) {
        this.bank_transfer_bank_add = bank_transfer_bank_add;
    }
    public String getBank_transfer_branch_number() {
        return bank_transfer_branch_number;
    }
    public void setBank_transfer_branch_number(String bank_transfer_branch_number) {
        this.bank_transfer_branch_number = bank_transfer_branch_number;
    }
    public String getBank_transfer_vat_number() {
        return bank_transfer_vat_number;
    }
    public void setBank_transfer_vat_number(String bank_transfer_vat_number) {
        this.bank_transfer_vat_number = bank_transfer_vat_number;
    }
    public String getWire_beneficiary_name() {
        return wire_beneficiary_name;
    }
    public void setWire_beneficiary_name(String wire_beneficiary_name) {
        this.wire_beneficiary_name = wire_beneficiary_name;
    }
    public String getWire_account_number() {
        return wire_account_number;
    }
    public void setWire_account_number(String wire_account_number) {
        this.wire_account_number = wire_account_number;
    }
    public String getWire_bank_name() {
        return wire_bank_name;
    }
    public void setWire_bank_name(String wire_bank_name) {
        this.wire_bank_name = wire_bank_name;
    }
    public String getWire_transfer_bank_add() {
        return wire_transfer_bank_add;
    }
    public void setWire_transfer_bank_add(String wire_transfer_bank_add) {
        this.wire_transfer_bank_add = wire_transfer_bank_add;
    }
    public String getWire_swift_code() {
        return wire_swift_code;
    }
    public void setWire_swift_code(String wire_swift_code) {
        this.wire_swift_code = wire_swift_code;
    }
    public String getWire_iban() {
        return wire_iban;
    }
    public void setWire_iban(String wire_iban) {
        this.wire_iban = wire_iban;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
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
    
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public StatusIdEnum getStatus() {
        return status;
    }
    public void setStatus(StatusIdEnum status) {
        this.status = status;
    }
    public Account_Type getType_id() {
        return type_id;
    }
    public void setType_id(Account_Type type_id) {
        this.type_id = type_id;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getCompany_name() {
        return company_name;
    }
    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
    public String getIm() {
        return im;
    }
    public void setIm(String im) {
        this.im = im;
    }
    public String getApi_key() {
        return api_key;
    }
    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }
    public int getInventory_source() {
        return inventory_source;
    }
    public void setInventory_source(int inventory_source) {
        this.inventory_source = inventory_source;
    }
    public String getBilling_rules_json() {
        return billing_rules_json;
    }
    public void setBilling_rules_json(String billing_rules_json) {
        this.billing_rules_json = billing_rules_json;
    }
    public String getBilling_name() {
        return billing_name;
    }
    public void setBilling_name(String billing_name) {
        this.billing_name = billing_name;
    }
    public String getBilling_email() {
        return billing_email;
    }
    public void setBilling_email(String billing_email) {
        this.billing_email = billing_email;
    }
    public String getExt() {
        return ext;
    }
    public void setExt(String ext) {
        this.ext = ext;
    }
    public boolean isAdxbased() {
        return adxbased;
    }
    public void setAdxbased(boolean adxbased) {
        this.adxbased = adxbased;
    }

    public Account getEntity(){
    	Account account = new Account();
    	BeanUtils.copyProperties(this, account);
    	return account;
    }
    
}
