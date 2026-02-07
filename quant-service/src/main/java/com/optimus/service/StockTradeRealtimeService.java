package com.optimus.service;

import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockTradeDelay;
import com.optimus.mysql.entity.StockTradeRealTime;

public interface StockTradeRealtimeService extends MybatisBaseService<StockTradeRealTime> {


    Result<StockTradeRealTime> getStockTradeRealtime(String code);


    Result<JSONObject> getFirstRequest2Data(String code);

}
