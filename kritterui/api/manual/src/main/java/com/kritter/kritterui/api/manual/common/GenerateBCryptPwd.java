package com.kritter.kritterui.api.manual.common;

import org.mindrot.jbcrypt.BCrypt;

public class GenerateBCryptPwd {
public static String genpwd(String pwd){
    if(pwd != null){
      return BCrypt.hashpw(pwd, BCrypt.gensalt());
    }
    return null;
}

public static void main(String args[]){
    if(args.length == 1){
        System.out.println(args[0]);
        System.out.println(genpwd(args[0]));
    }else{
        System.out.println("Incorrect Usage");
    }
}
}
