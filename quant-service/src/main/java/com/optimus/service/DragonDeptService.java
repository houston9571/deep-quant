package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.entity.DragonDept;
import com.optimus.mysql.entity.DragonStock;
import com.optimus.mysql.vo.DragonDeptDto;
import com.optimus.mysql.vo.DragonStockList;

import java.util.List;

public interface DragonDeptService {


    Result<List<DragonDept>> syncDragonDeptList(String date);

}
