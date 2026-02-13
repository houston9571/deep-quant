package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockDragon;
import com.optimus.mysql.entity.StockInfo;
import com.optimus.mysql.vo.StockDragonList;

import java.util.List;

public interface StockDragonService  {

    List<StockDragonList> queryPartnerDragonList(String tradeDate);

    Result<List<StockDragon>> getStockDragonList(String date);

}
