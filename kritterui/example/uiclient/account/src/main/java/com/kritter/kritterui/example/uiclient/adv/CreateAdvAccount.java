package com.kritter.kritterui.example.uiclient.adv;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.DemandType;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class CreateAdvAccount {
	/**
	 * Input 
	 * com.kritter.api.entity.account.Account  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"71"}   {Where id is the unique internal id of advertiser }
	 * Failure E.G
	 * {"msg":"SQL Exception","error_code":4,"id":null}
	 * 
	 * NOTE: We should verify account before calling create
	 */
    public static Message createDirectAdvertiser(String apiUrl) throws Exception{
        Account account = new Account();
        /**
         * Below Required
         */
        account.setAddress("address");
		account.setDemandtype(DemandType.DIRECT.getCode()); /*Always direct*/
        /*Above can also be obtained by /api/v1/meta/firstlevel/demandtype  */

        account.setPhone("9999");
        account.setUserid("uniqueadvid1");
        account.setPassword((BCrypt.hashpw("mypassword", BCrypt.gensalt())));
        account.setModified_by(1); /*Ask for this id */
        account.setName("myname");
        account.setEmail("uniqueadvemail1@email.com");
        account.setCity("Benagaluru");
        account.setCompany_name("company_name");
        account.setCountry("India"); /*Name from MetadatList*/
        /**
         * Default Selected if not Populated
        account.setBilling_rules_json(billing_rules_json);
         */

        
        /**
         * Auto Populated
        account.setApi_key(api_key);
        account.setLast_modified(last_modified);
        account.setCreated_on(created_on);
		account.setType_id(type_id);
		account.setStatus(StatusIdEnum.Pending);
         */
        
        /*
         * Optional
         */
        
        /*
         * Optional
       	account.setBilling_email(billing_email);
        account.setBilling_name(billing_name);
		account.setIm(im);

        */
        /**
         * Not Required
        account.setBilling_rules_json("70");;
		account.setExt(ext);
        account.setGuid(guid);
        account.setId(id);
        account.setTimeout(timeout);
        account.setComment(comment);
        account.setDemand_url(demand_url);
        account.setQps(qps);
        account.setDemandProps(demandProps);
        account.setFirstIndustryCode(firstIndustryCode);
        account.setSecondIndustryCode(secondIndustryCode);
        account.setBrand(brand);
        account.setContactdetail(contactdetail);
        
        account.setPayment_type(payment_type);
		account.setBank_transfer_account_number(bank_transfer_account_number);
        account.setBank_transfer_bank_add(bank_transfer_bank_add);
        account.setBank_transfer_bank_name(bank_transfer_bank_name);
        account.setBank_transfer_beneficiary_name(bank_transfer_beneficiary_name);
        account.setBank_transfer_branch_number(bank_transfer_branch_number);
        account.setBank_transfer_vat_number(bank_transfer_vat_number);
		account.setWire_account_number(wire_account_number);
        account.setWire_bank_name(wire_bank_name);
        account.setWire_beneficiary_name(wire_beneficiary_name);
        account.setWire_iban(wire_iban);
        account.setWire_swift_code(wire_swift_code);
        account.setWire_transfer_bank_add(wire_transfer_bank_add);
        account.setPaypal_id(paypal_id);
        account.setInventory_source(INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode());  
        account.setDemandpreference(DemandPreference.DIRECT.getCode());



         */
        
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, account.toJson());
        wr.flush();
        wr.close();
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Message msg = Message.getObject(response.toString());
        
        System.out.println(msg.toJson());
        return msg;
    }
    
    public static void main(String args[]) throws Exception{
        CreateAdvAccount.createDirectAdvertiser(ExampleConstant.url_prefix+ExampleConstant.create_adv);
    }
}
