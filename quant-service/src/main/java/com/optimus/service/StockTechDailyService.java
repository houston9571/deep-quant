package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockKlineDaily;
import com.optimus.mysql.entity.StockTechDaily;

import java.util.List;

public interface StockTechDailyService extends MybatisBaseService<StockTechDaily> {

    void calculateDailyIndicatorAndSave(List<StockKlineDaily> barList);


}
