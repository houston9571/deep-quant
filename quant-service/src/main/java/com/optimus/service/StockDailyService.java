package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockDaily;

import java.util.List;

public interface StockDailyService extends MybatisBaseService<StockDaily> {

    List<StockDaily>  syncStockTradeList();




}
