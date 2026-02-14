package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.entity.DragonDept;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.vo.DragonStockList;

import java.util.List;

public interface DragonDeptService {

    List<DragonDept> queryDragonDeptList(String tradeDate);

    Result<List<DragonDept>> syncDragonDeptList(String date);

}
