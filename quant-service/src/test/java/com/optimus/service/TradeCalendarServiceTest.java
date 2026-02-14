package com.optimus.service;

import com.optimus.constants.StockCodeUtils;
import com.optimus.utils.DateUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;

import static com.optimus.enums.DateFormatEnum.DATE;
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
        LocalDate end = DateUtils.parseLocalDate("2025-10-01", DATE);
        LocalDate from = DateUtils.parseLocalDate("2026-02-11", DATE);
        while (from.isAfter(end)) {
            if (from.getDayOfWeek().getValue() < 6) {
                System.out.println(DateUtils.format(from, DATE) + " " + from.getDayOfWeek().getDisplayName(SHORT, SIMPLIFIED_CHINESE));
            }
            from = from.plusDays(-1);
        }

    }
}
