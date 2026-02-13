package com.optimus.service.impl;

import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.BoardStock;
import com.optimus.mysql.mapper.BoardStockMapper;
import com.optimus.service.BoardStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardStockServiceImpl extends MybatisBaseServiceImpl<BoardStockMapper, BoardStock> implements BoardStockService {

    private final BoardStockMapper boardStockMapper;


}
