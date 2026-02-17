package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.vo.DragonStockList;

import java.util.List;

public interface DragonStockService {

    List<DragonStockList> queryDragonStockList(String tradeDate);

    List<DragonStockList> queryDragonStockDetail(String code);

    Result<List<DragonStock>> syncDragonStockList(String date);

}
