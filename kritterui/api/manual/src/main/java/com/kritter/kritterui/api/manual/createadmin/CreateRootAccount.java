package com.kritter.kritterui.api.manual.createadmin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Account_Type;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.kritterui.api.manual.common.GenerateBCryptPwd;
import com.kritter.utils.dbconnector.DBConnector;

public class CreateRootAccount {
    private static void createRoot(String userid, String pwd, String dbtype,String dbhost, String dbport, String dbname, String dbuser, String dbpwd){
    	String username="NewUser"+System.currentTimeMillis();
        Logger log = Logger.getLogger("my.logger");
        log.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        log.addHandler(handler);
        Account account = new Account();
        account.setUserid(userid);
        account.setPassword(GenerateBCryptPwd.genpwd(pwd));
        account.setAddress("");
        account.setCity("");
        account.setCompany_name("");
        account.setCountry("");
        account.setEmail("newuser@newuser.com");
        account.setModified_by(1);
        account.setName(username);
        account.setPhone("9999999999");
        account.setStatus(StatusIdEnum.Active);
        account.setType_id(Account_Type.root);
        Connection con = null;
        try {
            con = DBConnector.getConnection(dbtype, dbhost, dbport, dbname,  dbuser,  dbpwd);
            System.out.println(dbtype+":"+dbhost+":"+dbport+":"+dbname+":"+dbuser+":"+dbpwd);
            Message msg = ApiDef.verifyAccount(con, account);
            if(msg.getError_code() != 0){
            	System.out.println("Please Retry");
            	System.out.println(msg.getMsg());
            }else{
            	msg = ApiDef.createAccount(con, account);
            	System.out.println("User Created");
            	System.out.println("User Name: "+username);
            	System.out.println(msg.getError_code());
            	System.out.println(msg.getMsg());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(con != null){
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
       
    }
    
    
    public static void main(String args[]){
       // createadmin("rohan1","rohan1","MYSQL","localhost","3306","kritter","root","password");
        if(args.length != 8){
            System.out.println("######################INCORRECT USAGE#################################");
            System.out.println("java -cp <classpath> com.kritter.kritterui.api.manual.createadmin.CreateRootAccount <userid> <userpwd> <dbtype> <dbhost> <dbport> <dbname> <dbuser> <dbpwd>");
            System.out.println("######################################################################");
        }else{
        	createRoot(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);
            
        }
    }


}
