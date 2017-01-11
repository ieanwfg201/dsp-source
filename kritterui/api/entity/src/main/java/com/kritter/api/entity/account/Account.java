package com.kritter.api.entity.account;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.Account_Type;
import com.kritter.constants.OpenRTBVersion;
import com.kritter.constants.Payment_type;
import com.kritter.constants.Payout;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.ThirdPartyDemandChannel;
import com.kritter.entity.demand_props.DemandProps;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;



@EqualsAndHashCode
public class Account {
    /** mandatory int internal id of account {default at the time of creation} */
    private int id = -1; 
    /** mandatory String guid of account {default at the time of creation} */
    private String guid = null;
    /** mandatory @see  com.kritter.constants.StatusIdEnum */
    private StatusIdEnum status = StatusIdEnum.Pending;
    /** mandatory @see  com.kritter.constants.Account_Type 2 for publisher/aggregator/exchange and 3 for advertiser */
    private Account_Type type_id = null;
    /** mandatory */
    private String name = null;
    /** mandatory */
    private String userid = null;
    /** mandatory */
    private String password = null;
    /** mandatory */
    private String email = null;
    /** mandatory */
    private String address = null;
    /** mandatory country String */
    private String country = null;
    /** mandatory*/
    private String city = null;
    /** mandatory refers to the account is which is modifying the entity - root account by default  */
    private int modified_by = -1; 
    /** optional auto populated */
    private long created_on = -1;
    /** optional auto populated */
    private long last_modified = -1;
    /** mandatory */
    private String phone = null;
    /** mandatory */
    private String company_name = null;
    /** mandatory @see com.kritter.constants.Payment_type. Required for entering publisher payout information */
    private Payment_type payment_type = Payment_type.NothingSelected;
    /** optional */
    private String bank_transfer_beneficiary_name = null;
    /** optional */
    private String bank_transfer_account_number = null;
    /** optional */
    private String bank_transfer_bank_name = null;
    /** optional */
    private String bank_transfer_bank_add = null;
    /** optional */
    private String bank_transfer_branch_number = null;
    /** optional */
    private String bank_transfer_vat_number = null;
    /** optional */
    private String wire_beneficiary_name = null;
    /** optional */
    private String wire_account_number = null;
    /** optional */
    private String wire_bank_name = null;
    /** optional */
    private String wire_transfer_bank_add = null;
    /** optional */
    private String wire_swift_code = null;
    /** optional */
    private String wire_iban = null;
    /** optional */
    private String paypal_id = null;
    /** mandatory */
    private String im = null;
    /** optional */
    private String comment = null;
    /** mandatory auto populated */
    private String api_key = null;
    /** mandatory @see com.kritter.constants.INVENTORY_SOURCE */
    private int inventory_source = 0;
    /** mandatory @see com.kritter.constants.Payout */
    private String billing_rules_json = Payout.default_payout_percent_str;
    /** mandatory @see com.kritter.constants.DemandType */
    private int demandtype = 0;
    /** mandatory @see com.kritter.constants.DemandPreference */
    private int demandpreference = 0;
    /** mandatory for external demand */
    private int qps = 5;
    /** mandatory for external demand traffic */
    private int timeout = 200;
    @JsonIgnore@Getter@Setter
    private DemandProps demandProps = null;
    @Getter@Setter
    private String demand_url = "";
    @Getter @Setter
    private int open_rtb_ver_required = OpenRTBVersion.VERSION_2_3.getCode();
    @Getter @Setter
    private int third_party_demand_channel_type = ThirdPartyDemandChannel.STANDALONE_DSP_BIDDER.getCode();
    private String billing_name = "";
    private String billing_email="";
    @Getter@Setter
    private String ext="";
    @Getter@Setter
    private String contactdetail="";
    @Getter@Setter
    private String brand="";
    @Getter@Setter
    private String firstIndustryCode;
    @Getter@Setter
    private String secondIndustryCode;
    
    private boolean adxbased=false;
    
    public boolean isAdxbased() {
        return adxbased;
    }
    public void setAdxbased(boolean adxbased) {
        this.adxbased = adxbased;
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
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public StatusIdEnum getStatus() {
        return status;
    }
    public void setStatus(StatusIdEnum status) {
        this.status = status;
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
    public int getDemandtype() {
        return demandtype;
    }
    public void setDemandtype(int demandtype) {
        this.demandtype = demandtype;
    }
    public int getDemandpreference() {
        return demandpreference;
    }
    public void setDemandpreference(int demandpreference) {
        this.demandpreference = demandpreference;
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
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static Account getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        Account entity = objectMapper.readValue(str, Account.class);
        return entity;

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

    
}
