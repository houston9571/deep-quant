package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.StockDragon;
import com.optimus.mysql.entity.StockInfo;

import java.util.List;

public interface StockDragonService  {


    Result<List<StockDragon>> getStockDragonList(String date);

}
