package com.kritter.utils.channel_loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.kritter.utils.dbconnector.DBConnector;

public class ChannelDataloader {
    private static final String CTRL_A = String.valueOf((char)1);

    private void channel_transaction(Connection con, String exchangename, String channelcode, String channelname, int tier, String parentcode){
    	PreparedStatement selectchannelcode = null;
    	PreparedStatement insertchannelcode = null;
    	try{
    		selectchannelcode = con.prepareStatement("select * from channel where exchangename=? and channelcode=?");
    		selectchannelcode.setString(1, exchangename);
    		selectchannelcode.setString(2, channelcode);
    		ResultSet selectchannelcodeRset = selectchannelcode.executeQuery();
    		if(selectchannelcodeRset.next()){
    		}else{
    			String parentCodeTemp = parentcode;
    			if(parentCodeTemp==null){
    				parentCodeTemp="";
    			}else{
    				parentCodeTemp = parentCodeTemp.trim();
    			}
    			insertchannelcode = con.prepareStatement("insert into channel(exchangename,channelcode,tier,parentcode,channelname) values(?,?,?,?,?)");
    			insertchannelcode.setString(1, exchangename);
    			insertchannelcode.setString(2, channelcode);
    			insertchannelcode.setInt(3, tier);
    			insertchannelcode.setString(4, parentCodeTemp);
    			insertchannelcode.setString(5, channelname);
    			insertchannelcode.executeUpdate();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
                if(selectchannelcode != null){
                	selectchannelcode.close();
                }
                if(insertchannelcode != null){
                	insertchannelcode.close();
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
				//exchangename^Achannelcode^Aparentcode^Atier
				if(strSplit.length != 5){
					System.out.println("Incorrect format: "+str);
				}else{
					String exchangename = strSplit[0];
					String channelcode = strSplit[1];
					String channelname = strSplit[2];
					String parentcode = strSplit[3];
					String tier = strSplit[4];
					if(exchangename == null || channelcode == null || tier == null){
						System.out.println("NULL NOT ALLOWED exchangename: "+exchangename+
								" channelcode: "+channelcode+" tier: "+tier);
					}else{
						exchangename = exchangename.trim();
						channelcode = channelcode.trim();
						tier = tier.trim();
						if("".equals(exchangename) || "".equals(channelcode) ||
								"".equals(tier)){
							System.out.println("EMPTY NOT ALLOWED exchangename: "+exchangename+
									" channelcode: "+channelcode+" tier: "+tier);
							}else{
								try{
									int t = Integer.parseInt(tier);
									if(t==2 && (parentcode == null || "".equals(parentcode.trim()))){
										System.out.println("Tier 2 channel code cannot have parent code null exchangename: "+exchangename+
												" channelcode: "+channelcode+" tier: "+tier);
									}else{
										channel_transaction(con, exchangename, channelcode,channelname, t, parentcode);
									}
								}catch(Exception e){
									System.out.println("TIER Not Number exchangename: "+exchangename+
											" channelcode: "+channelcode+" tier: "+tier);
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
			ChannelDataloader mmaDL = new ChannelDataloader();
			//mmaDL.loadSupplyData(absoluteFullFilePath, dbHost, dbPort, dbUser, dbPwd, dbName, dbType);
			mmaDL.loadSupplyData(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
		}

	}
}
