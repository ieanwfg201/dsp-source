package com.kritter.utils.mma_loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.kritter.utils.dbconnector.DBConnector;

public class MMADataLoader {
    private static final String CTRL_A = String.valueOf((char)1);

    private void mma_statements(Connection con, String mma_category_code, String mma_category_name, String parentcode, int tier,
    		int mma_type){
    	////"mma_category_code^Amma_category_name^Aparentcode^Atier^Amma_type"
    	PreparedStatement selectmmacode = null;
    	PreparedStatement insertmmacode = null;
    	PreparedStatement selectuicode = null;
    	PreparedStatement insertuicode = null;
    	PreparedStatement selectmapping = null;
    	PreparedStatement insertmapping = null;
    	try{
    		selectmmacode = con.prepareStatement("select * from mma_categories where code=? and mma_type=?");
    		selectmmacode.setString(1, mma_category_code);
    		selectmmacode.setInt(2, mma_type);
    		ResultSet selectmmacodeRset = selectmmacode.executeQuery();
    		if(selectmmacodeRset.next()){
    		}else{
    			insertmmacode = con.prepareStatement("insert into mma_categories(code,name,parent_code,tier,mma_type) values(?,?,?,?,?)");
    			insertmmacode.setString(1, mma_category_code);
    			insertmmacode.setString(2, mma_category_name);
    			insertmmacode.setString(3, parentcode);
    			insertmmacode.setInt(4, tier);
    			insertmmacode.setInt(5, mma_type);
    			insertmmacode.executeUpdate();
    		}
    		selectuicode = con.prepareStatement("select * from ui_mma_category where name=? and mma_type=?");
    		selectuicode.setString(1, mma_category_name);
    		selectuicode.setInt(2, mma_type);
    		ResultSet selectuicodeRset = selectuicode.executeQuery();
    		int id =-1;
    		if(selectuicodeRset.next()){
    			id = selectuicodeRset.getInt("id");
    		}else{
    			insertuicode = con.prepareStatement("insert into ui_mma_category(name,tier,mma_type) values(?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
    			insertuicode.setString(1, mma_category_name);
    			insertuicode.setInt(2, tier);
    			insertuicode.setInt(3, mma_type);
    			insertuicode.executeUpdate();
                ResultSet keyResultSet = insertuicode.getGeneratedKeys();
                if (keyResultSet.next()) {
                	id = keyResultSet.getInt(1);
                }
    		}
    		selectmapping = con.prepareStatement("select * from mma_code_mma_ui_mapping where code=?");
    		selectmapping.setString(1, mma_category_code);
    		ResultSet selectmappingRset = selectmapping.executeQuery();
    		if(selectmappingRset.next()){
    		}else{
    			insertmapping = con.prepareStatement("insert into mma_code_mma_ui_mapping(code,ui_id) values(?,?)");
    			insertmapping.setString(1, mma_category_code);
    			insertmapping.setInt(2, id);
    			insertmapping.executeUpdate();
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
            if(selectmmacode != null){
            	selectmmacode.close();
            }
            if(insertmmacode != null){
            	insertmmacode.close();
            }
            if(selectuicode != null){
            	selectuicode.close();
            }
            if(insertuicode != null){
            	insertuicode.close();
            }
            if(selectmapping != null){
            	selectmapping.close();
            }
            if(insertmapping != null){
            	insertmapping.close();
            }
    		}catch(Exception e){
    			e.printStackTrace();
    		}

    	}
    }
	public void loadMMAData(String absoluteFullFilePath, String dbHost, String dbPort,
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
				if(strSplit.length != 5){
					System.out.println("Incorrect format: "+str);
				}else{
					//"mma_category_code^Amma_category_name^Aparentcode^Atier"
					String mma_category_code = strSplit[0];
					String mma_category_name = strSplit[1];
					String parentcode = strSplit[2];
					String tier = strSplit[3];
					String mma_typeStr = strSplit[4];
					int mma_type = 1;
					if(mma_category_code == null || mma_category_name == null ||
							tier == null){
						System.out.println("NULL NOT ALLOWED mma_category_code: "+mma_category_code+
								" mma_category_name: "+mma_category_name+" tier: "+tier);
					}else{
						mma_category_code = mma_category_code.trim();
						mma_category_name = mma_category_name.trim();
						int tierInt =1;
						boolean tierexception = false;
						boolean mmatypeexception = false;
						try{
							tierInt = Integer.parseInt(tier.trim());
						}catch(Exception e){
							tierexception = true;
							System.out.println("tier is not INTEGER: "+tier);
							e.printStackTrace();
						}
						try{
							mma_type = Integer.parseInt(mma_typeStr.trim());
						}catch(Exception e){
							mmatypeexception = true;
							System.out.println("mma_type is not INTEGER: "+mma_typeStr);
							e.printStackTrace();
						}
						if(!tierexception && !mmatypeexception){
							if("".equals(mma_category_code) || "".equals(mma_category_name) ||
								"".equals(tier)){
							System.out.println("EMPTY NOT ALLOWED mma_category_code: "+mma_category_code+
									" mma_category_name: "+mma_category_name+" tier: "+tier);
							}else{
								mma_statements(con, mma_category_code, mma_category_name, parentcode, tierInt,mma_type);
							}
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
			MMADataLoader mmaDL = new MMADataLoader();
			//mmaDL.loadMMAData(absoluteFullFilePath, dbHost, dbPort, dbUser, dbPwd, dbName, dbType);
			mmaDL.loadMMAData(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
		}
	}
}
