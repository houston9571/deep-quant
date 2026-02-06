package com.optimus.utils;

import cn.hutool.core.util.StrUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneUtils {

    private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();


    public static boolean checkPhone(String zone, String phone) {
        if (!StrUtil.isBlank(zone) && !StrUtil.isBlank(phone)) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setCountryCode(Integer.parseInt(zone));
            phoneNumber.setNationalNumber(Long.parseLong(phone));
            return PHONE_NUMBER_UTIL.isValidNumber(phoneNumber);
        } else {
            return false;
        }
    }

}
