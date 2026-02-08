package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockTradeDelay;

public interface StockTradeDelayService extends MybatisBaseService<StockTradeDelay> {

    Result<Void> syncStockTradeList();




}
