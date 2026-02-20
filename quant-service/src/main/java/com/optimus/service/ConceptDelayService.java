package com.optimus.service;

import com.optimus.mysql.MybatisBaseService;
import com.optimus.mysql.entity.ConceptDelay;

import java.util.List;

public interface ConceptDelayService extends MybatisBaseService<ConceptDelay> {


     List<List<ConceptDelay>> queryConceptTradeList(int days, int top);

     void syncConceptTradeList();




}
