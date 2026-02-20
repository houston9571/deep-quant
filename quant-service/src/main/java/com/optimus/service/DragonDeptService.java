package com.optimus.service;

import com.optimus.base.Result;
import com.optimus.mysql.entity.DragonDept;

import java.util.List;

public interface DragonDeptService {


    Result<List<DragonDept>> syncDragonDeptList(String date);

}
