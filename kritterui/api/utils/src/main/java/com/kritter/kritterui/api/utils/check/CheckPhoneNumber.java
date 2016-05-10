package com.kritter.kritterui.api.utils.check;

import com.kritter.constants.error.ErrorEnum;

public class CheckPhoneNumber {
    public static ErrorEnum checkPhoneNumber(String phoneNumber){
        if(phoneNumber == null){
            return ErrorEnum.PHONE_NULL;
        }
        String tmpphoneNumber = phoneNumber.trim();
        if(tmpphoneNumber.equals("")){
            return ErrorEnum.PHONE_NULL;
        }
        for (char c : tmpphoneNumber.toCharArray()) {
            if (Character.isDigit(c)) {
                continue;
            }
            if (c != '.') {
                if (!(c == '-' || c == 'E' || c == '+' || c == '(' || c ==')')) {
                    return ErrorEnum.INVALID_PHONE;
                }
            }
        }
        return ErrorEnum.NO_ERROR;
    }
}
