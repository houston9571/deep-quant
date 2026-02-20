package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.TradeCalendar;

public interface TradeCalendarService extends MybatisBaseService<TradeCalendar> {

    int genYearCalendar();

}