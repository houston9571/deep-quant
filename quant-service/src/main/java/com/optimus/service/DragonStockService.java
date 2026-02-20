package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.vo.DragonDetailPartner;
import com.optimus.mysql.vo.DragonDetailStock;

import java.util.List;

public interface DragonStockService {

    List<DragonDetailStock> queryDragonStockList(String tradeDate);

    List<DragonDetailStock> queryDragonStockDetail(String stockCode);

    List<DragonDetailPartner> queryDragonPartnerDetail(String partnerCode);

    Result<List<DragonStock>> syncDragonStockList(String date);

}
