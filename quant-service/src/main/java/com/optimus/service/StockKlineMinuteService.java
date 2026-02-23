package com.optimus.service;

import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockKlineMinute;

public interface StockKlineMinuteService extends MybatisBaseService<StockKlineMinute> {

    void syncStockKlineMinutePools();

    Result<StockKlineMinute> syncStockKlineMinute(String stockCode);


    Result<JSONObject> getFirstRequest2Data(String code);

}
