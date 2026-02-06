package com.optimus.service;

import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockTrade;

public interface StockTradeService extends MybatisBaseService<StockTrade> {


    Result<Void> getStockTrade(String code);



    Result<JSONObject> getFirstRequest2Data(String code);

}
