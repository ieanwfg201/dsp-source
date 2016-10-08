package com.kritter.kritterui.api.account;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountList;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Account_Type;
import com.kritter.constants.Payment_type;
import com.kritter.constants.Payout;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.demand_props.DemandProps;
import com.kritter.kritterui.api.utils.check.CheckPhoneNumber;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;



public class AccountCrud {
    private static final Logger LOG = LoggerFactory.getLogger(AccountCrud.class);
    
    public static String payout_percentage_to_billing_rule_json(String payout_percentage){
        if(payout_percentage != null){
            String tmp_payout_percentage = payout_percentage.trim();
            if(!tmp_payout_percentage.equals("")){
                return "{\"payout\":"+payout_percentage+"}";
            }
        }
        return "{\"payout\":"+Payout.default_payout_percent+"}";
    }
    public static String billing_rule_json_to_payout_percentage(String billing_rule_json){
        if(billing_rule_json != null){
            String tmp_billing_rule_json = billing_rule_json.trim();
            if(!tmp_billing_rule_json.equals("")){
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode node = mapper.readValue(tmp_billing_rule_json,JsonNode.class);
                    JsonNode payout = node.get("payout");
                    if(payout != null){
                        return payout.getValueAsText();
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
        return Payout.default_payout_percent_str;
    }
    
    public static void populateAccount(Account account, ResultSet rset) throws Exception{
        if(account != null && rset != null){
            account.setId(rset.getInt("id"));
            account.setGuid(rset.getString("guid"));
            account.setAddress(rset.getString("address"));
            account.setCity(rset.getString("city"));
            account.setCompany_name(rset.getString("company_name"));
            account.setCountry(rset.getString("country"));
            account.setEmail(rset.getString("email"));
            account.setName(rset.getString("name"));
            account.setPhone(rset.getString("phone"));
            account.setStatus(StatusIdEnum.getEnum(rset.getInt("status")));
            account.setUserid(rset.getString("userid"));
            account.setBank_transfer_account_number(rset.getString("bank_transfer_account_number"));
            account.setBank_transfer_bank_add(rset.getString("bank_transfer_bank_add"));
            account.setBank_transfer_bank_name(rset.getString("bank_transfer_bank_name"));
            account.setBank_transfer_beneficiary_name(rset.getString("bank_transfer_beneficiary_name"));
            account.setBank_transfer_branch_number(rset.getString("bank_transfer_branch_number"));
            account.setBank_transfer_vat_number(rset.getString("bank_transfer_vat_number"));
            account.setPassword(rset.getString("password"));
            account.setPayment_type(Payment_type.getEnum(rset.getInt("payment_type")));
            account.setWire_account_number(rset.getString("wire_account_number"));
            account.setWire_bank_name(rset.getString("wire_bank_name"));
            account.setWire_beneficiary_name(rset.getString("wire_beneficiary_name"));
            account.setWire_iban(rset.getString("wire_iban"));
            account.setWire_swift_code(rset.getString("wire_swift_code"));
            account.setWire_transfer_bank_add(rset.getString("wire_transfer_bank_add"));
            account.setPaypal_id(rset.getString("paypal_id"));
            account.setType_id(Account_Type.getEnum(rset.getInt("type_id")));
            account.setIm(rset.getString("im"));
            account.setComment(rset.getString("comment"));
            account.setApi_key(rset.getString("api_key"));
            account.setInventory_source(rset.getInt("inventory_source"));
            account.setBilling_rules_json(billing_rule_json_to_payout_percentage(rset.getString("billing_rules_json")));
            account.setDemandtype(rset.getInt("demandtype"));
            account.setDemandpreference(rset.getInt("demandpreference"));
            account.setQps(rset.getInt("qps"));
            account.setTimeout(rset.getInt("timeout"));
            account.setAdxbased(rset.getBoolean("adxbased"));
            String demandProps = rset.getString("demand_props");
            if(demandProps != null){
                String demandPropsTrim = demandProps.trim();
                if(!"".equals(demandPropsTrim)){
                    DemandProps dPropsEntity = DemandProps.getObject(demandPropsTrim);
                    if(dPropsEntity != null){
                        account.setDemandProps(dPropsEntity);
                        if(dPropsEntity.getDemand_url() != null){
                            account.setDemand_url(dPropsEntity.getDemand_url());
                        }
                    }
                }
            }
            account.setBilling_name(rset.getString("billing_name"));
            account.setBilling_email(rset.getString("billing_email"));
            String ext = rset.getString("ext");
            if(ext != null){
                account.setExt(ext);
            }
            account.setContactdetail(rset.getString("contactdetail"));
            account.setBrand(rset.getString("brand"));
            account.setFirstIndustryCode(rset.getString("firstind"));
            account.setSecondIndustryCode(rset.getString("secondind"));
        }
    }
    private static String generateDemandProps(Account account){
        DemandProps demandProps = new DemandProps();
        if(account != null){
            demandProps.setDemand_url(account.getDemand_url());
            return demandProps.toJson().toString();
        }
        return "";
    }
    
    private static String generate_api_key(String salt){
        return DigestUtils.sha1Hex(salt);
    }
    public static JsonNode verifyAccount(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.treeToValue(jsonNode, Account.class);
            return verifyAccount(con, account).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message verifyAccount(Connection con,Account account){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(account == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        try{
            boolean checkEmail = false;
            if(account.getEmail() != null){
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.verify_userId_email);
                pstmt.setString(1, account.getUserid());
                pstmt.setString(2, account.getEmail());
                checkEmail = true;
            }else{
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.verify_userId);
                pstmt.setString(1, account.getUserid());
            }
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                if(checkEmail){
                    if(account.getEmail() != null && account.getEmail().equals(rset.getString("email"))){
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EMAIL_ALREADY_PRESENT.getId());
                        msg.setMsg(ErrorEnum.EMAIL_ALREADY_PRESENT.getName());
                        return msg;
                    }
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.USERID_ALREADY_TAKEN.getId());
                    msg.setMsg(ErrorEnum.USERID_ALREADY_TAKEN.getName());
                    return msg;
                }else{
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.USERID_ALREADY_TAKEN.getId());
                    msg.setMsg(ErrorEnum.USERID_ALREADY_TAKEN.getName());
                    return msg;
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }
    public static JsonNode createAccount(Connection con, JsonNode jsonNode) {
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg.toJson();
        }
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.treeToValue(jsonNode, Account.class);
            return createAccount(con, account,true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message createAccount(Connection con,Account account, boolean createTransaction){

        return createAccount(con,account,createTransaction,false);
    }

    public static Message createAccount(Connection con,Account account, boolean createTransaction,boolean useProvidedGuid){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(account == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            return msg;
        }
        ErrorEnum errorEnum = CheckPhoneNumber.checkPhoneNumber(account.getPhone());
        if(errorEnum != ErrorEnum.NO_ERROR){
            Message msg = new Message();
            msg.setError_code(errorEnum.getId());
            msg.setMsg(errorEnum.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.create_account);
            String generated_guid =SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();

            if(useProvidedGuid)
                pstmt.setString(1,account.getGuid());
            else
                pstmt.setString(1, generated_guid);

            pstmt.setInt(2, account.getStatus().getCode());
            pstmt.setInt(3, account.getType_id().getId());
            pstmt.setString(4, account.getName());
            pstmt.setString(5, account.getUserid());
            pstmt.setString(6, account.getPassword());
            pstmt.setString(7, account.getEmail());
            pstmt.setString(8, account.getAddress());
            pstmt.setString(9, account.getCountry());
            pstmt.setString(10, account.getCity());
            pstmt.setString(11, account.getPhone());
            pstmt.setString(12, account.getCompany_name());
            pstmt.setInt(13, account.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(14, ts);
            pstmt.setInt(15, account.getPayment_type().getCode());
            pstmt.setString(16, account.getBank_transfer_beneficiary_name());
            pstmt.setString(17,account.getBank_transfer_account_number() );
            pstmt.setString(18, account.getBank_transfer_bank_name());
            pstmt.setString(19, account.getBank_transfer_bank_add());
            pstmt.setString(20, account.getBank_transfer_branch_number());
            pstmt.setString(21, account.getBank_transfer_vat_number());
            pstmt.setString(22, account.getWire_beneficiary_name());
            pstmt.setString(23, account.getWire_account_number());
            pstmt.setString(24, account.getWire_bank_name());
            pstmt.setString(25, account.getWire_transfer_bank_add());
            pstmt.setString(26, account.getWire_swift_code());
            pstmt.setString(27, account.getWire_iban());
            pstmt.setString(28, account.getPaypal_id());
            pstmt.setString(29, account.getIm());
            pstmt.setString(30, account.getComment());
            if(account.getApi_key() == null || "".equals(account.getApi_key())){
                {
                    String guidForKey = generated_guid;
                    if(useProvidedGuid)
                        guidForKey = account.getGuid();

                    account.setApi_key(generate_api_key(guidForKey+ts.getTime()));
                }
                pstmt.setString(31, account.getApi_key());
            }else{
                pstmt.setString(31, account.getApi_key());
            }
            pstmt.setInt(32, account.getInventory_source());
            pstmt.setString(33, payout_percentage_to_billing_rule_json(account.getBilling_rules_json()));
            pstmt.setInt(34, account.getDemandtype());
            pstmt.setInt(35, account.getDemandpreference());
            pstmt.setInt(36, account.getQps());
            pstmt.setInt(37, account.getTimeout());
            pstmt.setString(38, generateDemandProps(account));
            pstmt.setString(39, account.getBilling_name());
            pstmt.setString(40, account.getBilling_email());
            pstmt.setString(41, account.getExt());
            pstmt.setBoolean(42, account.isAdxbased());
            pstmt.setInt(43,account.getOpen_rtb_ver_required());
            pstmt.setInt(44,account.getThird_party_demand_channel_type());
            pstmt.setString(45, account.getContactdetail());
            pstmt.setString(46, account.getBrand());
            pstmt.setString(47, account.getFirstIndustryCode());
            pstmt.setString(48, account.getSecondIndustryCode());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ACCOUNT_NOT_CREATED.getId());
                msg.setMsg(ErrorEnum.ACCOUNT_NOT_CREATED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            if(createTransaction){
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }
    
    public static JsonNode listAccount(Connection con, JsonNode jsonNode) {
        if(jsonNode == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            com.kritter.api.entity.account.ListEntity listEntity = objectMapper.treeToValue(jsonNode, com.kritter.api.entity.account.ListEntity.class);
            return listAccount(con, listEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
    }
    
    public static AccountList listAccount(Connection con,com.kritter.api.entity.account.ListEntity listEntity){
        if(con == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            accList.setMsg(msg);
            return accList;
        }
        if(listEntity == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            accList.setMsg(msg);
            return accList;
        }
        PreparedStatement pstmt = null;
        try{
            if(listEntity.getAccount_type() == null){
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.list_all_accounts);
                pstmt.setInt(1, listEntity.getPage_no()*listEntity.getPage_size());
                pstmt.setInt(2, listEntity.getPage_size());
            }else{
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.list_accounts_by_type);
                pstmt.setInt(1, listEntity.getAccount_type().getId());
                pstmt.setInt(2, listEntity.getPage_no()*listEntity.getPage_size());
                pstmt.setInt(3, listEntity.getPage_size());
            }
            ResultSet rset = pstmt.executeQuery();
            AccountList accList = new AccountList();
            List<Account> account_list = new LinkedList<Account>(); 
            while(rset.next()){
                Account account = new Account();
                populateAccount(account, rset);
                account_list.add(account);
            }
            
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            accList.setMsg(msg);
            accList.setAccount_list(account_list);
            return accList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }
    public static JsonNode various_get_account(Connection con, JsonNode jsonNode) {
        if(jsonNode == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            com.kritter.api.entity.account.ListEntity listEntity = objectMapper.treeToValue(jsonNode, com.kritter.api.entity.account.ListEntity.class);
            return various_get_account(con, listEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
    }
    
    public static AccountList various_get_account(Connection con,com.kritter.api.entity.account.ListEntity listEntity){
        if(con == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            accList.setMsg(msg);
            return accList;
        }
        if(listEntity == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            accList.setMsg(msg);
            return accList;
        }
        PreparedStatement pstmt = null;
        try{
            switch(listEntity.getAccountAPIEnum()){
                case list_active_advertiser_by_demandtype:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.list_active_advertiser_by_demandtype);
                    pstmt.setInt(1, listEntity.getDemandType().getCode());
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            AccountList accList = new AccountList();
            List<Account> account_list = new LinkedList<Account>(); 
            while(rset.next()){
                Account account = new Account();
                populateAccount(account, rset);
                account_list.add(account);
            }
            
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            accList.setMsg(msg);
            accList.setAccount_list(account_list);
            return accList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }
    
    public static JsonNode listAccountByStatus(Connection con, JsonNode jsonNode) {
        if(jsonNode == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            com.kritter.api.entity.account.ListEntity listEntity = objectMapper.treeToValue(jsonNode, com.kritter.api.entity.account.ListEntity.class);
            return listAccountByStatus(con, listEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
    }
    
    public static AccountList listAccountByStatus(Connection con,com.kritter.api.entity.account.ListEntity listEntity){
        if(con == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            accList.setMsg(msg);
            return accList;
        }
        if(listEntity == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            accList.setMsg(msg);
            return accList;
        }
        PreparedStatement pstmt = null;
        try{
            if(listEntity.getAccount_type() == null){
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.list_all_accounts_by_status);
                pstmt.setInt(1, listEntity.getStatus().getCode());
                pstmt.setInt(2, listEntity.getPage_no()*listEntity.getPage_size());
                pstmt.setInt(3, listEntity.getPage_size());
            }else{
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.list_accounts_by_type_by_status);
                pstmt.setInt(1, listEntity.getAccount_type().getId());
                pstmt.setInt(2, listEntity.getStatus().getCode());
                pstmt.setInt(3, listEntity.getPage_no()*listEntity.getPage_size());
                pstmt.setInt(4, listEntity.getPage_size());
            }
            ResultSet rset = pstmt.executeQuery();
            AccountList accList = new AccountList();
            List<Account> account_list = new LinkedList<Account>(); 
            while(rset.next()){
                Account account = new Account();
                populateAccount(account, rset);
                account_list.add(account);
            }
            
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            accList.setMsg(msg);
            accList.setAccount_list(account_list);
            return accList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }

    
    public static JsonNode listExchangesByStatus(Connection con, JsonNode jsonNode) {
        if(jsonNode == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            com.kritter.api.entity.account.ListEntity listEntity = objectMapper.treeToValue(jsonNode, com.kritter.api.entity.account.ListEntity.class);
            return listExchangesByStatus(con, listEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
    }
    
    public static AccountList listExchangesByStatus(Connection con,com.kritter.api.entity.account.ListEntity listEntity){
        if(con == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            accList.setMsg(msg);
            return accList;
        }
        if(listEntity == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            accList.setMsg(msg);
            return accList;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.list_exchanges_by_status);
            pstmt.setInt(1, listEntity.getStatus().getCode());
            pstmt.setInt(2, listEntity.getPage_no()*listEntity.getPage_size());
            pstmt.setInt(3, listEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            AccountList accList = new AccountList();
            List<Account> account_list = new LinkedList<Account>(); 
            while(rset.next()){
                Account account = new Account();
                populateAccount(account, rset);
                account_list.add(account);
            }
            
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            accList.setMsg(msg);
            accList.setAccount_list(account_list);
            return accList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }

    public static JsonNode listDirectPublisherByStatus(Connection con, JsonNode jsonNode) {
        if(jsonNode == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_LIST_ENTITY_NULL.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            com.kritter.api.entity.account.ListEntity listEntity = objectMapper.treeToValue(jsonNode, com.kritter.api.entity.account.ListEntity.class);
            return listDirectPublisherByStatus(con, listEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList.toJson();
        }
    }
    
    public static AccountList listDirectPublisherByStatus(Connection con,com.kritter.api.entity.account.ListEntity listEntity){
        if(con == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            accList.setMsg(msg);
            return accList;
        }
        if(listEntity == null){
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            accList.setMsg(msg);
            return accList;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.list_directpublisher_by_status);
            pstmt.setInt(1, listEntity.getStatus().getCode());
            pstmt.setInt(2, listEntity.getPage_no()*listEntity.getPage_size());
            pstmt.setInt(3, listEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            AccountList accList = new AccountList();
            List<Account> account_list = new LinkedList<Account>(); 
            while(rset.next()){
                Account account = new Account();
                populateAccount(account, rset);
                account_list.add(account);
            }
            
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            accList.setMsg(msg);
            accList.setAccount_list(account_list);
            return accList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountList accList = new AccountList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            accList.setMsg(msg);
            return accList;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }
    public static JsonNode updateAccount(Connection con, JsonNode jsonNode) {
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.treeToValue(jsonNode, Account.class);
            return updateAccount(con, account,true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message updateAccount(Connection con,Account account, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(account == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            return msg;
        }
        ErrorEnum errorEnum = CheckPhoneNumber.checkPhoneNumber(account.getPhone());
        if(errorEnum != ErrorEnum.NO_ERROR){
            Message msg = new Message();
            msg.setError_code(errorEnum.getId());
            msg.setMsg(errorEnum.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.update_accounts);
            pstmt.setInt(1, account.getStatus().getCode());
            pstmt.setString(2, account.getName());
            pstmt.setString(3, account.getEmail());
            pstmt.setString(4, account.getAddress());
            pstmt.setString(5, account.getCountry());
            pstmt.setString(6, account.getCity());
            pstmt.setString(7, account.getPhone());
            pstmt.setString(8, account.getCompany_name());
            pstmt.setInt(9, account.getModified_by());
            Timestamp timestamp = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(10, timestamp);
            pstmt.setInt(11,account.getPayment_type().getCode());
            pstmt.setString(12, account.getBank_transfer_beneficiary_name());
            pstmt.setString(13, account.getBank_transfer_account_number());
            pstmt.setString(14, account.getBank_transfer_bank_name());
            pstmt.setString(15, account.getBank_transfer_bank_add());
            pstmt.setString(16, account.getBank_transfer_branch_number());
            pstmt.setString(17, account.getBank_transfer_vat_number());
            pstmt.setString(18, account.getWire_beneficiary_name());
            pstmt.setString(19, account.getWire_account_number());
            pstmt.setString(20, account.getWire_bank_name());
            pstmt.setString(21, account.getWire_transfer_bank_add());
            pstmt.setString(22, account.getWire_swift_code());
            pstmt.setString(23, account.getWire_iban());
            pstmt.setString(24, account.getPaypal_id());
            pstmt.setString(25, account.getIm());
            pstmt.setString(26, account.getComment());
            if(account.getApi_key() == null || "".equals(account.getApi_key())){
                account.setApi_key(generate_api_key(account.getGuid()+timestamp.getTime()));
            }
            pstmt.setString(27, account.getApi_key());
            pstmt.setInt(28, account.getInventory_source());
            pstmt.setString(29, payout_percentage_to_billing_rule_json(account.getBilling_rules_json()));
            pstmt.setString(30, account.getPassword());
            pstmt.setInt(31, account.getDemandtype());
            pstmt.setInt(32, account.getDemandpreference());
            pstmt.setInt(33, account.getQps());
            pstmt.setInt(34, account.getTimeout());
            pstmt.setString(35, generateDemandProps(account));
            pstmt.setString(36, account.getBilling_name());
            pstmt.setString(37, account.getBilling_email());
            pstmt.setString(38, account.getExt());
            pstmt.setBoolean(39, account.isAdxbased());
            pstmt.setInt(40,account.getOpen_rtb_ver_required());
            pstmt.setInt(41,account.getThird_party_demand_channel_type());
            pstmt.setString(42, account.getContactdetail());
            pstmt.setString(43, account.getBrand());
            pstmt.setString(44, account.getFirstIndustryCode());
            pstmt.setString(45, account.getSecondIndustryCode());
            pstmt.setInt(46, account.getId());

            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ACCOUNT_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.ACCOUNT_NOT_UPDATED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            if(createTransaction){
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        } 
    }

    public static JsonNode get_Account(Connection con, JsonNode jsonNode){
        if(con == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
        if(jsonNode == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.treeToValue(jsonNode, Account.class);
            return get_Account(con, account).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
    }
    public static AccountMsgPair get_Account(Connection con,Account account){
        if(con == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp;
        }
        if(account == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.get_Account);
            pstmt.setString(1, account.getUserid());
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                AccountMsgPair amp = new AccountMsgPair();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                populateAccount(account, rset);
                amp.setAccount(account);
                amp.setMsg(msg);
                return amp;
            }
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.USERIDINCORRECT.getId());
            msg.setMsg(ErrorEnum.USERIDINCORRECT.getName());
            amp.setMsg(msg);
            return amp;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }

    public static JsonNode get_Account_By_Guid(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.treeToValue(jsonNode, Account.class);
            return get_Account_By_Guid(con, account).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
    }

    public static Account get_Account_By_Guid(Connection con, String guid){
        try{
            Account account = new Account();
            account.setGuid(guid);
            return get_Account_By_Guid(con, account).getAccount();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }
    }

    public static Account get_Account_By_Id(Connection con, int id){
        try{
            Account account = new Account();
            account.setId(id);
            return get_Account_By_Id(con, account).getAccount();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }
    }

    public static AccountMsgPair get_Account_By_Guid(Connection con,Account account){
        if(con == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp;
        }
        if(account == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.get_Account_By_Guid);
            pstmt.setString(1, account.getGuid());
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                AccountMsgPair amp = new AccountMsgPair();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                populateAccount(account, rset);
                amp.setAccount(account);
                amp.setMsg(msg);
                return amp;
            }
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.USERIDINCORRECT.getId());
            msg.setMsg(ErrorEnum.USERIDINCORRECT.getName());
            amp.setMsg(msg);
            return amp;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }

    public static AccountMsgPair get_Account_By_Id(Connection con,Account account){
        if(con == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp;
        }
        if(account == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.get_Account_By_Id);
            pstmt.setInt(1, account.getId());
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                AccountMsgPair amp = new AccountMsgPair();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                populateAccount(account, rset);
                amp.setAccount(account);
                amp.setMsg(msg);
                return amp;
            }
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.USERIDINCORRECT.getId());
            msg.setMsg(ErrorEnum.USERIDINCORRECT.getName());
            amp.setMsg(msg);
            return amp;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }

    public static JsonNode updateStatus(Connection con, JsonNode jsonNode) {
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.treeToValue(jsonNode, Account.class);
            return updateStatus(con, account,true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message updateStatus(Connection con,Account account, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(account == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.update_status);
            pstmt.setInt(1, account.getStatus().getCode());
            if(account.getComment() != null){
                pstmt.setString(2,account.getComment());
            }else{
                pstmt.setString(2,"");
            }
            pstmt.setTimestamp(3, new Timestamp((new Date()).getTime()));
            pstmt.setInt(4, account.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ACCOUNT_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.ACCOUNT_NOT_UPDATED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            if(createTransaction){
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        } 
    }
    public static JsonNode get_Account_By_Guid_Apikey(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.treeToValue(jsonNode, Account.class);
            return get_Account_By_Guid_Apikey(con, account).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
    }
    public static AccountMsgPair get_Account_By_Guid_Apikey(Connection con,Account account){
        if(con == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp;
        }
        if(account == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.get_Account_By_Guid_Apikey);
            pstmt.setString(1, account.getGuid());
            pstmt.setString(2, account.getApi_key());
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                AccountMsgPair amp = new AccountMsgPair();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                populateAccount(account, rset);
                amp.setAccount(account);
                amp.setMsg(msg);
                return amp;
            }
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.USERIDINCORRECT.getId());
            msg.setMsg(ErrorEnum.USERIDINCORRECT.getName());
            amp.setMsg(msg);
            return amp;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }
    public static JsonNode get_Account_By_UserId_Pwd(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.treeToValue(jsonNode, Account.class);
            return get_Account_By_UserId_Pwd(con, account).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
    }
    public static AccountMsgPair get_Account_By_UserId_Pwd(Connection con,Account account){
        if(con == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp;
        }
        if(account == null){
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp;
        }
        PreparedStatement pstmt = null;
        try{
            String inputpwd = account.getPassword();
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.get_Account_By_UserId_Pwd);
            pstmt.setString(1, account.getUserid());
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                AccountMsgPair amp = new AccountMsgPair();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                populateAccount(account, rset);
                if(BCrypt.checkpw(inputpwd, account.getPassword())){
                    amp.setAccount(account);
                    amp.setMsg(msg);
                    return amp;
                }
            }
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AUTH_CREDENTIALS_INVALID.getId());
            msg.setMsg(ErrorEnum.AUTH_CREDENTIALS_INVALID.getName());
            amp.setMsg(msg);
            return amp;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AccountMsgPair amp = new AccountMsgPair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }

    public static Account getAccount(Connection connection, String accountGuid)
    {
        PreparedStatement pstmt = null;
        try{
            Account account = null;

            pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Account.get_Account_By_Guid);
            pstmt.setString(1, accountGuid);
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                account = new Account();
                populateAccount(account, rset);
                return account;
            }
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }

        return null;
    }
}
