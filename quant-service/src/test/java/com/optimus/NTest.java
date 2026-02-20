package com.optimus;

import com.optimus.components.StockCodeUtils;
import org.junit.Test;

public class NTest   {


    @Test
    public void test() {
        String s = StockCodeUtils.buildFields(300);
        System.out.println("--->" + s);


    }

    @Test
    public void testGenId() {
        System.out.println(1200%1000);
    }


}