package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockKlineDaily;

import java.util.List;

public interface StockKlineDailyService extends MybatisBaseService<StockKlineDaily> {

    List<StockKlineDaily>  syncStockTradeList();




}
