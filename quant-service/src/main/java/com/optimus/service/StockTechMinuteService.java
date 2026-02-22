package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockTechDaily;
import com.optimus.mysql.entity.StockTechMinute;

public interface StockTechMinuteService extends MybatisBaseService<StockTechMinute> {

    void   calcMinuteIndicatorAndSave(String stockCode);




}
