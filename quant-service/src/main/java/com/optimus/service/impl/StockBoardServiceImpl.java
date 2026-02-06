package com.optimus.service.impl;

import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.StockBoard;
import com.optimus.mysql.mapper.StockBoardMapper;
import com.optimus.service.StockBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockBoardServiceImpl extends MybatisBaseServiceImpl<StockBoardMapper, StockBoard> implements StockBoardService {

    private final StockBoardMapper stockBoardMapper;


}
