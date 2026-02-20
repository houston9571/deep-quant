package com.optimus.service.impl;

import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.TradeCalendar;
import com.optimus.mysql.mapper.TradeCalendarMapper;
import com.optimus.service.TradeCalendarService;
import com.optimus.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.optimus.constant.Constants.DISABLED;
import static com.optimus.constant.Constants.ENABLE;
import static com.optimus.enums.DateFormatEnum.DATE;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.SIMPLIFIED_CHINESE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeCalendarServiceImpl extends MybatisBaseServiceImpl<TradeCalendarMapper, TradeCalendar> implements TradeCalendarService {


    private final TradeCalendarMapper tradeCalendarMapper;

    public int genYearCalendar() {
        LocalDate b = DateUtils.parseLocalDate(DateUtils.now().getYear() + "-01-01", DATE);
        LocalDate e = DateUtils.parseLocalDate((DateUtils.now().getYear() + 1) + "-01-01", DATE);
        List<TradeCalendar> list = new ArrayList<>(366);
        while (b.isBefore(e)) {
            list.add(TradeCalendar.builder()
                    .date(b)
                    .week(b.getDayOfWeek().getDisplayName(SHORT, SIMPLIFIED_CHINESE))
                    .isTrade(b.getDayOfWeek().getValue() <= 5 ? ENABLE : DISABLED)
                    .build());
            b = b.plusDays(1);
        }
       return saveOrUpdateBatch(list, new String[]{"date"});
    }


}
