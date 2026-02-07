package com.optimus.service;

import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockTradeDelay;
import com.optimus.mysql.entity.StockTradeRealTime;

public interface StockTradeDelayService extends MybatisBaseService<StockTradeDelay> {

    Result<Void> syncStockTradeList();




}
