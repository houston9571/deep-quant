package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.entity.StockDragon;
import com.optimus.mysql.entity.StockDragonDetail;

import java.time.LocalDate;
import java.util.List;

public interface StockDragonDetailService {


    int getStockDragonDetail(LocalDate date, String code, String name) ;

}
