package com.optimus.service;

import com.optimus.mysql.entity.DragonStockDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DragonStockDetailService {


    List<List<DragonStockDetail>> queryDragonStockDetailWithPartner(String tradeDate);

    int syncDragonStockDetail(LocalDate date, String code, String name) ;

}
