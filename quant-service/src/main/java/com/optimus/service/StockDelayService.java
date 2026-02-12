package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockDelay;

import java.util.List;

public interface StockDelayService extends MybatisBaseService<StockDelay> {

    List<StockDelay>  syncStockTradeList();




}
