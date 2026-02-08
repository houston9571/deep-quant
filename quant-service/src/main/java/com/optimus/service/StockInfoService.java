package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockInfo;

public interface StockInfoService extends MybatisBaseService<StockInfo> {

    Result<Void> getStockInfo(String code);

    Result<Void> getStockBoardList(String code);


}
