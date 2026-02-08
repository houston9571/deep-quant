package com.optimus.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DateFormatEnum {

    TIME("HH:mm:ss"),
    YEAR("yyyy"),
    MONTH("yyyyMM"),
    DAY("yyyyMMdd"),
    WEEK("E"),
    YEAR_MONTH("yyyy-MM"),
    DATE("yyyy-MM-dd"),
    DATE_SHORT("yyyyMMdd"),
    DATETIME_SHORT("yyyyMMddHHmmss"),
    TIMESTAMP_SHORT("yyyyMMddHHmmssSSS"),
    DATETIME_MIN("yyyy-MM-dd HH:mm"),
    DATETIME("yyyy-MM-dd HH:mm:ss"),
    DATETIMEZ("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    TIMESTAMP("yyyy-MM-dd HH:mm:ss.SSS"),
    TIMESTAMPT("yyyy-MM-dd'T'HH:mm:ss.SSS"),
    TIMESTAMPZ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
    ;

    //	19 , which is the number of characters in yyyy-mm-dd hh:mm:ss
//	20 + s , which is the number of characters in the yyyy-mm-dd hh:mm:ss.[fff...] and s represents the scale of the given Timestamp, its fractional seconds precision. 
    private final String format;

}
