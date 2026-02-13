package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.vo.DragonStockList;

import java.util.List;

public interface DragonStockService {

    List<DragonStockList> queryDragonPartnerList(String tradeDate);

    Result<List<DragonStock>> getDragonStockList(String date);

}
