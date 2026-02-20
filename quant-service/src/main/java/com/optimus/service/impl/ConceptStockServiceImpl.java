package com.optimus.service.impl;

import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.ConceptStock;
import com.optimus.mysql.mapper.ConceptStockMapper;
import com.optimus.service.ConceptStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConceptStockServiceImpl extends MybatisBaseServiceImpl<ConceptStockMapper, ConceptStock> implements ConceptStockService {

    private final ConceptStockMapper conceptStockMapper;


}
