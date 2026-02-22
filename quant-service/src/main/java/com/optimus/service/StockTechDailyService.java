package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockTechDaily;

public interface StockTechDailyService extends MybatisBaseService<StockTechDaily> {

    void   calculateTechAndSave(String stockCode);




}
