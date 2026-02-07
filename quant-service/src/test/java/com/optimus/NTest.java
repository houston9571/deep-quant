package com.optimus;

import com.google.common.collect.Maps;
import com.optimus.constants.StockCodeUtils;
import org.junit.Test;

import java.util.Map;

public class NTest   {


    @Test
    public void test() {
        String s = StockCodeUtils.buildFields(300);
        System.out.println("--->" + s);


    }

    @Test
    public void testGenId() {
        for (int i = 0; i < 100; i++) {
        }
    }


}