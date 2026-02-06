package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockFundsFlow;

public interface StockFundsFlowService extends MybatisBaseService<StockFundsFlow> {


    Result<Void> getStockFundsFlow(String code);

}
