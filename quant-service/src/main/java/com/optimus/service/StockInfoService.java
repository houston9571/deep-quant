package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockInfo;

public interface StockInfoService extends MybatisBaseService<StockInfo> {

    Result<StockInfo> getStockInfo(String code);

    Result<Void> getStockBoardList(String code);


}
