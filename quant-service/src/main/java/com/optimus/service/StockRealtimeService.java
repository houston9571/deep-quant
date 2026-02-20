package com.optimus.service;

import com.alibaba.fastjson2.JSONObject;
import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.vo.FundsFlowLine;
import com.optimus.mysql.entity.StockTechMin;

import java.util.List;

public interface StockRealtimeService extends MybatisBaseService<StockTechMin> {


    Result<StockTechMin> getStockRealtime(String code);

    Result<List<FundsFlowLine>> getFundsFlowLines(String code);

    Result<JSONObject> getFirstRequest2Data(String code);

}
