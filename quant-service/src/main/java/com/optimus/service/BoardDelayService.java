package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.BoardDelay;

import java.util.List;

public interface BoardDelayService extends MybatisBaseService<BoardDelay> {


     List<List<BoardDelay>> queryBoardTradeList(int days, int top);

     void syncBoardTradeList();




}
