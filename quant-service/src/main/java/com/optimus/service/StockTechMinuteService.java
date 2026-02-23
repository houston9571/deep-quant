package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockKlineMinute;
import com.optimus.mysql.entity.StockTechDaily;
import com.optimus.mysql.entity.StockTechMinute;

import java.util.List;

public interface StockTechMinuteService extends MybatisBaseService<StockTechMinute> {

    void   calcMinuteIndicatorAndSave(List<StockKlineMinute> last10);




}
