package com.optimus.service;

import com.optimus.constants.StockCodeUtils;
import com.optimus.utils.DateUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.SIMPLIFIED_CHINESE;

public class TradeCalendarServiceTest {


    @Test
    public void test() {

        System.out.println("--->" + DateUtils.now().plusDays(7).getDayOfWeek().getDisplayName(SHORT, SIMPLIFIED_CHINESE));

        MathContext mc = new MathContext(4, HALF_UP);
        System.out.println(BigDecimal.valueOf(4375378432L).divide(BigDecimal.valueOf(61495128151L), mc));


    }

    @Test
    public void test1() {
        String[] s = "深科技 特发信息 粤桂股份 国风新材 欢瑞世纪 博纳影业 天奇股份 巨力索具 百川股份 二六三 通鼎互联 汉缆股份 银河电子    浙江世宝 ".split("\\s+");
        for (int i = 0; i < s.length; i++) {

            System.out.println(s[i]);
        }
    }
}
