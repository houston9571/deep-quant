package com.optimus.service.impl;

import com.optimus.mysql.MybatisBaseServiceImpl;
import com.optimus.mysql.entity.BoardInfo;
import com.optimus.mysql.mapper.BoardInfoMapper;
import com.optimus.service.BoardInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardInfoServiceImpl extends MybatisBaseServiceImpl<BoardInfoMapper, BoardInfo> implements BoardInfoService {

    private final BoardInfoMapper boardInfoMapper;


}
