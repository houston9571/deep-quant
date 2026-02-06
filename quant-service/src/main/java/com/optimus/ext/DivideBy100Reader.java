package com.optimus.ext;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;

import java.lang.reflect.Type;

public class DivideBy100Reader implements ObjectReader<Double> {
    @Override
    public Double readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        // 读取原始数值
        Number rawValue = jsonReader.readNumber();
        if (rawValue == null) return null;
        // 除以100
        return rawValue.doubleValue() / 100.0;
    }
}
