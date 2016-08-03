package com.kritter.utils.mma_loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.kritter.utils.dbconnector.DBConnector;

public class SupplyDataloader {
    private static final String CTRL_A = String.valueOf((char)1);

    private void supply_transation(Connection con, String exchangename, String supplycode, String mma_category_code){
    	PreparedStatement selectsupplycode = null;
    	PreparedStatement insertsupplycode = null;
    	try{
    		selectsupplycode = con.prepareStatement("select * from supply_mma_mapping where exchangename=? and supplycode=?");
    		selectsupplycode.setString(1, exchangename);
    		selectsupplycode.setString(2, supplycode);
    		ResultSet selectsupplycodeRset = selectsupplycode.executeQuery();
    		if(selectsupplycodeRset.next()){
    		}else{
    			insertsupplycode = con.prepareStatement("insert into supply_mma_mapping(exchangename,supplycode,mma_category_code) values(?,?,?)");
    			insertsupplycode.setString(1, exchangename);
    			insertsupplycode.setString(2, supplycode);
    			insertsupplycode.setString(3, mma_category_code);
    			insertsupplycode.executeUpdate();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
                if(selectsupplycode != null){
                	selectsupplycode.close();
                }
                if(insertsupplycode != null){
                	insertsupplycode.close();
                }
        		}catch(Exception e){
        			e.printStackTrace();
        		}
    	}
    }
    
	public void loadSupplyData(String absoluteFullFilePath, String dbHost, String dbPort,
			String dbUser, String dbPwd, String dbName , String dbType) throws Exception{
		if(absoluteFullFilePath == null){
			System.out.println("absoluteFullFilePath is Null");
			return;
		}
		if(dbHost == null){
			System.out.println("dbHost is Null");
			return;
		}
		if(dbPort == null){
			System.out.println("dbPort is Null");
			return;
		}
		if(dbUser == null){
			System.out.println("dbUser is Null");
			return;
		}
		if(dbPwd == null){
			System.out.println("dbPwd is Null");
			return;
		}
		BufferedReader br =  null;
		FileReader fr = null;
		Connection con = null;
		boolean autoCommit = true;
		try{
			fr = new FileReader(new File(absoluteFullFilePath));
			br = new BufferedReader(fr);
			String str = null;
            con = DBConnector.getConnection(dbType, dbHost,  dbPort, dbName,  dbUser,  dbPwd);
            autoCommit  = con.getAutoCommit();
            con.setAutoCommit(false);
			while((str= br.readLine()) != null){
				String strSplit[] =  str.split(CTRL_A);
				//exchangename^Asupplycode^Amma_category_codeparentcode
				if(strSplit.length != 3){
					System.out.println("Incorrect format: "+str);
				}else{
					String exchangename = strSplit[0];
					String supplycode = strSplit[1];
					String mma_category_code = strSplit[2];
					if(exchangename == null || supplycode == null){
						System.out.println("NULL NOT ALLOWED exchangename: "+exchangename+
								" supplycode: "+supplycode+" mma_category_code: "+mma_category_code);
					}else{
						exchangename = exchangename.trim();
						supplycode = supplycode.trim();
						mma_category_code = mma_category_code.trim();
						if("".equals(exchangename) || "".equals(supplycode) ||
								"".equals(mma_category_code)){
							System.out.println("EMPTY NOT ALLOWED exchangename: "+exchangename+
									" supplycode: "+supplycode+" mma_category_code: "+mma_category_code);
							}else{
								supply_transation(con, exchangename, supplycode, mma_category_code);
							}

					}
				}
			}
			con.commit();
		}catch(Exception e){
			con.rollback();
			e.printStackTrace();
		}finally{
			con.setAutoCommit(autoCommit);
			if (con != null) {
				con.close();
			}
			if(fr == null){
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(br ==null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String args[]) throws Exception{
		if(args.length != 7){
			System.out.println("Incorrect Usage: 7 arguments required");
		}else{
			SupplyDataloader mmaDL = new SupplyDataloader();
			//mmaDL.loadSupplyData(absoluteFullFilePath, dbHost, dbPort, dbUser, dbPwd, dbName, dbType);
			mmaDL.loadSupplyData(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
		}

	}
}
